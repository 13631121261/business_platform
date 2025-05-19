package com.kunlun.firmwaresystem.entity;

public class Push_Device {



    String address;
    String gateway_address;
    String bt;
    String project_key;
    double x,y;
    String map_key;
    String push_type;
    String device_type;
    long last_time;
    long time=0;
    int run=0;
    int sos=0;

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getSos() {
        return sos;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getRun() {
        return run;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setLast_time(long last_time) {
        this.last_time = last_time;
    }

    public long getLast_time() {
        return last_time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGateway_address() {
        return gateway_address;
    }

    public void setGateway_address(String gateway_address) {
        this.gateway_address = gateway_address;
    }

    public String getBt() {
        return bt;
    }

    public void setBt(String bt) {
        this.bt = bt;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Push_Device{" +
                "address='" + address + '\'' +
                ", gateway_address='" + gateway_address + '\'' +
                ", bt='" + bt + '\'' +
                ", project_key='" + project_key + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", map_key='" + map_key + '\'' +
                ", push_type='" + push_type + '\'' +
                ", device_type='" + device_type + '\'' +
                ", last_time=" + last_time +
                ", time=" + time +
                ", run=" + run +
                '}';
    }
}
