package com.example.demo02.config;

import com.example.demo02.domain.MqttConfigurationProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
public class MqttConfiguration {

    @Autowired
    private MqttConfigurationProperties mqttConfigurationProperties ;

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory() ;
        MqttConnectOptions options = new MqttConnectOptions() ;
        options.setCleanSession(true);
        options.setUserName(mqttConfigurationProperties.getUsername());
        options.setPassword(mqttConfigurationProperties.getPassword().toCharArray());
        options.setServerURIs(new String[]{ mqttConfigurationProperties.getUrl() } );

        options.setCleanSession(true);

        // 处理空用户名密码
        if (mqttConfigurationProperties.getUsername() != null &&
                !mqttConfigurationProperties.getUsername().isEmpty()) {
            options.setUserName(mqttConfigurationProperties.getUsername());
        }

        if (mqttConfigurationProperties.getPassword() != null &&
                !mqttConfigurationProperties.getPassword().isEmpty()) {
            options.setPassword(mqttConfigurationProperties.getPassword().toCharArray());
        }

        options.setServerURIs(new String[]{ mqttConfigurationProperties.getUrl() });

        // 添加连接优化
        options.setConnectionTimeout(30);        // 连接超时时间
        options.setKeepAliveInterval(60);        // 保活间隔
        options.setAutomaticReconnect(true);     // 自动重连
        options.setMaxReconnectDelay(5000);      // 最大重连延迟

        mqttPahoClientFactory.setConnectionOptions(options);

        return mqttPahoClientFactory ;
    }

}
