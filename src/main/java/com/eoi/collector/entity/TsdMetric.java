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
public class TsdMetric {
    private String metric;
    private String[] tsuids;
    private Map<String, String> tags;
    private Map<String, Double> dps;
}
