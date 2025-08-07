package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class History {
    @TableId(type = IdType.AUTO)  // 数据库ID自增
    long id;
    String map_key;
    String type;
    String sn;
    long start_time;
    long end_time;
    String project_key;
    String name;
    String station_mac;
    double x,y;
    @TableField(exist = false)
    String Station_name;
    @TableField(exist = false)
    String company_name;

    int company_id;

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getStation_name() {
        return Station_name;
    }

    public void setStation_name(String station_name) {
        Station_name = station_name;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setStation_mac(String station_mac) {
        this.station_mac = station_mac;
    }

    public String getStation_mac() {
        return station_mac;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", map_key='" + map_key + '\'' +
                ", type='" + type + '\'' +
                ", sn='" + sn + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", project_key='" + project_key + '\'' +
                ", name='" + name + '\'' +
                ", station_mac='" + station_mac + '\'' +
                '}';
    }
}
