package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.kunlun.firmwaresystem.entity.Tag;

import java.text.SimpleDateFormat;

public class Devicep {

    String fence_id;
    int id;
    String name;
    String photo;
    long createtime;
    long lasttime;
    String bind_mac;
    int isbind;
    int isopen;
    String userkey;
    String sn;
    String type;
    @TableField(exist = false)
    String fence_name;
    @TableField(exist = false)
    int sos;
    @TableField(exist = false)
    int online;
    @TableField(exist = false)
    String Station_mac;
    @TableField(exist = false)
    String Station_name;
    @TableField(exist = false)
    String point_name;
    @TableField(exist = false)
    int rssi;

    @TableField(exist = false)
    String map_name;
    @TableField(exist = false)
    String map_key;
    int open_run;
    String project_key;
    String idcard;
    String person_name;
    String atime,btime;
    String customer_key;
    double bt;



    int is_area;
    int area_id;
    @TableField(exist = false)
    double x;
    @TableField(exist = false)
    double y;
    @TableField(exist = false)
    int run;
    @TableField(exist = false)
    String area_name;
    @TableField(exist = false)
    int b_area_id;
    @TableField(exist = false)
    String b_area_name;
    @TableField(exist = false)
    int area_sos;

    public Devicep() {

    }

    public Devicep(String type, String name,
                   String photo,
                   String bind_mac,
                   int isbind,
                   int isopen,
                   String userkey,
                   String sn   , String customer_key,String idcard,String person_name) {
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.createtime = System.currentTimeMillis()/1000;
        this.bind_mac = bind_mac;
        this.photo = photo;
        this.isbind = isbind;
        this.isopen = isopen;
        this.userkey = userkey;
        this.sn = sn;
        this.name = name;
        this.type=type;

        this.customer_key=customer_key;
        this.idcard=idcard;
        this.person_name=person_name;
    }

    public void setStation_name(String Station_name) {
        this.Station_name = Station_name;
    }

    public String getStation_name() {
        return Station_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setFence_name(String fence_name) {
        this.fence_name = fence_name;
    }

    public String getFence_name() {
        return fence_name;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setArea_sos(int area_sos) {
        this.area_sos = area_sos;
    }

    public int getArea_sos() {
        return area_sos;
    }



    public void setFence_id(String fence_id) {
        this.fence_id = fence_id;
    }

    public String getFence_id() {
        return fence_id;
    }



    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setB_area_id(int b_area_id) {
        this.b_area_id = b_area_id;
    }

    public int getB_area_id() {
        return b_area_id;
    }

    public void setB_area_name(String b_area_name) {
        this.b_area_name = b_area_name;
    }

    public String getB_area_name() {
        return b_area_name;
    }



    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public void setOpen_run(int open_run) {
        this.open_run = open_run;
    }

    public int getOpen_run() {
        return open_run;
    }

    public int getRun() {
        return run;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public int getIsbind() {
        return isbind;
    }

    public void setIsbind(int isbind) {
        this.isbind = isbind;
    }

    public int getIsopen() {
        return isopen;
    }

    public void setIsopen(int isopen) {
        this.isopen = isopen;
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



    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getSos() {
        return sos;
    }

    public void setPoint_name(String point_name) {
        this.point_name = point_name;
    }

    public void setStation_mac(String Station_mac) {
        this.Station_mac = Station_mac;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public String getPoint_name() {
        return point_name;
    }

    public String getStation_mac() {
        return Station_mac;
    }

    public int getRssi() {
        return rssi;
    }

    public double getBt() {
        return bt;
    }


    public void setAtime(String atime) {
        this.atime = atime;
    }

    public String getPerson_name() {
        return person_name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public void setBtime(String btime) {
        this.btime = btime;
    }

    public String getAtime() {
        return atime;
    }

    public String getBtime() {
        return btime;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }





    public String getProject_key() {
        return project_key;
    }



    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getIs_area() {
        return is_area;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setIs_area(int is_area) {
        this.is_area = is_area;
    }

    public void unbind(){
        this.bt=0;
        this.rssi=0;
        this.bind_mac="";
        this.isopen=0;
        this.isbind=0;
        this.Station_mac="";
        this.point_name="";
    }
    public void bind(Tag tag){
        this.bt= tag.getBt();
        this.rssi= tag.getRssi();
        this.bind_mac= tag.getMac();
        this.isopen=1;
        this.isbind=1;
    }

    @Override
    public String toString() {
        return "Devicep{" +
                "fence_id=" + fence_id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", createtime=" + createtime +
                ", lasttime=" + lasttime +
                ", bind_mac='" + bind_mac + '\'' +
                ", isbind=" + isbind +
                ", isopen=" + isopen +
                ", userkey='" + userkey + '\'' +
                ", sn='" + sn + '\'' +
                ", type='" + type + '\'' +
                ", fence_name='" + fence_name + '\'' +
                ", sos=" + sos +
                ", online=" + online +
                ", Station_mac='" + Station_mac + '\'' +
                ", Station_name='" + Station_name + '\'' +
                ", point_name='" + point_name + '\'' +
                ", rssi=" + rssi +
                ", map_name='" + map_name + '\'' +
                ", map_key='" + map_key + '\'' +
                ", open_run=" + open_run +
                ", project_key='" + project_key + '\'' +
                ", idcard='" + idcard + '\'' +
                ", person_name='" + person_name + '\'' +
                ", atime='" + atime + '\'' +
                ", btime='" + btime + '\'' +
                ", customer_key='" + customer_key + '\'' +
                ", bt=" + bt +
                ", is_area=" + is_area +
                ", area_id=" + area_id +
                ", x=" + x +
                ", y=" + y +
                ", run=" + run +
                ", area_name='" + area_name + '\'' +
                ", b_area_id=" + b_area_id +
                ", b_area_name='" + b_area_name + '\'' +
                ", area_sos=" + area_sos +
                '}';
    }
}