package com.kunlun.firmwaresystem.entity;

public class Real_Point {
    int id;
    String idcard;
    int partol_id;
    long create_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public int getPartol_id() {
        return partol_id;
    }

    public void setPartol_id(int partol_id) {
        this.partol_id = partol_id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
}
