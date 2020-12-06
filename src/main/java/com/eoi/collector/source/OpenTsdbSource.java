package com.eoi.collector.source;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.eoi.collector.entity.Body;
import com.eoi.collector.entity.Metric;
import com.eoi.collector.entity.TsdMetric;
import com.eoi.collector.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author lemon
 */
@Component
public class OpenTsdbSource {
    private final Logger logger = LoggerFactory.getLogger(OpenTsdbSource.class);

    public List<Metric> getSource(String apiUrl, Body body) {
        String req = JsonUtil.encodeNotThrowException(body);
        List<Metric> res = new ArrayList<>();
        String result = HttpRequest.post(apiUrl + "/api/query")
                .body(req)
                .timeout(10000)
                .execute()
                .body();
        try {
            List<TsdMetric> list = JSON.parseArray(result, TsdMetric.class);
            list.forEach(x-> x.getDps().forEach((key, value) -> {
                Metric metric = new Metric();
                metric.setMetric(x.getMetric());
                Long timestamp = Long.parseLong(key);

                if (10 == key.length()) {
                    timestamp = timestamp * 1000;
                }
                metric.setTimestamp(timestamp);
                metric.setValue(value);
                metric.setTsuids(x.getTsuids());
                metric.setTags(x.getTags());
                res.add(metric);
            }));
            res.sort(Comparator.comparingLong(Metric::getTimestamp));
            return res;
        } catch (Exception e) {
            logger.error("处理opentsdb数据出差，req:{}, e:{}", req, e.getMessage());
            return res;
        }
    }
}
