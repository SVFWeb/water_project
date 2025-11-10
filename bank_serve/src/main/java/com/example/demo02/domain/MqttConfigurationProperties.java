package com.example.demo02.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttConfigurationProperties {

    private String username ;
    private String password ;
    private String url ;
    private String subClientId ;
    private String subTopic ;
    private String pubClientId ;

}

