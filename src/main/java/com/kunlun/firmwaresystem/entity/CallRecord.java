package com.kunlun.firmwaresystem.entity;

public class CallRecord {
    int id;
    long start_time;
    long stop_time;
    int should;
    int present;
    int absent;
    String path;
    String project_key;
    String user_key;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getStop_time() {
        return stop_time;
    }

    public void setStop_time(long stop_time) {
        this.stop_time = stop_time;
    }

    public int getShould() {
        return should;
    }

    public void setShould(int should) {
        this.should = should;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public String toString() {
        return "CallRecord{" +
                "id=" + id +
                ", start_time=" + start_time +
                ", stop_time=" + stop_time +
                ", should=" + should +
                ", present=" + present +
                ", absent=" + absent +
                ", path='" + path + '\'' +
                ", project_key='" + project_key + '\'' +
                ", user_key='" + user_key + '\'' +
                '}';
    }
}
