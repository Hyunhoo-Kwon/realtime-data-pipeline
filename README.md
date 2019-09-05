# realtime-data-pipeline
스트리밍 데이터 실시간 분석 with Java

## 데이터 파이프라인 아키텍처
### 람다 아키텍처 (Lambda Architecture)

#### 1. 람다 아키텍처란
- 람다 아키텍처는 배치 및 스트림 처리 방법을 모두 활용하여 방대한 양의 데이터를 처리하도록 설계된 데이터 아키텍처
- 대용량의 데이터를 배치 처리와 실시간 처리로 대용량의 데이터를 빠르고 안전하게 처리하며 배치 뷰와 실시간 뷰를 조합하여 빠르게 실시간 분석을 할 수 있다

#### 2. 람다 아키텍처 구조
![출처: http://lambda-architecture.net](http://lambda-architecture.net/img/la-overview.png)
1. 시스템의 모든 데이터는 batch layer와 speed layer 모두에 전달
2. batch layer: raw data가 저장되 있고, batch 처리하여 batch view 생성
3. serving layer: batch로 만들어진 데이터 (batch view) 저장
4. speed layer: 실시간 데이터 처리
5. query를 통해 batch view와 realtime-view를 병합하여 조회
- 새로운 데이터는 batch layer와 speed layer에서 동시에 처리된다. batch layer는 주기적으로 집계 작업을 수행하여 batch view를 생성한다. speed layer는 최신 정보를 반영하도록 설계되었으며 batch view에서 아직 생성되지 않은 실시간 데이터 집계를 계산한다.

***

## 데이터 분석 플랫폼 구조
![출처: https://www.niceideas.ch/roller2/badtrash/entry/lambda-architecture-with-kafka-elasticsearch](https://www.niceideas.ch/roller2/badtrash/mediaresource/f36e9948-0053-4f07-a0ff-a1ebca0b5762)
#### 1. Speed Layer
1. Kafka + Spark Streaming + Elasticsearch 예제
    1. [Kafka + Spark Streaming + Elasticsearch with Java](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/tree/master/kafka-spark-es-java)
    2. [Kafka + Spark Streaming + Elasticsearch with Spring boot](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/tree/master/kafka-spark-es-spring)
2. RabbitMQ + Spark Streaming + Elasticsearch 예제
    1. RabbitMQ + Spark Streaming + Elasticsearch with Java
    2. RabbitMQ + Spark Streaming + Elasticsearch with Spring boot
#### 2. Batch Layer

## 참고 자료
- http://lambda-architecture.net
- https://www.niceideas.ch/roller2/badtrash/entry/lambda-architecture-with-kafka-elasticsearch
