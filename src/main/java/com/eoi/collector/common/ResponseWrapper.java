package com.eoi.collector.common;


import lombok.Builder;
import lombok.Data;

/**
 * @author lemon
 */
@Data
@Builder
public class ResponseWrapper<T> {
    private String code;
    private String msg;
    private T entity;
    private Integer total;
}
