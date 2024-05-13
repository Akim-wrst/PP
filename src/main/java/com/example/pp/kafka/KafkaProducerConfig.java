package com.example.pp.kafka;

import com.example.pp.model.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${clients.partitions}")
    private int partitions;

    @Value("${clients.replicas}")
    private int replicas;

    @Value("${clients.topic_name}")
    private String topic_name;

    @Value("${clients.host}")
    private String host;

    @Value("${clients.weeks}")
    private int weeks;

    @Value("${clients.days}")
    private int days;

    @Bean
    public NewTopic exampleTopic() {
        Map<String, String> topicConfig = new HashMap<>();
        long retentionPeriodMs = calculateRetentionPeriodInMs(weeks, days); // Вычисляем время жизни сообщений в миллисекундах
        topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(retentionPeriodMs));
        return TopicBuilder.name(topic_name).partitions(partitions).replicas(replicas).configs(topicConfig).build();
    }

    private long calculateRetentionPeriodInMs(int weeks, int days) {
        long retentionPeriodSeconds = (weeks * 7L + days) * 24 * 60 * 60;
        return retentionPeriodSeconds * 1000; // Конвертируем в миллисекунды
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
