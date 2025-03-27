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

public class PatrolList {
    int id;
    String name;
    String description;
    @TableField(exist = false)
    List<Patrol> patrols;
    String patrol_str;
    long create_time;
    long update_time;
    String user_key;
    String project_key;

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Patrol> getPatrolInfos() {
        return patrols;
    }

    public void setPatrolInfos(List<Patrol> patrols) {
        if (patrols != null&& !patrols.isEmpty()) {
            this.patrols = patrols;
        }

    }

    public String getPatrol_str() {
        return patrol_str;
    }

    public void setPatrol_str(String patrol_str) {
        this.patrol_str = patrol_str;
        if (patrol_str != null && !patrol_str.isEmpty()) {
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
                    this.setPatrolInfos(patrols);
                }
            }catch (Exception e){
                myPrintln(e.getMessage());
            }

        }
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


}
