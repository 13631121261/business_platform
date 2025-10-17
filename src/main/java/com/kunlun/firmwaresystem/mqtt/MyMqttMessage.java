package com.kunlun.firmwaresystem.mqtt;

import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MyMqttMessage {
    String topic;
    MqttMessage MqttMessage;

    public void setMqttMessage(MqttMessage mqttMessage) {
        MqttMessage = mqttMessage;
    }

    public MqttMessage getMqttMessage() {
        return MqttMessage;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
    public MyMqttMessage(String topic,MqttMessage message) {
        this.topic = topic;
        this.MqttMessage = message;

    }
}
