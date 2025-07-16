package com.kunlun.firmwaresystem.entity;

public class Company {
    private int id;
    private String name;
    private String describes;
    private String project_key;
    private int person_count  ;
    private int  device_count  ;
    public String getProject_key() {
        return project_key;
    }

    public int getPerson_count() {
        return person_count;
    }

    public void setPerson_count(int person_count) {
        this.person_count = person_count;
    }

    public int getDevice_count() {
        return device_count;
    }

    public void setDevice_count(int device_count) {
        this.device_count = device_count;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
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

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describe) {
        this.describes = describe;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", describes='" + describes + '\'' +
                ", project_key='" + project_key + '\'' +
                ", person_count=" + person_count +
                ", device_count=" + device_count +
                '}';
    }
}
