package com.study.kfakasparkesjava.job;

import org.junit.Test;

import static org.junit.Assert.*;

public class WordCountTest {

    @Test
    public void streamWordCount() throws Exception {
        WordCount wordCount = new WordCount();
        wordCount.streamWordCount();
    }

}
