package com.kunlun.firmwaresystem.entity;
public class Alarm {
    int id;
    Alarm_Type alarm_type;
    Alarm_object alarm_object;
    long create_time;
    String map_key;
    int fence_id;
    String fence_name;
    double bt;
    int area_id;
    String area_name;
    String name;
    String sn;
    String project_key;
    public Alarm(){

    }
    public Alarm( Alarm_Type alarm_type,
                  Alarm_object alarm_object,

            String map_key,
            int fence_id,
            String fence_name,
            double bt,
            int area_id,
            String area_name,
            String name,
            String sn,
            String project_key,long time){
    this.alarm_object=alarm_object;
    this.alarm_type=alarm_type;
    this.area_id=area_id;
    this.area_name=area_name;
    this.sn=sn;
    this.fence_id=fence_id;
    this.fence_name=fence_name;
    this.bt=bt;
    this.map_key=map_key;
    if(time<=0){
        time=System.currentTimeMillis()/1000;
    }
    this.create_time=time;
    this.name=name;
    this.project_key=project_key;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public int getFence_id() {
        return fence_id;
    }

    public void setFence_id(int fence_id) {
        this.fence_id = fence_id;
    }

    public String getFence_name() {
        return fence_name;
    }

    public void setFence_name(String fence_name) {
        this.fence_name = fence_name;
    }

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }
}
