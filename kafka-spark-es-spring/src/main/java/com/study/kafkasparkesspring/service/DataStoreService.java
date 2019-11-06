package com.study.kafkasparkesspring.service;

import com.study.kafkasparkesspring.domain.Document;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.elasticsearch.spark.streaming.api.java.JavaEsSparkStreaming;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataStoreService {

    public static void saveDocumentStream(JavaPairDStream<String, Document> documentStream, String esNodes, String esPort, String esIndex) {
        Map<String, String> esConf = new HashMap<>();
        esConf.put("es.index.auto.create", "true");
        esConf.put("es.nodes", esNodes);
        esConf.put("es.port", esPort);
        JavaEsSparkStreaming.saveToEsWithMeta(documentStream, esIndex, esConf);
    }

}
