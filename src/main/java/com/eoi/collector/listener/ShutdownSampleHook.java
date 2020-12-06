package com.eoi.collector.listener;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import com.alibaba.fastjson.JSON;
import com.eoi.collector.common.Threads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * @author lemon
 */
@Slf4j
public class ShutdownSampleHook extends Thread {
    @Override
    public void run() {
        // 执行收尾工作
        log.info("do something on shutdown hook");
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String iniUrl = jarFile.getParentFile().toString() + "/jobConfig.json";
        if (!FileUtil.exist(iniUrl)) {
            FileUtil.touch(iniUrl);
        }
        FileWriter writer = new FileWriter(iniUrl);
        writer.write(JSON.toJSONString(Threads.JobConfig, true));
        log.info("do something on shutdown hook end");
    }
}
