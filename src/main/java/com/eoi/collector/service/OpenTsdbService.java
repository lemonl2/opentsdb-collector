package com.eoi.collector.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.eoi.collector.common.JobStatus;
import com.eoi.collector.common.Threads;
import com.eoi.collector.entity.Job;
import com.eoi.collector.entity.Statistics;
import com.eoi.collector.entity.ThreadPoolConfig;
import com.eoi.collector.sink.KafkaSink;
import com.eoi.collector.source.OpenTsdbSource;
import com.eoi.collector.task.OpenTsdbTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lemon
 */
@Service
public class OpenTsdbService {
    private final Logger logger = LoggerFactory.getLogger(OpenTsdbService.class);

    @Value("${spring.threadPool.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Value("${spring.threadPool.keepAliveTime}")
    private Long keepAliveTime;

    @Value("${spring.threadPool.workQueue}")
    private Integer workQueue;

    private static ThreadPoolExecutor executor;

    /**
     * IO密集型任务  = 一般为2*CPU核心数（常出现于线程中：数据库数据交互、文件上传下载、网络数据传输等等）
     * CPU密集型任务 = 一般为CPU核心数+1（常出现于线程中：复杂算法）
     * 混合型任务  = 视机器配置和复杂度自测而定
     */
    private static int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,
     *                           TimeUnit unit,BlockingQueue<Runnable> workQueue)
     * corePoolSize用于指定核心线程数量
     * maximumPoolSize指定最大线程数
     * keepAliveTime和TimeUnit指定线程空闲后的最大存活时间
     * workQueue则是线程池的缓冲队列,还未执行的线程会在队列中等待
     * 监控队列长度，确保队列有界
     * 不当的线程池大小会使得处理速度变慢，稳定性下降，并且导致内存泄露。如果配置的线程过少，则队列会持续变大，消耗过多内存。
     * 而过多的线程又会 由于频繁的上下文切换导致整个系统的速度变缓——殊途而同归。队列的长度至关重要，它必须得是有界的，这样如果线程池不堪重负了它可以暂时拒绝掉新的请求。
     * ExecutorService 默认的实现是一个无界的 LinkedBlockingQueue。
     */

    /**
     * 初始化时加载jobConfig.json配置文件信息到内存
     */
    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(workQueue));
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String iniUrl = jarFile.getParentFile().toString() + "/jobConfig.json";
        if (FileUtil.exist(iniUrl)) {
            FileReader fileReader = new FileReader(iniUrl);
            String result = fileReader.readString();
            Map<String, Object> map =  JSON.parseObject(result, Map.class);
            map.forEach((key, value) -> {
                Job job = JSON.parseObject(JSON.toJSONString(value), Job.class);
                Threads.JobConfig.put(key, job);
            });
            Threads.JobConfig.values().forEach(job-> {
                if (JobStatus.running.equals(job.getJobStatus())) {
                    startJob(job.getJobId());
                }
            });
        }
    }

    @Autowired
    private OpenTsdbSource openTsdbSource;

    @Autowired
    private KafkaSink kafkaSink;

    public void createJob(Job job) {
        if (StringUtils.isEmpty(job.getJobId())) {
            String jobId = UUID.randomUUID().toString().replaceAll("-", "");
            job.setJobId(jobId);
            job.setJobStatus(JobStatus.stop);
            job.setStatistics(new Statistics(0L, 0L));
            job.getSource().getBody().getQueries().forEach(x-> x.setStatistics(new Statistics(0L, 0L)));
            Threads.JobConfig.put(jobId, job);
        } else {
            Job newJob = Threads.JobConfig.get(job.getJobId());
            newJob.setJobName(job.getJobName());
            newJob.getSource().setApiUrl(job.getSource().getApiUrl());
            newJob.getSource().getBody().setStart(job.getSource().getBody().getStart());
            newJob.getSource().getBody().setQueries(job.getSource().getBody().getQueries());
            newJob.setSink(job.getSink());
            Threads.JobConfig.put(job.getJobId(), newJob);
        }
    }

    public void startJob(String jobId) {
        Job job = Threads.JobConfig.get(jobId);
        job.setJobStatus(JobStatus.running);
        OpenTsdbTask task = new OpenTsdbTask(job, openTsdbSource, kafkaSink);
        Threads.JobThreads.put(jobId, task);
        executor.execute(task);
    }

    public void stopJob(String jobId) {
        Job job = Threads.JobConfig.get(jobId);
        job.setJobStatus(JobStatus.stop);
        OpenTsdbTask task = Threads.JobThreads.get(jobId);
        executor.remove(task);
        Threads.JobThreads.remove(jobId);
    }

    public void deleteJob(String jobId) {
       OpenTsdbTask task = Threads.JobThreads.get(jobId);
       executor.remove(task);
       Threads.JobThreads.remove(jobId);
       Threads.JobConfig.remove(jobId);
    }

    public List<Job> getJobs() {
        List<Job> list = new ArrayList<>();
        list.addAll(Threads.JobConfig.values());
        return list;
    }

    public List<String> getSuggest(String urlApi, String type, String query) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", type);
        paramMap.put("q", query);
        String result = HttpUtil.get(urlApi + "/api/suggest", paramMap);
        return JSON.parseArray(result, String.class);
    }

    public List<String> getAggregators(String urlApi) {
        String result = HttpUtil.get(urlApi + "/api/aggregators");
        return JSON.parseArray(result, String.class);
    }

    public ThreadPoolConfig getThreadConfig() {
        return ThreadPoolConfig.builder()
                .corePoolSize(corePoolSize)
                .maximumPoolSize(maximumPoolSize)
                .workQueue(workQueue)
                .build();
    }

    @Scheduled(fixedRateString = "${spring.job.flush-interval}")
    public void updateJobConfig() {
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String iniUrl = jarFile.getParentFile().toString() + "/jobConfig.json";
        if (!FileUtil.exist(iniUrl)) {
            FileUtil.touch(iniUrl);
        }
        FileWriter writer = new FileWriter(iniUrl);
        writer.write(JSON.toJSONString(Threads.JobConfig, true));
        logger.info("已经提交jobConfig到文件");
    }
}
