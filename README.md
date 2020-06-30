# realtime-data-pipeline
스트리밍 데이터 실시간 분석 with Java

## 데이터 파이프라인 아키텍처
### 람다 아키텍처 (Lambda Architecture)
#### 1. 람다 아키텍처란
- 람다 아키텍처는 주기적으로 배치 처리를 수행하는 전통적인 아키텍처에 짧은 지연 시간을 제공하는 스트림 처리 기반의 스피드 계층을 추가한 데이터 아키텍처

#### 2. 람다 아키텍처 구조
<img width="600" alt="출처: https://www.oreilly.com/library/view/stream-processing-with/9781491974285/assets/spaf_0107.png" src="https://www.oreilly.com/library/view/stream-processing-with/9781491974285/assets/spaf_0107.png">

- 람다 아키텍처 구조
  - 시스템의 모든 데이터는 스트림 처리기와 배치 저장소로 데이터 전송
  - 스트림 처기리는 실시간으로 연산을 수행해 이를 스피드 테이블에 저장
  - 배치 처리기는 주기적으로 배치 저장소에 있는 데이터를 처리하고 정확한 결과를 배치 테이블에 쓰고, 관련 있는 스피드 테이블의 부정확한 결과는 버린다
  - 애플리케이션은 스피드 테이블의 결과와 배치 테이블의 결과를 병합하여 조회한다
  
- 람다 아키텍처의 목표와 한계
  - 목표: 람다 아키텍처의 목표는 배치 분석 아키텍처 결과의 늦은 지연 시간을 보완하기 위한 것이다
  - 한계:
    - 서로 다른 이벤트 처리 시스템이 각각의 API를 이용해 의미적으로 동일한 애플리케이션을 두 벌 구현해야 한다
    - 람다 아키텍처를 설치하고 유지 보수하기가 어렵다

***

## 데이터 분석 플랫폼
### 데이터 분석 플랫폼 종류
#### 1. 메세지 큐
- Kafka
- RabbitMQ

#### 2. 실시간 처리 프레임워크
- Flink
- [Spark Streaming](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/wiki/Spark-Streaming)

#### 3. 저장소
- Elasticsearch

### 데이터 분석 플랫폼 구조
![출처: https://www.niceideas.ch/roller2/badtrash/entry/lambda-architecture-with-kafka-elasticsearch](https://www.niceideas.ch/roller2/badtrash/mediaresource/f36e9948-0053-4f07-a0ff-a1ebca0b5762)
#### 1. Speed Layer
1. Kafka + Spark Streaming + Elasticsearch 예제
    1. [Kafka + Spark Streaming + Elasticsearch with Java](https://github.com/Hyunhoo-Kwon/realtime-data-pipeline/tree/master/kafka-spark-es-java)
2. RabbitMQ + Spark Streaming + Elasticsearch 예제
    1. RabbitMQ + Spark Streaming + Elasticsearch with Java
#### 2. Batch Layer

## 참고
- http://lambda-architecture.net
- https://www.oreilly.com/library/view/stream-processing-with/9781491974285/ch01.html#fig-lambda-arch
- https://www.niceideas.ch/roller2/badtrash/entry/lambda-architecture-with-kafka-elasticsearch
