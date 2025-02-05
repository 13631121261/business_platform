package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("agroups") // 表名
public class Group {
    int id;
    String[] tag_name;
    String group_name;
    int type;
    String describes;
    long create_time;
    long update_time;

    public void setUpdate_time(long update_time) {
            this.update_time = update_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getDescribes() {
        return describes;
    }

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

    public int getType() {
        return type;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTag_name(String[] tag_name) {
        this.tag_name = tag_name;
    }



    public String[] getTag_name() {
        return tag_name;
    }
}
