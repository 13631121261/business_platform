package com.kunlun.firmwaresystem.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {
    int id;
    String device_mac;
    String device_name;
    String Station_mac;
    String Station_name;
    int rssi;
    String bt;
    String time;
    String customer_key;
    String userkey;
  //  Tag tag;
    int onLine = 0;

    int run;
    double d = 0, x, y;
    int type = 1;
    public Record() {

    }
    public Record(String device_mac,

                  String device_name,
                  String Station_mac,
                  String Station_name,
                  int rssi,    String customer_key,String userkey
              ) {
        this.device_name = device_name;
        this.device_mac = device_mac;
        this.Station_mac = Station_mac;
        this.Station_name = Station_name;
        this.rssi = rssi;
        this.customer_key=customer_key;
        this.userkey=userkey;
      //  this.tag = tag;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.time = sdf.format(date);
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getD() {
        return d;
    }

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    public String getDevice_mac() {
        return device_mac;
    }

    public String getBt() {
        return bt;
    }

    public String getCustomerkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public void setBt(String bt) {
        if (bt != null && bt.equals("0")) {
            bt = -1 + "";
        }
        this.bt = bt;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", device_mac='" + device_mac + '\'' +
                ", device_name='" + device_name + '\'' +
                ", Station_mac='" + Station_mac + '\'' +
                ", Station_name='" + Station_name + '\'' +
                ", rssi=" + rssi +
                ", bt='" + bt + '\'' +
                ", time='" + time + '\'' +

                ", onLine=" + onLine +
                ", run=" + run +
                ", d=" + d +
                ", x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
