package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.kunlun.firmwaresystem.entity.Tag;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Devicep {
    long update_time;
    @TableId(type = IdType.AUTO) // 确保 ID 为自增
    int id;
    String name;
    int company_id;
    long createtime;
    long lasttime;
    String bind_mac;
    String userkey;
    String sn;
    String project_key;
    String customer_key;
    String describes;
    String tagf_id;
    int group_id;
    @TableField(exist = false)
    String group_name;
    @TableField(exist = false)
    String company_name;
    @TableField(exist = false)
    String[] tagfs_id;
    @TableField(exist = false)
    List<Tagf> tagfs;
    @TableField(exist = false)
    String station_type;

    int online=0;
    int fence_id;
    int fence_group_id;
    @TableField(exist = false)
    String f_g_name;
    @TableField(exist = false)
    String f_name;

    String map_key;

    String map_name;
    @TableField(exist = false)
    String type;

    double x,y;
    @TableField(exist = false)
    int sos;
    @TableField(exist = false)
    int run;

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }

    public String getStation_type() {
        return station_type;
    }

    String near_s_address;

    String near_s_name;

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_name() {
        return f_name;
    }

    public int getCompany_id() {
        return company_id;
    }
    long first_time;
    public void setFirst_time(long first_time) {
        this.first_time = first_time;
    }

    public long getFirst_time() {
        return first_time;
    }
    public void setNear_s_address(String near_s_address) {
        this.near_s_address = near_s_address;
    }

    public void setNear_s_name(String near_s_name) {
        this.near_s_name = near_s_name;
    }

    public String getNear_s_address() {
        return near_s_address;
    }

    public String getNear_s_name() {
        return near_s_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getRun() {
        return run;
    }
    @TableField(exist = false)
    double bt;

    public void setBt(double bt) {
        this.bt = bt;
    }

    public double getBt() {
        return bt;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getSos() {
        return sos;
    }

    public String getType() {
        return type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setF_g_name(String f_g_name) {
        this.f_g_name = f_g_name;
    }

    public String getF_g_name() {
        return f_g_name;
    }

    public void setFence_group_id(int fence_group_id) {
        this.fence_group_id = fence_group_id;
    }

    public void setFence_id(int fence_id) {
        this.fence_id = fence_id;
    }

    public int getFence_group_id() {
        return fence_group_id;
    }

    public int getFence_id() {
        return fence_id;
    }

    public Devicep() {

    }

    public Devicep(String name,
                   String bind_mac,
                   String userkey,
                   String sn   , String customer_key) {
        this.createtime = System.currentTimeMillis()/1000;
        this.bind_mac = bind_mac;
        this.userkey = userkey;
        this.sn = sn;
        this.name = name;
        this.customer_key=customer_key;
    }

    public int getId() {
        return id;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getDescribes() {
        return describes;
    }

    public void setUpdate_time(long update_time) {
            this.update_time = update_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setGroup_name(String group_name) {
            this.group_name = group_name;
    }


    public String getGroup_name() {
        return group_name;
    }

    public String getTagf_id() {
        return tagf_id;
    }


    public void setTagfs(List<Tagf> tagfs) {
        this.tagfs = tagfs;
    }

    public List<Tagf> getTagfs() {
        return tagfs;
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

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public String getBind_mac() {
        return bind_mac;
    }

    public void setBind_mac(String bind_mac) {
        this.bind_mac = bind_mac;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    public void setTagf_id(String tag_name) {
        if(tag_name.isEmpty()){
            this.tagf_id = "";
            return;
        }
        String[] tagfs_id=tag_name.split("-9635241-");
        if(tagfs_id.length>=1){
            this.tagfs_id=tagfs_id;
        }
        this.tagf_id = tag_name;
    }

    public String[] getTagfs_id() {
        if(tagfs_id!=null&&tagfs_id.length>0){
            String name="";
            for(int i=0;i<tagfs_id.length;i++){
                name+=tagfs_id[i]+"-9635241-";
            }
            this.tagf_id = name;
        }
        return tagfs_id;
    }

    public void setTagfs_id(String[] tagf_ids) {
        //myPrintln("tag_names:" + Arrays.toString(tagf_ids));
        this.tagfs_id=tagf_ids;
        if(tagf_ids!=null&&tagf_ids.length>0){
            String name="";
            for(int i=0;i<tagf_ids.length;i++){
                name+=tagf_ids[i]+"-9635241-";
            }
            this.tagf_id = name;
        }

    }

    @Override
    public String toString() {
        return "Devicep{" +
                "update_time=" + update_time +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", createtime=" + createtime +
                ", lasttime=" + lasttime +
                ", bind_mac='" + bind_mac + '\'' +
                ", userkey='" + userkey + '\'' +
                ", sn='" + sn + '\'' +
                ", project_key='" + project_key + '\'' +
                ", customer_key='" + customer_key + '\'' +
                ", describes='" + describes + '\'' +
                ", tagf_id='" + tagf_id + '\'' +
                ", group_id=" + group_id +
                ", group_name='" + group_name + '\'' +
                ", tagfs_id=" + Arrays.toString(tagfs_id) +
                ", tagfs=" + tagfs +
                ", online=" + online +
                ", fence_id=" + fence_id +
                ", fence_group_id=" + fence_group_id +
                ", f_g_name='" + f_g_name + '\'' +
                ", map_key='" + map_key + '\'' +
                ", type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", sos=" + sos +
                ", run=" + run +
                ", bt=" + bt +
                '}';
    }
}