package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Arrays;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

@TableName("agroups") // 表名
public class Group {
    int id;

    String tag_name;
    @TableField(exist = false)
    String[] tag_names;
    String group_name;
    int type;
    String describes;
    long create_time;
    long update_time;
    String project_key;
    int count  ;
    int f_g_id;
    int f_id;

    public void setF_id(int f_id) {
        this.f_id = f_id;
    }

    public int getF_id() {
        return f_id;
    }

    public void setF_g_id(int f_g_id) {
        this.f_g_id = f_g_id;
    }

    public int getF_g_id() {
        return f_g_id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        if(tag_name==null||tag_name.isEmpty()){
            return;
        }
        String[] tag_names=tag_name.split("-9635241-");
        if(tag_names.length>=1){
            this.tag_names=tag_names;
        }
        this.tag_name = tag_name;
    }

    public String[] getTag_names() {
        if(tag_names!=null&&tag_names.length>0){
            String name="";
            for(int i=0;i<tag_names.length;i++){
                name+=tag_names[i]+"-9635241-";
            }
            this.tag_name = name;
        }
        return tag_names;
    }

    public void setTag_names(String[] tag_names) {
      //  myPrintln("tag_names:" + Arrays.toString(tag_names));
        this.tag_names = tag_names;
        if(tag_names!=null&&tag_names.length>0){
            String name="";
            for(int i=0;i<tag_names.length;i++){
                name+=tag_names[i]+"-9635241-";
            }
            this.tag_name = name;
        }

    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
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

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", tag_name='" + tag_name + '\'' +
                ", tag_names=" + Arrays.toString(tag_names) +
                ", group_name='" + group_name + '\'' +
                ", type=" + type +
                ", describes='" + describes + '\'' +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", project_key='" + project_key + '\'' +
                '}';
    }
}
