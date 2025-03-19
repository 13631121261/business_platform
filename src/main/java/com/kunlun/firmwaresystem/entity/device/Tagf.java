package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
public class Tagf {
    @TableId(type = IdType.AUTO) // 确保 ID 为自增
    int id;
    String name;
    //0 表示为设备  1表示为人员
    int type;
    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
    }

    String project_key;

    public void setType(int type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Tagf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", project_key='" + project_key + '\'' +
                '}';
    }
}
