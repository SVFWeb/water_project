package com.example.demo02;

import com.example.demo02.domain.MqttConfigurationProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableConfigurationProperties(MqttConfigurationProperties.class)
@SpringBootApplication
@MapperScan("com.example.demo02.mapper") // 添加MyBatis的Mapper扫描
public class Demo02Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo02Application.class, args);
    }

}
