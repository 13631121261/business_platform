package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Arrays;

public  class Patrol {
    int id;
    @TableField(exist = false)
    String[] time_range;
    String startTime;
    String endTime;
    boolean status;
    int area_id;
    String area_name;
    int staytime;
    @TableField(exist = false)
    String[] enable_days;
    String enable_day;
    String project_key;
    String user_key;
    String name;
    long create_time;
    long update_time;
    @TableField(exist = false)
    boolean required=true;
    @TableField(exist = false)
    boolean checked=false;

    @TableField(exist = false)
    String points;


    double proportion=10;

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public double getProportion() {
        return proportion;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String[] getTime_range() {
        return time_range;
    }

    public void setTime_range(String[] time_range) {
        this.time_range = time_range;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getStaytime() {
        return staytime;
    }

    public void setStaytime(int staytime) {
        this.staytime = staytime;
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

    public void setStart_end_time(String[] startEndTime){
        if (startEndTime!=null&&startEndTime.length==2){
            startTime=startEndTime[0];
            endTime=startEndTime[1];
        }
    }
    public void setTime_range(String  startTime,String endTime){
       String[] time_range=new String[2];
       time_range[0]=startTime;
       time_range[1]=endTime;
       this.time_range=time_range;
    }


    public void setEnable_day(String[] enableDays) {
       if (enableDays != null && enableDays.length > 0) {
           String d="";
           for (int i = 0; i < enableDays.length; i++) {
               d=d+"-"+enableDays[i];
           }
           this.enable_day = d;
       }
    }

    public void setEnable_day(String enableDay) {
        this.enable_day = enableDay;
    }

    public void setEnable_days(String enableDay) {
        if (enableDay != null && !enableDay.isEmpty()) {
            this.enable_days = enableDay.split("-");
        }

    }

    public String getEnable_day() {
        return enable_day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String[] getEnable_days() {
        return enable_days;
    }

    public void setEnable_days(String[] enableDays) {
        this.enable_days = enableDays;
    }



    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Patrol{" +
                "id=" + id +
                ", time_range=" + Arrays.toString(time_range) +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status=" + status +
                ", area_id=" + area_id +
                ", area_name='" + area_name + '\'' +
                ", staytime=" + staytime +
                ", enable_days=" + Arrays.toString(enable_days) +
                ", enable_day='" + enable_day + '\'' +
                ", project_key='" + project_key + '\'' +
                ", user_key='" + user_key + '\'' +
                ", name='" + name + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", required=" + required +
                ", checked=" + checked +
                '}';
    }
}