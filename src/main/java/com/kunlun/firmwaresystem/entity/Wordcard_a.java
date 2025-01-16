package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

public class Wordcard_a  {
    int id;
    int sos;
    int run;
    double n;
    int isbind;
    String idcard;
    double x,y;
    String customer_key;
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

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = bt;
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


    public Wordcard_a(String mac,
                      String user_key, int type, String customer_key, int id) {

        this.mac = mac;
        this.user_key = user_key;
        this.type = type;
        this.customer_key=customer_key;
        this.id=id;
    }

    public Wordcard_a(String mac,
                      String user_key, int type, String customer_key) {

        this.mac = mac;
        this.user_key = user_key;
        this.type = type;
        this.customer_key=customer_key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSos() {
        return sos;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public int getIsbind() {
        return isbind;
    }

    public void setIsbind(int isbind) {
        this.isbind = isbind;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public Wordcard_a(){

    }
}
