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
public class Metric {
    private String metric;
    private Long timestamp;
    private Double value;
    private String[] tsuids;
    private Map<String, String> tags;
}
