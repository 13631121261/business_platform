package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

public class Fence {
    int id;

    FenceType fence_type;
    //触发停留时间
    int trigger1;
    String notes;
    String name;
    String user_key;
    String project_key;
    long create_time;
    long update_time;
    String customer_key;
    String map_key;
    boolean open_status;
    int time_type;
    String start_time;
    String stop_time;
    String start_times;
    String stop_times;

    String map_name;
    String points;
    int time_out;

    public void setTime_out(int time_out) {
        this.time_out = time_out;
    }

    public int getTime_out() {
        return time_out;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    public Fence(){

     }

    public int getTime_type() {
        return time_type;
    }

    public void setTime_type(int time_type) {
        this.time_type = time_type;
    }





    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }



    public String getMap_name() {
        return map_name;
    }


    public void setFence_type(FenceType fence_type) {
        this.fence_type = fence_type;
    }

    public FenceType getFence_type() {
        return fence_type;
    }

    public boolean getOpen_status() {
        return open_status;
    }

    public void setOpen_status(boolean open_status) {
        this.open_status = open_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public void setTrigger1(int trigger) {
        this.trigger1 = trigger;
    }

    public int getTrigger1() {
        return trigger1;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Long getCreate_time() {
        return create_time;
    }



    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_times(String start_times) {
        this.start_times = start_times;
    }

    public void setStop_times(String stop_times) {
        this.stop_times = stop_times;
    }

    public String getStart_times() {
        return start_times;
    }

    public String getStop_times() {
        return stop_times;
    }

    public String getStop_time() {
        return stop_time;
    }

    @Override
    public String toString() {
        return "Fence{" +
                "id=" + id +
                ", fence_type=" + fence_type +
                ", trigger1=" + trigger1 +
                ", notes='" + notes + '\'' +
                ", name='" + name + '\'' +
                ", user_key='" + user_key + '\'' +
                ", project_key='" + project_key + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", customer_key='" + customer_key + '\'' +
                ", map_key='" + map_key + '\'' +
                ", open_status=" + open_status +
                ", time_type=" + time_type +
                ", start_time='" + start_time + '\'' +
                ", stop_time='" + stop_time + '\'' +
                ", start_times='" + start_times + '\'' +
                ", stop_times='" + stop_times + '\'' +
                ", map_name='" + map_name + '\'' +
                ", points='" + points + '\'' +
                ", time_out=" + time_out +
                '}';
    }
}
