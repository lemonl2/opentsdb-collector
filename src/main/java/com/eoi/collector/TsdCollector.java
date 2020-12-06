package com.eoi.collector;

import com.eoi.collector.listener.ShutdownSampleHook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lemon
 */
@SpringBootApplication
@EnableScheduling
public class TsdCollector {
    public static void main(String[] args) {
        //注册一个 ShutdownHook
        ShutdownSampleHook thread=new ShutdownSampleHook();
        Runtime.getRuntime().addShutdownHook(thread);
        SpringApplication.run(TsdCollector.class);
    }
}
