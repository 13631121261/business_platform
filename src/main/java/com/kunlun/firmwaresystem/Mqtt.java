package com.kunlun.firmwaresystem;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Mqtt {
    @Value("${mqtt.server.host}")
    private   String server;
    @Value("${mqtt.server.port}")
    private Integer port;
    @Value("${mqtt.server.subTopic}")
    private  String subTopic;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }
}
