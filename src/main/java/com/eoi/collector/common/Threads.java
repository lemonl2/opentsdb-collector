package com.eoi.collector.common;

import com.eoi.collector.entity.Job;
import com.eoi.collector.task.OpenTsdbTask;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lemon
 */
public class Threads {
    public static volatile ConcurrentHashMap<String, Job> JobConfig = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String, OpenTsdbTask> JobThreads = new ConcurrentHashMap<>();
}
