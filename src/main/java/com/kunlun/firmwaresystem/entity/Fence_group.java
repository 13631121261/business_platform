package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Fence_group {
    int id;
    String name;
    String f_id;
    String describes ;
    long create_time;
    long update_time;
    String project_key;
    String customer_key;
    @TableField(exist = false)
    int used_count;
    @TableField(exist = false)
    ArrayList<Fence> fences;
    @TableField(exist = false)
    ArrayList<T> t;
    public void setUsed_count(int used_count) {
        this.used_count = used_count;
    }

    public void setFences(ArrayList<Fence> fences) {
        this.fences = fences;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public void setT(ArrayList<T> t) {
        this.t = t;
    }

    public ArrayList<Fence> getFences() {
        return fences;
    }

    public ArrayList<T> getT() {
        return t;
    }

    public int getUsed_count() {
        return used_count;
    }

    public String getDescribes() {
        return describes;
    }

    public int getId() {
        return id;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
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

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getDescrides() {
        return describes;
    }

    public void setDescrides(String descrides) {
        this.describes = descrides;
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

    @Override
    public String toString() {
        return "Fence_group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", f_id='" + f_id + '\'' +
                ", descrides='" + describes + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
    public static class   T{
        String name;
        int type;
        String sn;

        public void setName(String name) {
            this.name = name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getSn() {
            return sn;
        }
    }
}
