package com.kunlun.firmwaresystem.entity;

public class History {
    int id;
    double x;
    double y;
    String map_key;
    String type;
    String sn;
    long time;
    String project_key;
    String name;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", map_key='" + map_key + '\'' +
                ", type='" + type + '\'' +
                ", sn='" + sn + '\'' +
                ", time=" + time +
                ", project_key='" + project_key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
