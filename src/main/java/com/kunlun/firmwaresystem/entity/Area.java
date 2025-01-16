package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Area {
    double x,y;
    int id;
    String Station_mac;
    String userkey;
    String name;
    long createtime;
    long updatetime;
    String customer_key;
    @TableField(exist = false)
    String username;
    @TableField(exist = false)
    int g_count;
    String project_key;
    String point;
    String data;
    String map_key;
    String map_name;

    public Area(String name,    String Station_mac,
            String userkey, String customer_key) {

        this.name = name;
        this.userkey=userkey;
        this.Station_mac=Station_mac;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.createtime = System.currentTimeMillis()/1000;
        this.customer_key=customer_key;
    }
    public Area(String name,    String Station_mac,
                String userkey, String customer_key,  String project_key,
                        String point,
                        String data) {

        this.name = name;
        this.userkey=userkey;
        this.Station_mac=Station_mac;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记

        this.createtime =System.currentTimeMillis()/1000;
        this.customer_key=customer_key;
        this.project_key=project_key;
        this.point=point;
        this.data=data;
    }
    public Area() {

    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }


    public String getMap_name() {
        return map_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getProject_key() {
        return project_key;
    }

    public String getData() {
        return data;
    }

    public String getPoint() {
        return point;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setStation_mac(String Station_mac) {
        this.Station_mac = Station_mac;
    }

    public String getUserkey() {
        return userkey;
    }

    public String getUsername() {
        return username;
    }

    public long getCreatetime() {
        return createtime;
    }

    public String getStation_mac() {
        return Station_mac;
    }

    public String getCustomername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getG_count() {
        return g_count;
    }

    public void setG_count(int g_count) {
        this.g_count = g_count;
    }
}
