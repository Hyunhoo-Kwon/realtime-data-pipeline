package com.study.kafkasparkesspring.service;

import com.study.kafkasparkesspring.domain.Document;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Date;

public class StreamService {

    public static JavaDStream<String> getValueStream(JavaInputDStream<ConsumerRecord<String, String>> stream) {
        return stream
                .map(record -> record.value());
    }

    public static JavaPairDStream<String, Document> getDocumentStream(JavaDStream<String> valueStream) {
        return valueStream
                .flatMap(x -> Arrays.asList(x.split("\\s+")).iterator())
                .mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2)
                .mapToPair(pair -> getDocumentTuple(pair));
    }

    private static Tuple2<String, Document> getDocumentTuple(Tuple2<String, Integer> pair) {
        String key = pair._1;
        int count = pair._2;
        return new Tuple2<>(key, new Document(key, new Date(), count));
    }

}
