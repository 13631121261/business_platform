package com.kunlun.firmwaresystem.entity.check;

import com.kunlun.firmwaresystem.entity.Alarm_Type;
import com.kunlun.firmwaresystem.entity.Alarm_object;

public class Check_alarm {
    int id;
    long create_time;
    int sum;
    Alarm_Type alarm_type;
    Alarm_object alarm_object;
    String project_key;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public Alarm_Type getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(Alarm_Type alarm_type) {
        this.alarm_type = alarm_type;
    }

    public Alarm_object getAlarm_object() {
        return alarm_object;
    }

    public void setAlarm_object(Alarm_object alarm_object) {
        this.alarm_object = alarm_object;
    }
}
