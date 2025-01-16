package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.ArrayList;

public class FWordcard {
        int id;
        int  is_bind;
        String person_name;
        String person_idcard;
        int bt;
       double x;
       double y;
        double latitude;
        double longitude;
        int location_type;
        String customer_key;
        String user_key;
        String project_key;
        long create_time;
        long update_time;
        String imei;
    @TableField(exist=false)
        ArrayList<Beacon_tag> beaconTags;
    @TableField(exist=false)
    int[] group_arr;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_bind() {
        return is_bind;
    }

    public void setIs_bind(int is_bind) {
        this.is_bind = is_bind;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_idcard() {
        return person_idcard;
    }

    public void setPerson_idcard(String person_idcard) {
        this.person_idcard = person_idcard;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public int getBt() {
        return bt;
    }

    public void setBt(int bt) {
        this.bt = bt;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLocation_type() {
        return location_type;
    }

    public void setLocation_type(int location_type) {
        this.location_type = location_type;
    }
    public String getCustomer_key() {
        return customer_key;
    }
    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getProject_key() {
        return project_key;
    }
    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}

