package com.kunlun.firmwaresystem.entity;

public class Record_sos {
    int id;
    int sos;
    long time;
    String handle_time;
    int handle = 0;
    String mac;
    String Station_name;
    String Station_mac;
    String sn_idcard;
    String name;
    String type;
    String customer_key;
    String userkey;

    public Record_sos() {

    }

    public Record_sos(int sos,
                      long time,
                      String mac,
                      String Station_mac,
                      String Station_name,
                      String name,
                      String sn_idcard,
                      String type,    String customer_key,String userkey
                    ) {
        this.mac = mac;
        this.userkey=userkey;
        this.sos= sos;
        this.time = time;
        this.Station_mac = Station_mac;
        this.Station_name = Station_name;
        this.name=name;
        this.sn_idcard=sn_idcard;
        this.type=type;
        this.customer_key=customer_key;

    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSn_idcard(String sn_idcard) {
        this.sn_idcard = sn_idcard;
    }

    public String getSn_idcard() {
        return sn_idcard;
    }

    public String getType() {
        return type;
    }

    public void setStation_name(String Station_name) {
        this.Station_name = Station_name;
    }

    public void setStation_mac(String Station_mac) {
        this.Station_mac = Station_mac;
    }

    public String getStation_name() {
        return Station_name;
    }

    public String getStation_mac() {
        return Station_mac;
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


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHandle_time() {
        return handle_time;
    }

    public void setHandle_time(String handle_time) {
        this.handle_time = handle_time;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
