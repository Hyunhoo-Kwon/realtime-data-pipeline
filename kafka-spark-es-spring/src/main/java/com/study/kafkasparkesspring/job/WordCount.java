package com.study.kafkasparkesspring.job;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.elasticsearch.spark.streaming.api.java.JavaEsSparkStreaming;
import org.spark_project.guava.collect.ImmutableMap;
import scala.Tuple2;

import java.util.*;

public class WordCount {

    public void streamWordCount() {
        try {
            SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
            JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));

            JavaRDD<String> rdd = streamingContext.sparkContext().parallelize(Arrays.asList("This is a message", "This is another message"));
            java.util.Queue<JavaRDD<String>> queue = new LinkedList<JavaRDD<String>>();
            queue.add(rdd);
            JavaDStream<String> lines = streamingContext.queueStream(queue);
            JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split("\\s+")).iterator());
            JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1)).reduceByKey((i1, i2) -> i1 + i2);
            JavaPairDStream<String, ImmutableMap<String, Integer>> wordCountsMap = wordCounts.mapToPair(tuple2 -> new Tuple2<>(tuple2._1, ImmutableMap.of("count", tuple2._2)));

            // results.print();
            wordCountsMap.print();

            Map<String, String> esConf = new HashMap<>();
            esConf.put("es.index.auto.create", "true");
            esConf.put("es.nodes", "localhost");
            esConf.put("es.port", "9200");
            JavaEsSparkStreaming.saveToEsWithMeta(wordCountsMap, "spark", esConf);

            streamingContext.start();
            streamingContext.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
