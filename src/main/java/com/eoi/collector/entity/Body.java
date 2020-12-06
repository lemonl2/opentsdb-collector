package com.eoi.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lemon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Body {
    private Long start;
    private Long end;
    private String showTSUIDs;
    private String msResolution;
    private List<Query> queries;
}
