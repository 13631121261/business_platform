package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.kunlun.firmwaresystem.entity.Tag;

import java.text.SimpleDateFormat;

public class Devicep_record {
    int id;
    String name;
    String photo;
    long createtime;
    String lasttime;
    String bind_mac;
    int isbind;
    int isopen;
    String userkey;
    String sn;
    int type_id;
    @TableField(exist = false)
    String type_name;
    @TableField(exist = false)
    int sos;
    @TableField(exist = false)
    int online;
    @TableField(exist = false)
    String Station_mac;
    @TableField(exist = false)
    String point_name;
    @TableField(exist = false)
    int rssi;

    String idcard;
    String person_name;

    String customer_key;
    double bt;

    int outbound;

    public Devicep_record() {

    }

    public Devicep_record(int type_id, String name,
                          String photo,
                          String bind_mac,
                          int isbind,
                          int isopen,
                          String userkey,
                          String sn   , int outbound, String customer_key,String idcard,String person_name) {
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.createtime =System.currentTimeMillis()/1000;
        this.bind_mac = bind_mac;
        this.photo = photo;
        this.isbind = isbind;
        this.isopen = isopen;
        this.userkey = userkey;
        this.sn = sn;
        this.name = name;
        this.type_id = type_id;
        this.outbound=outbound;
        this.customer_key=customer_key;
        this.idcard=idcard;
        this.person_name=person_name;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
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

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
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

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_name() {
        return type_name;
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

    public void setOutbound(int outbound) {
        this.outbound = outbound;
    }

    public int getOutbound() {
        return outbound;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getIdcard() {
        return idcard;
    }

    public String getPerson_name() {
        return person_name;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", createtime='" + createtime + '\'' +
                ", lasttime='" + lasttime + '\'' +
                ", bind_mac='" + bind_mac + '\'' +
                ", isbind=" + isbind +
                ", isopen=" + isopen +
                ", userkey='" + userkey + '\'' +
                ", sn='" + sn + '\'' +
                ", type_id=" + type_id +
                ", type_name='" + type_name + '\'' +
                ", sos=" + sos +
                ", online=" + online +
                ", Station_mac='" + Station_mac + '\'' +
                ", point_name='" + point_name + '\'' +
                ", rssi=" + rssi +
                ", customer_key='" + customer_key + '\'' +
                ", bt=" + bt +
                ", outbound=" + outbound +
                '}';
    }
}