package com.example.pp.myApp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "myapp")
public class AppConfig {
    private String discount;
    private String cron;
}
