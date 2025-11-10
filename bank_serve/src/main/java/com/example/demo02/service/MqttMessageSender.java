package com.example.demo02.service;


import com.example.demo02.gateway.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageSender {

    @Autowired
    private MqttGateway mqttGateway;

    public void sendMsg(String topic , String msg) {
        mqttGateway.sendMsgToMqtt(topic , msg);
    }

    public void sendMsg(String topic , int qos ,  String msg) {
        mqttGateway.sendMsgToMqtt(topic , qos ,  msg);
    }

}
