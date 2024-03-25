package com.example.pp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "myapp")
public class AppConfig {
    private String discount;
    private String cron;
    private String topic_name;
    private String host;
    private Integer partitions;
    private Integer replicas;
    private Integer hour;
    private Integer minute;
    private String lastDigitOfNumber;
}
