package com.study.kafkasparkesspring.job;

import com.study.kafkasparkesspring.domain.Document;
import com.study.kafkasparkesspring.service.ConsumerService;
import com.study.kafkasparkesspring.service.DataStoreService;
import com.study.kafkasparkesspring.service.StreamService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WordCount {

    @Value("${spark.app.name}")
    private String sparkAppName;

    @Value("${spark.master}")
    private String sparkMaster;

    @Value("${es.nodes}")
    private String esNodes;

    @Value("${es.port}")
    private String esPort;

    @Value("${es.index}")
    private String esIndex;

    public void streamWordCount() {
        try {
            SparkConf conf = new SparkConf().setMaster(sparkMaster).setAppName(sparkAppName);
            JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));

            JavaInputDStream<ConsumerRecord<String, String>> stream = ConsumerService.getConsumerRecordStream(streamingContext);
            JavaDStream<String> valueStream = StreamService.getValueStream(stream);
            JavaPairDStream<String, Document> documentStream = StreamService.getDocumentStream(valueStream);
            DataStoreService.saveDocumentStream(documentStream, esNodes, esPort, esIndex);

            streamingContext.start();
            streamingContext.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
