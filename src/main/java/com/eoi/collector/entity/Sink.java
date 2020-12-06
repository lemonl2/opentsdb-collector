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
public class Sink {
    private String bootstrapServers;
    private String topic;
    private String acks = "1";
    private Integer retries = 1;
    private Integer batchSize = 16384;

    private Long lingerMs = 5L;
    private Long bufferMemory = 33554432L;
}
