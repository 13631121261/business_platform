package com.kunlun.firmwaresystem.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class
Device_offline {
    int id;
    String sn;
    String name;
    long  createtime;
    int rssi;
    double bt;
    String bind_mac;
    String photo;
    int type_id;
    String area_name;
    String Station_mac;
    long lasttime;
    String userkey;
    @TableField(exist = false)
    String type_name;
    @TableField(exist = false)
    String point_name;
    String customer_key;

    String keep_time;

    public Device_offline() {

    }

    public Device_offline(  String sn,
            String name,
            int rssi,
            double bt,
            String bind_mac,
            String photo,
            int type_id,
            String area_name,
            String Station_mac,
            long lasttime,String userkey ,String customer_key  ){
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date=new Date();
        createtime=System.currentTimeMillis()/1000;
        this.area_name=area_name;
        this.name=name;

        this.rssi=rssi;
        this.bt=bt;
        this.bind_mac=bind_mac;
        this.type_id=type_id;
        this.photo=photo;
        this.Station_mac=Station_mac;
        this.userkey=userkey;
        this.lasttime=lasttime;
        this.sn=sn;
        this.customer_key=customer_key;

        try{
            long now=date.getTime();
            long old=lasttime;
            long t=(now-old)/1000;
            long d1=t/(60*60*24);
            long h=t%(60*60*24)/(60*60);
            long m=t%(60*60*24)%(60*60)/60;
            long s1=t%(60*60*24)%(60*60)/60%60;
            keep_time=d1+"天"+h+"时"+m+"分"+s1+"秒";
            System.out.println("持续时间="+keep_time);
        }catch (Exception e){
            System.out.println("Device_offline时间格式异常，"+e.getMessage());
        }


    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setKeep_time(String keep_time) {
        this.keep_time = keep_time;
    }

    public String getKeep_time() {
        return keep_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public String getBind_mac() {
        return bind_mac;
    }

    public void setBind_mac(String bind_mac) {
        this.bind_mac = bind_mac;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getStation_mac() {
        return Station_mac;
    }

    public void setStation_mac(String Station_mac) {
        this.Station_mac = Station_mac;
    }

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getPoint_name() {
        return point_name;
    }

    public void setPoint_name(String point_name) {
        this.point_name = point_name;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }
}