package com.eoi.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author lemon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    private String jobId;
    private String jobName;
    private String jobStatus;
    private Source source;
    private Sink sink;
    private Statistics statistics;
}
