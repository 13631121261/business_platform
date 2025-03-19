package com.kunlun.firmwaresystem.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

//数据转发的规则
public class Rules implements Serializable {
    int id;
    String name;
    String rule_key;
    int type;
    String server;
    int port;
    String customer_key;
    String user_key;
    String create_time;

    public Rules(String name, int type, String server, int port, String userKey,    String customer_key) {
        this.name = name;
        this.type = type;
        this.server = server;
        this.port = port;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.create_time = sdf.format(date);
        //   myPrintln("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
        this.rule_key = Base64.getEncoder().encodeToString((name + "_" + date.getTime()).getBytes()).replaceAll("\\+", "");
        this.user_key = userKey;
        this.customer_key=customer_key;
    }

    public Rules() {

    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule_key() {
        return rule_key;
    }

    public void setRule_key(String rule_key) {
        this.rule_key = rule_key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUser_key() {
        return user_key;
    }


    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

}
