package com.eoi.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lemon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Source {
    private String apiUrl;
    private Body body;
}
