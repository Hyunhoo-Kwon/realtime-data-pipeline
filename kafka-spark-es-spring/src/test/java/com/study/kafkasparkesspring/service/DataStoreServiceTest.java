package com.study.kafkasparkesspring.service;

import com.study.kafkasparkesspring.domain.Document;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class DataStoreServiceTest {

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

    // local elasticsearch 실행 후 테스트 수행. Todo. Mock 적용
    @Test
    @Ignore
    public void saveDocumentStream() throws Exception {
        // local 데이터 insert 확인: http://localhost:9200/spark/_doc/This
        JavaDStream<String> stream = createInputStream();
        JavaPairDStream<String, Document> documentStream = StreamService.getDocumentStream(stream);
        documentStream.print();

        String esNodes = "localhost";
        String esPort = "9200";
        String esIndex = "spark";
        DataStoreService.saveDocumentStream(documentStream, esNodes, esPort, esIndex);

        streamingContext.start();
        streamingContext.awaitTerminationOrTimeout(10000);
    }

    private JavaDStream<String> createInputStream() throws Exception {
        String message = "This is a message";
        List<String> list = new ArrayList<>();
        list.add(message);
        JavaRDD<String> rdd = streamingContext.sparkContext().parallelize(list);

        Queue<JavaRDD<String>> queue = new LinkedList<>();
        queue.add(rdd);
        return streamingContext.queueStream(queue);
    }

}
