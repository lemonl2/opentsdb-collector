package com.eoi.collector.controller;

import com.eoi.collector.common.BaseResult;
import com.eoi.collector.common.ResponseWrapper;
import com.eoi.collector.entity.Job;
import com.eoi.collector.entity.SuggestReq;
import com.eoi.collector.service.OpenTsdbService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author lemon
 */
@RestController
@RequestMapping("/api/opentsdb")
public class OpenTsdbController implements BaseResult {
    @Autowired
    private OpenTsdbService openTsdbService;

    @ApiOperation("创建opentsdb的采集任务")
    @PostMapping("/createJob")
    public ResponseWrapper createJob(@Valid @RequestBody Job job) {
        openTsdbService.createJob(job);
        return successful("jobId: "+ job.getJobId() +" 创建采集任务成功");
    }

    @ApiOperation("启动opentsdb的采集任务")
    @PostMapping("/startJob")
    public ResponseWrapper startJob(@Valid @RequestBody Job job) {
        openTsdbService.startJob(job.getJobId());
        return successful("jobId: "+ job.getJobId() +" 启动采集任务成功");
    }

    @ApiOperation("启动opentsdb的采集任务")
    @PostMapping("/deleteJob")
    public ResponseWrapper deleteJob(@Valid @RequestBody Job job) {
        openTsdbService.deleteJob(job.getJobId());
        return successful("jobId: "+ job.getJobId() +" 删除采集任务成功");
    }

    @ApiOperation("获取启动的所以opentsdb的采集任务")
    @GetMapping("/getJobs")
    public ResponseWrapper getJobs() {
        return successful(openTsdbService.getJobs());
    }

    @ApiOperation("关闭opentsdb的采集任务")
    @PostMapping("/stopJob")
    public ResponseWrapper stopJob(@Valid @RequestBody Job job) {
        openTsdbService.stopJob(job.getJobId());
        return successful("jobId: "+ job.getJobId() +" 关闭采集任务成功");
    }

    @ApiOperation("根据条件获取")
    @PostMapping("/getSuggest")
    public ResponseWrapper getSuggest(@Valid @RequestBody SuggestReq req) {
        return successful(openTsdbService.getSuggest(req.getApiUrl(), req.getType(),req.getQ()));
    }

    @ApiOperation("获取聚合类型")
    @PostMapping("/getAggregators")
    public ResponseWrapper getAggregators(@Valid @RequestBody SuggestReq req) {
        return successful(openTsdbService.getAggregators(req.getApiUrl()));
    }

    @ApiOperation("获取线程池设置")
    @GetMapping("/getThreadConfig")
    public ResponseWrapper getThreadConfig() {
        return successful(openTsdbService.getThreadConfig());
    }
}
