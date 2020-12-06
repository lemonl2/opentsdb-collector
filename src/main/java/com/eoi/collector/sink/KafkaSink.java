package com.eoi.collector.sink;

import com.eoi.collector.entity.Metric;
import com.eoi.collector.entity.Sink;
import com.eoi.collector.util.JsonUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * @author lemon
 */
@Component
public class KafkaSink {
    public void sink(Sink sink, List<Metric> list)  {
        Properties props = new Properties();
        props.put("bootstrap.servers", sink.getBootstrapServers());
        props.put("acks", sink.getAcks());
        props.put("retries", sink.getRetries());
        props.put("batch.size", sink.getBatchSize());
        props.put("linger.ms", sink.getLingerMs());
        props.put("buffer.memory", sink.getBufferMemory());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        list.forEach(item-> producer.send(new ProducerRecord<>(sink.getTopic(), JsonUtil.encodeNotThrowException(item))));
        producer.close();
    }
}
