package com.study.kfakasparkesjava;

import com.study.kfakasparkesjava.job.WordCount;

public class WordCountApplication {

    public static void main(String[] args) {
        WordCount wordCount = new WordCount();
        wordCount.streamWordCount();
    }
}
