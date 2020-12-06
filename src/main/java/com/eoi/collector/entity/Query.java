package com.eoi.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author lemon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Query {
    private Long start;
    private Long end;
    private String aggregator;
    private String metric;
    private Map<String, String> tags;
    private Statistics statistics;
}
