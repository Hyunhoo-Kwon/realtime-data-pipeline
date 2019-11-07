# Kafka + Spark Streaming + Elasticsearch with Spring boot (+JUnit test)
This tutorial will use:

- Java8
- Spring Boot 2.1.7.RELEASE
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
- [build.gradle](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/blob/master/kafka-spark-es-spring/build.gradle)
```
configurations.compile {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
}

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
- spark-core > slf4j 와 dependency 충돌 방지를 위해 spring-boot-starter-logging 제외
