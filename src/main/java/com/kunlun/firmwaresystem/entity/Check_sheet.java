package com.kunlun.firmwaresystem.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Check_sheet {
    String project_key;
    int line_time;
    String udp;
    int id;
    String name;
    long  createtime;
    String       userkey;
    String host;
      int      port;
    String sub;
    String   user;
    String pub;
    String    password;
   int qos;
    int   relay_type;
    String r_host;
           int r_port;
    String r_sub;
    String   r_pub;
    String r_user;
    String    r_password;
    int relay_status;
        int     person_l;
    int device_l;
    int  tag_l;
    int  online;
    int     offline;
    int fence;
    int     low_p;
    int detach;
    int     move;
    public Check_sheet(){

    }
    public void setLine_time(int line_time) {
        this.line_time = line_time;
    }

    public int getLine_time() {
        return line_time;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getUdp() {
        return udp;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getProject_key() {
        return project_key;
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

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public int getRelay_type() {
        return relay_type;
    }

    public void setRelay_type(int relay_type) {
        this.relay_type = relay_type;
    }

    public String getR_host() {
        return r_host;
    }

    public void setR_host(String r_host) {
        this.r_host = r_host;
    }

    public int getR_port() {
        return r_port;
    }

    public void setR_port(int r_port) {
        this.r_port = r_port;
    }

    public String getR_sub() {
        return r_sub;
    }

    public void setR_sub(String r_sub) {
        this.r_sub = r_sub;
    }

    public String getR_pub() {
        return r_pub;
    }

    public void setR_pub(String r_pub) {
        this.r_pub = r_pub;
    }

    public String getR_user() {
        return r_user;
    }

    public void setR_user(String r_user) {
        this.r_user = r_user;
    }

    public String getR_password() {
        return r_password;
    }

    public void setR_password(String r_password) {
        this.r_password = r_password;
    }

    public int getRelay_status() {
        return relay_status;
    }

    public void setRelay_status(int relay_status) {
        this.relay_status = relay_status;
    }

    public int getPerson_l() {
        return person_l;
    }

    public void setPerson_l(int person_l) {
        this.person_l = person_l;
    }

    public int getDevice_l() {
        return device_l;
    }

    public void setDevice_l(int device_l) {
        this.device_l = device_l;
    }

    public int getTag_l() {
        return tag_l;
    }

    public void setTag_l(int tag_l) {
        this.tag_l = tag_l;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getFence() {
        return fence;
    }

    public void setFence(int fence) {
        this.fence = fence;
    }

    public int getLow_p() {
        return low_p;
    }

    public void setLow_p(int low_p) {
        this.low_p = low_p;
    }

    public int getDetach() {
        return detach;
    }

    public void setDetach(int detach) {
        this.detach = detach;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "Check_sheet{" +
                "line_time=" + line_time +
                ", udp='" + udp + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", createtime=" + createtime +
                ", userkey='" + userkey + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", sub='" + sub + '\'' +
                ", user='" + user + '\'' +
                ", pub='" + pub + '\'' +
                ", password='" + password + '\'' +
                ", qos=" + qos +
                ", relay_type=" + relay_type +
                ", r_host='" + r_host + '\'' +
                ", r_port=" + r_port +
                ", r_sub='" + r_sub + '\'' +
                ", r_pub='" + r_pub + '\'' +
                ", r_user='" + r_user + '\'' +
                ", r_password='" + r_password + '\'' +
                ", relay_status=" + relay_status +
                ", person_l=" + person_l +
                ", device_l=" + device_l +
                ", tag_l=" + tag_l +
                ", online=" + online +
                ", offline=" + offline +
                ", fence=" + fence +
                ", low_p=" + low_p +
                ", detach=" + detach +
                ", move=" + move +
                '}';
    }
}
