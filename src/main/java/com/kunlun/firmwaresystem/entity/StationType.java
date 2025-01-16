package com.kunlun.firmwaresystem.entity;

public class StationType {
    int id;
    String name;
    String project_key;
    long create_time;
    String describes;

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describe) {
        this.describes = describe;
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
}
