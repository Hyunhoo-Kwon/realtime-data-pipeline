package com.study.kafkasparkesspring;

import com.study.kafkasparkesspring.job.WordCount;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaSparkEsSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSparkEsSpringApplication.class, args);
        WordCount wordCount = new WordCount();
        wordCount.streamWordCount();
    }

}
