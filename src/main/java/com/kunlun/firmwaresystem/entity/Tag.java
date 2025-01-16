package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tag {
    int id;
    double n;
    int isbind;
    //1:绑定设备资产，，2：绑定人员
    int bind_type;
    String bind_key;

    String map_key;
    @TableField(exist = false)
    int sos=-1;
    @TableField(exist = false)
    int run=-1;
    long createtime;
    String customer_key;
    String project_key;
    @TableField(exist = false)
    double x=0;
    @TableField(exist = false)
    double y=0;


    int type;
    String mac;
    double bt;
    String user_key;
    @TableField(exist = false)
    String Station_address;
    @TableField(exist = false)
    int online;

    long lastTime;
    @TableField(exist = false)
    int rssi;
    public long getLastTime() {
        return lastTime;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getRssi() {
        return rssi;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public String getStation_address() {
        return Station_address;
    }

    public void setStation_address(String Station) {
        this.Station_address = Station;
    }



    public Tag() {

    }
    public Tag(String mac) {
    this.mac=mac;
    }
    public Tag(String mac,
               String user_key, int type, String customer_key) {

        this.mac = mac;
        this.type=type;
        this.user_key = user_key;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.createtime = System.currentTimeMillis()/1000;
        this.customer_key=customer_key;
        switch (type){
            case 1:
               setRun(-1);
               setSos(-1);
                break;
            case 2:
                setRun(-1);
                setSos(0);
                break;
            case 3:
                setRun(0);
                setSos(-1);
                break;
            case 4:
                setRun(0);
                setSos(0);
                break;
        }
    }

    public void setBind_type(int bind_type) {
        this.bind_type = bind_type;
    }

    public int getBind_type() {
        return bind_type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }



    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getRun() {
        return run;
    }

    public Tag(int id, String mac,
               String user_key, int type) {
        this.id = id;

        this.mac = mac;


        this.user_key = user_key;
        this.type = type;

    }



    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public double getBt() {
        return bt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }


    public int getSos() {
        return sos;
    }

    public int getIsbind() {
        return isbind;
    }

    public void setIsbind(int isbind) {
        this.isbind = isbind;
    }

    public String getBind_key() {
        return bind_key;
    }

    public void setBind_key(String bind_key) {
        this.bind_key = bind_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getCreatetime() {
        return createtime;
    }
    public void bind(String bind_key){
        this.bind_key=bind_key;
        this.isbind=1;
    }
    public void unbind(){
        this.bind_key="";
        this.isbind=0;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "id=" + id +
                ", n=" + n +
                ", isbind=" + isbind +
                ", bind_type=" + bind_type +
                ", map_key='" + map_key + '\'' +
                ", sos=" + sos +
                ", run=" + run +
                ", createtime=" + createtime +
                ", customer_key='" + customer_key + '\'' +
                ", project_key='" + project_key + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", type=" + type +
                ", mac='" + mac + '\'' +
                ", bt=" + bt +
                ", user_key='" + user_key + '\'' +
                ", Station_address='" + Station_address + '\'' +
                ", online=" + online +
                ", lastTime=" + lastTime +
                ", rssi=" + rssi +
                '}';
    }
}
