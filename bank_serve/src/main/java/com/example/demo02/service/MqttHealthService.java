// MqttHealthService.java
package com.example.demo02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

@Service
public class MqttHealthService {

    @Autowired
    private MqttPahoClientFactory mqttPahoClientFactory;

    /**
     * 检查MQTT服务连接状态
     */
    public boolean checkMqttConnection() {
        MqttClient client = null;
        try {
            MqttConnectOptions options = mqttPahoClientFactory.getConnectionOptions();
            String serverURI = options.getServerURIs()[0];

            // 创建临时客户端测试连接
            String clientId = "health_check_" + System.currentTimeMillis();
            client = new MqttClient(serverURI, clientId);
            client.connect(options);

            boolean isConnected = client.isConnected();
            System.out.println("MQTT健康检查: " + (isConnected ? "✅ 连接正常" : "❌ 连接异常"));

            return isConnected;

        } catch (MqttException e) {
            System.err.println("MQTT健康检查失败: " + e.getMessage());
            return false;
        } finally {
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                    client.close();
                } catch (MqttException e) {
                    System.err.println("关闭MQTT健康检查客户端失败: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 获取MQTT连接信息
     */
    public String getMqttConnectionInfo() {
        try {
            MqttConnectOptions options = mqttPahoClientFactory.getConnectionOptions();
            String serverURI = options.getServerURIs()[0];
            String username = options.getUserName();

            return String.format("MQTT服务器: %s, 用户名: %s, 自动重连: %s",
                    serverURI,
                    username != null ? username : "未设置",
                    options.isAutomaticReconnect());
        } catch (Exception e) {
            return "获取MQTT连接信息失败: " + e.getMessage();
        }
    }
}