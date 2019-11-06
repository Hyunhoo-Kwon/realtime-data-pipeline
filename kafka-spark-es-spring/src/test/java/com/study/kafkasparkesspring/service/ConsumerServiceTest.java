package com.study.kafkasparkesspring.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import scala.Tuple2;

import static org.junit.Assert.*;

public class ConsumerServiceTest {

    private static JavaStreamingContext streamingContext;

    @Before
    public void setStreamingContext() {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
        streamingContext = new JavaStreamingContext(conf, Durations.seconds(5));
    }

    @After
    public void stopStreamingContext() {
        streamingContext.stop();
    }

    // local kafka producer 실행 후 테스트 수행. Todo. Mock 적용
    @Test
    @Ignore
    public void getConsumerRecordStream() throws Exception {
        JavaInputDStream<ConsumerRecord<String, String>> stream = ConsumerService.getConsumerRecordStream(streamingContext);
        JavaPairDStream<String, String> results = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        results.print();

        streamingContext.start();
        streamingContext.awaitTerminationOrTimeout(10000);
    }

}
