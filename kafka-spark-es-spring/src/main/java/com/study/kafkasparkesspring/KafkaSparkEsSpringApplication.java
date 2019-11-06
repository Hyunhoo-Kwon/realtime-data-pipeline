package com.study.kafkasparkesspring;

import com.study.kafkasparkesspring.job.WordCount;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KafkaSparkEsSpringApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(KafkaSparkEsSpringApplication.class, args);
        WordCount wordCount = applicationContext.getBean(WordCount.class);
        wordCount.streamWordCount();
    }

}
