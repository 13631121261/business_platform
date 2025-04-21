package com.kunlun.firmwaresystem.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Patrol_list {
    int id;
    String name;
    String describes;
    @TableField(exist = false)
    List<Patrol> patrol_list_detail;
    String patrol_list;
    long create_time;
    long update_time;
    String patrol_type;
    String project_key;
    String must_list;
    @TableField(exist = false)
    int person_count;

    public void setPerson_count(int person_count) {
        this.person_count = person_count;
    }

    public int getPerson_count() {
        return person_count;
    }

    public void setMust_list(String must_list) {
        this.must_list = must_list;
    }

    public void setPatrol_type(String patrol_type) {
        this.patrol_type = patrol_type;
    }

    public String getMust_list() {
        return must_list;
    }

    public String getPatrol_type() {
        return patrol_type;
    }

    public String getProject_key() {
        return project_key;
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

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getDescribes() {
        return describes;
    }

    public List<Patrol> getPatrol_list_detail() {
        return patrol_list_detail;
    }

    public void setPatrol_list_detail(List<Patrol> patrols) {
        if (patrols != null&& !patrols.isEmpty()) {
            this.patrol_list_detail = patrols;
        }

    }

    public String getPatrol_list() {
        return patrol_list;
    }

    public void setPatrol_list(String patrol_str) {
        this.patrol_list = patrol_str;
        myPrintln("路线="+patrol_str);
        /*if (patrol_str != null && !patrol_str.isEmpty()) {
            try {
                JSONArray jsonArray = JSON.parseArray(patrol_str);
                if (jsonArray != null&& !jsonArray.isEmpty()) {
                    List<Patrol> patrols = new ArrayList<Patrol>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Patrol patrol = new Gson().fromJson(jsonObject.toString(), new TypeToken<Patrol>() {
                        }.getType());
                        patrols.add(patrol);
                    }
                    this.setPatrol_list_detail(patrols);
                }
            }catch (Exception e){
                myPrintln(e.getMessage());
            }

        }*/
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
        return "Patrol_list{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", describes='" + describes + '\'' +
                ", patrol_list_detail=" + patrol_list_detail +
                ", patrol_list='" + patrol_list + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", patrol_type='" + patrol_type + '\'' +
                ", project_key='" + project_key + '\'' +
                ", must_list='" + must_list + '\'' +
                '}';
    }
}
