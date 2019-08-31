# Kafka + Spark Streaming + Elasticsearch with Java

## 1. 로컬 테스트 환경 설정
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
