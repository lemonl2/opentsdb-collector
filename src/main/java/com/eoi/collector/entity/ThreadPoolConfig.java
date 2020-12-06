package com.eoi.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lemon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadPoolConfig {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer workQueue;
}
