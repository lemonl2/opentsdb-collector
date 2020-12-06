package com.eoi.collector.task;

import com.eoi.collector.common.Threads;
import com.eoi.collector.common.JobStatus;
import com.eoi.collector.entity.*;
import com.eoi.collector.sink.KafkaSink;
import com.eoi.collector.source.OpenTsdbSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author lemon
 */
public class OpenTsdbTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(OpenTsdbTask.class);

    Job job;
    OpenTsdbSource openTsdbSource;
    KafkaSink kafkaSink;

    public OpenTsdbTask(Job job, OpenTsdbSource openTsdbSource, KafkaSink kafkaSink) {
        this.job = job;
        this.openTsdbSource = openTsdbSource;
        this.kafkaSink = kafkaSink;
    }

    @Override
    public void run() {
        String jobId = job.getJobId();
        Source source = job.getSource();
        Body body = source.getBody();
        String apiUrl = source.getApiUrl();
        while (JobStatus.running.equals(job.getJobStatus())) {
            try {
                subQueryTask(jobId, apiUrl, body);
                // 默认阻塞5秒钟
                Thread.sleep(5000L);
            } catch (Exception e) {
                logger.error("执行任务jobId:{}, 出错:{}", jobId, e.getMessage());
            }
        }
    }

    private void subQueryTask(String jobId, String apiUrl, Body body) {
        body.getQueries().forEach(x-> {
            Body subReq = new Body();
            subReq.setMsResolution(body.getMsResolution());
            subReq.setShowTSUIDs(body.getShowTSUIDs());
            subReq.setQueries(Collections.singletonList(x));
            Long now = System.currentTimeMillis();
            Long start = x.getStart();
            // 如果采集开始时间在2小时之前，就使用滚动窗口，间隔为10分钟
            // 否则使用start ~ now作为时间范围
            if (start < now - 2 * 60 * 60 * 1000) {
                // 每次滚动窗口 10 分钟，防止开始时间太小，采集的数据量太大
                // start ~ (start + 10分钟)作为时间范围
                x.setEnd(start + 10 * 60 * 1000);
            } else {
                // 如果采集开始时间在2小时之内，则把当前时间设置为结束时间end
                x.setEnd(now);
            }

            // 设置到body中用于查询使用
            subReq.setStart(x.getStart());
            subReq.setEnd(x.getEnd());

            List<Metric> list = openTsdbSource.getSource(apiUrl, subReq);
            Statistics statistics = Threads.JobConfig.get(jobId).getStatistics();
            statistics.setInputCount(statistics.getInputCount() + list.size());
            if (list.size() > 0) {
                Statistics xStatistics = x.getStatistics();
                // 记录每一个指标采集量
                xStatistics.setInputCount(xStatistics.getInputCount() + list.size());
                // 输出到kafka
                kafkaSink.sink(job.getSink(), list);
                // 记录每个指标的输出量
                xStatistics.setOutputCount(xStatistics.getOutputCount() + list.size());
                logger.info("api:{}, metric:{}, 采集到数据:{}", apiUrl, x.getMetric(), list.size());
            }
            if (start < now - 2 * 60 * 60 * 1000) {
                // start在2小时之前，下一个开始时间start就是当前的结束时间end
                x.setStart(x.getEnd() + 1);
            } else {
                // start在2小时之内，那么如果存在数据，就把最后一条数据的时间作为下一次的开始时间start
                // 如果没有数据，则不更新开始时间
                if (list.size()>0) {
                    x.setStart(list.get(list.size()-1).getTimestamp() + 1);
                }
            }
            statistics.setOutputCount(statistics.getOutputCount() + list.size());
        });
    }
}
