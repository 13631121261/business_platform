package com.kunlun.firmwaresystem.mqtt;

import com.google.gson.Gson;

public class RabbitMessage {
    String pubTopic;
    String msg;
    String udp;
    String project_key;

    public RabbitMessage() {

    }

    public RabbitMessage(String pubTopic, String msg, String project_key) {
        this.msg = msg;
        this.project_key=project_key;
        this.pubTopic = pubTopic;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getUdp() {
        return udp;
    }

    public String getPubTopic() {
        return pubTopic;
    }

    public void setPubTopic(String pubTopic) {
        this.pubTopic = pubTopic;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
