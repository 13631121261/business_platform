package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    @TableField(exist = false)
    String[][] time_set;
    @TableField(exist = false)
    int time_keep_out;
    @TableField(exist = false)
    int time_static_out;
    String time_out_set;
    boolean time_out_set_status;
    int time_keep;
    public Check_sheet(){

    }

    public void setTime_keep(int time_keep) {
        this.time_keep = time_keep;
    }

    public int getTime_keep() {
        return time_keep;
    }

    public void setTime_out_set_status(boolean time_out_set_status) {
        this.time_out_set_status = time_out_set_status;
    }

    public boolean isTime_out_set_status() {
        return time_out_set_status;
    }

    public void setTime_out_set(String time_out_set) {
        this.time_out_set = time_out_set;
    }

    public void setTime_out_set() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < time_set.length; i++) {
            if (time_set[i]!=null&&time_set[i].length > 0) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(time_set[i][0]).append(",").append(time_set[i][1]);
            }


        }

        this.time_out_set= time_static_out+"-"+time_keep_out+"-"+ sb;

    }
    public String getTime_out_set() {
        return time_out_set;
    }

    public void setTime_keep_out(int time_keep_out) {
        this.time_keep_out = time_keep_out;
    }

    public void setTime_static_out(int time_static_out) {
        this.time_static_out = time_static_out;
    }

    public int getTime_keep_out() {
        return time_keep_out;
    }

    public int getTime_static_out() {
        return time_static_out;
    }

    public String[][] getTime_set() {
        return time_set;
    }

    public void setTime_set(String[][] time_set) {
        this.time_set = time_set;
    }

    public void setTime_set(String time_sets) {
        if (time_sets == null|| time_sets.isEmpty()) {
            return;
        }
        String[] rows = time_sets.split("-");
        if (rows.length != 3) {
            return;
        }
        int time_static_out = Integer.parseInt(rows[0]);
        int time_keep_out = Integer.parseInt(rows[1]);
        rows = rows[2].split(" ");
        // 初始化二维数组
        String[][] result = new String[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            // 按逗号分割每行的元素
            String[] elements = rows[i].split(",");
            result[i] = new String[elements.length];

            for (int j = 0; j < elements.length; j++) {
                result[i][j] = elements[j];
            }
        }
        this.time_static_out=time_static_out;
        this.time_keep_out=time_keep_out;
        this.time_set = result;
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
                "project_key='" + project_key + '\'' +
                ", line_time=" + line_time +
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
                ", time_set=" + Arrays.toString(time_set) +
                ", time_static_out=" + time_static_out +
                ", time_keep_out=" + time_keep_out +
                ", time_out_set='" + time_out_set + '\'' +
                '}';
    }
}
