package com.kunlun.firmwaresystem.entity;

public class Locator {
    int id;
    String name;
    String map_name;
    String address;
    String   ip;
    long last_time;
    long     create_time;
    double x;
    double  y;
    double z;
    String map_id;
    String map_key;
    String project_key;
    String user_key;
    String model_name;
    String version;
    int  online;
    int area_id;
    String area_name;
    double proportion;

    public Locator(){

    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public double getProportion() {
        return proportion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLast_time() {
        return last_time;
    }

    public void setLast_time(long last_time) {
        this.last_time = last_time;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
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

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public int getArea_id() {
        return area_id;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
       // System.out.println("设置基站离线");
        this.online = online;
    }

    @Override
    public String toString() {
        return "Locator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", map_name='" + map_name + '\'' +
                ", address='" + address + '\'' +
                ", ip='" + ip + '\'' +
                ", last_time=" + last_time +
                ", create_time=" + create_time +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", map_id='" + map_id + '\'' +
                ", map_key='" + map_key + '\'' +
                ", project_key='" + project_key + '\'' +
                ", user_key='" + user_key + '\'' +
                ", model_name='" + model_name + '\'' +
                ", version='" + version + '\'' +
                ", online=" + online +
                '}';
    }
}
