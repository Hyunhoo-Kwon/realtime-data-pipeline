# Kafka + Spark Streaming + Elasticsearch with Java
This tutorial will use:

- Java8
- Gradle
- Kafka
- Spark Stremaing
- Elasticsearch

## 로컬 테스트 환경 설정
### Install
#### kafka
- Version: 2.2.0 release
- Quickstart: https://kafka.apache.org/quickstart

#### Spark
- Version: 2.4.3
- Quickstart: https://spark.apache.org/docs/latest/quick-start.html

#### Elasticsearch
- Version: 7.2.0
- Quickstart: https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html
- install with Homebrew: https://www.elastic.co/guide/en/elasticsearch/reference/current/brew.html
  ```
  brew tap elastic/tap
  brew install elastic/tap/elasticsearch-full
  -- home location: /usr/local/var/homebrew/linked/elasticsearch-full
  ```

### Start server
#### kafka
1. Start the server
    - Start single-node ZooKeeper instance
    ```
    bin/zookeeper-server-start.sh config/zookeeper.properties
    ```
    - Start Kafka server
    ```
    bin/kafka-server-start.sh config/server.properties
    ```
2. Create a topic
    - Create topic named 'test'
    ```
    bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
    ```
    - See topic list
    ```
    bin/kafka-topics.sh --list --bootstrap-server localhost:9092
    ```
3. Send some messages
    - Run procedure (Command line client)
    ```
    // key 옵션: --property "parse.key=true" --property "key.separator=:"
    
    bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test --property "parse.key=true" --property "key.separator=:"
    > key1:This is a message
    > key2:This is another message
    ```

#### Elasticsearch
1. Start the server
    ```
    ./bin/elasticsearch
    ```

## 코드 구현
### dependency
- [build.gradle](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/blob/master/kafka-spark-es-java/build.gradle)
```
dependencies {
    ...
    compile group: 'org.apache.spark', name: 'spark-core_2.11', version: '2.4.3'
    compile group: 'org.apache.spark', name: 'spark-streaming_2.11', version: '2.4.3'
    compile group: 'org.apache.spark', name: 'spark-streaming-kafka-0-10_2.11', version: '2.4.3'
    compile group: 'org.elasticsearch', name: 'elasticsearch-spark-20_2.11', version: '7.2.0'
}
```
- spark-stremaing & spark-kafka: https://spark.apache.org/docs/latest/streaming-programming-guide.html#linking
- elasticsearch-spark: https://www.elastic.co/guide/en/elasticsearch/hadoop/current/install.html#install
> spark 2.0+ 버전과 호환을 위해 elasticsearch-spark-20_ 사용. Scala 버전은 2.11로 통일. (spark-core와 scala 버전 다를 경우 dependency 충돌 발생)

#### 1. Getting JavaStreamingContext
```
// Create a local StreamingContext with two working thread and batch interval of 10 second
SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));
```
- Spark Streaming Programming Guide: https://spark.apache.org/docs/latest/streaming-programming-guide.html

#### 2. Getting DStream from Kafka
```
Map<String, Object> kafkaParams = new HashMap<>();
kafkaParams.put("bootstrap.servers", "localhost:9092");
kafkaParams.put("key.deserializer", StringDeserializer.class);
kafkaParams.put("value.deserializer", StringDeserializer.class);
kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
kafkaParams.put("auto.offset.reset", "latest");
kafkaParams.put("enable.auto.commit", true);

Collection<String> topics = Arrays.asList("test");

JavaInputDStream<ConsumerRecord<String, String>> stream =
        KafkaUtils.createDirectStream(streamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));
```
- streaming-kafka-integration: https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html
> consumer group의 최신 오프셋을 선택하며 오프셋에 대한 자동 커밋을 실행. 애플리케이션의 실행 중인 상태와 관계없이 모든 메시지를 사용하고 이미 게시된 메시지를 관리하려면 적절한 오프셋 구성 필요.

#### 3. Processing Obtained DStream
```
JavaPairDStream<String, String> results = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
JavaDStream<String> lines = results.map(tuple2 -> tuple2._2());
JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split("\\s+")).iterator());
JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1)).reduceByKey((i1, i2) -> i1 + i2);
```

#### 4. Persisting Processed DStream into Elasticsearch
```
JavaPairDStream<String, ImmutableMap<String, Integer>> wordCountsMap = wordCounts.mapToPair(tuple2 -> new Tuple2<>(tuple2._1, ImmutableMap.of("count", tuple2._2)));

Map<String, String> esConf = new HashMap<>();
esConf.put("es.index.auto.create", "true");
esConf.put("es.nodes", "localhost");
esConf.put("es.port", "9200");
JavaEsSparkStreaming.saveToEsWithMeta(wordCountsMap, "spark", esConf);
```
- elasticsearch-spark support: https://www.elastic.co/guide/en/elasticsearch/hadoop/master/spark.html
- elasticsearch-hadoop configuration: https://www.elastic.co/guide/en/elasticsearch/hadoop/master/configuration.html
> Map 또는 JavaBean인 RDD를 elasticsearch에 저장

#### 5. Running the Application
```
streamingContext.start();
streamingContext.awaitTermination();
```

## 배포
#### 1. uber jar
- Shadow documentation: https://imperceptiblethoughts.com/shadow/introduction/
- [build.gradle](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/blob/master/kafka-spark-es-java/build.gradle)
```
plugins {
    ...
    id 'com.github.johnrengelman.shadow' version '5.1.0'
}

...

shadowJar {
    zip64 true
    manifest {
        attributes(
                'Main-Class': "com.study.kfakasparkesjava.WordCountApplication"
        )
    }
}
```
> uber jar 생성을 위해 shadowJar 플러그인 설정
```
./gradlew shadowJar
```
> build/libs/kafka-spark-es-java-1.0-SNAPSHOT-all.jar 생성

#### spark submit
- Spark deploying: https://spark.apache.org/docs/latest/cluster-overview.html
1. standalone 클러스터 실행
```
./bin/spark-submit \
--master local[2] \
realtime-data-pipeline/kafka-spark-es-spring/build/libs/kafka-spark-es-spring-0.0.1-SNAPSHOT-all.jar
```
2. spark admin page
    - http://localhost:4040

## 참고 자료
- kafka-spark 예제: https://www.baeldung.com/kafka-spark-data-pipeline
