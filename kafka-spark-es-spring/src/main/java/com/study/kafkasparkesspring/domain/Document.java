package com.study.kafkasparkesspring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document implements Serializable {

    private String doc_id;
    private Date timestamp;
    private Integer count;

}
