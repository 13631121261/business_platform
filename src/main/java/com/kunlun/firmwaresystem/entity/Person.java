package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Person {
    int company_id;
    int id;
    String name;
    String phone;
    int sex;
    String photo;
    int department_id;
    int fence_id;
    int fence_group_id;
    @TableField(exist = false)
    String fence_name;
    @TableField(exist = false)
    String fence_group_name;
    @TableField(exist = false)
    String company_name;
    String department_name;
    long create_time;
    long lasttime;
    long update_time;
    String bind_mac;
    int isbind;
    String jobnumber;
    int isopen;
    double x;
    double y;
    String user_key;
    String project_key;
    String idcard;
    long first_time;
    int online;
    @TableField(exist = false)
    int run=-1;
    @TableField(exist = false)
    int sos=-1;


    @TableField(exist = false)
    String station_type;

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }

    public String getStation_type() {
        return station_type;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setFence_group_id(int fence_group_id) {
        this.fence_group_id = fence_group_id;
    }

    public void setFence_group_name(String fence_group_name) {
        this.fence_group_name = fence_group_name;
    }

    public String getFence_group_name() {
        return fence_group_name;
    }

    public int getFence_group_id() {
        return fence_group_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getSos() {
        return sos;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getRun() {
        return run;
    }

    String  Station_mac;

    String Station_name;

    String customer_key;
    //用于判断定位信标的类型
    int type;
    @TableField(exist = false)
    int b_area_id;
    @TableField(exist = false)
    String b_area_name;
    @TableField(exist = false)
    String map_name;

    String map_key;

    String patrol_list_id;
    @TableField(exist = false)
    String[] patrol_list_ids;
    @TableField(exist = false)
    List<Patrol_list>patrol_lists;

    public void setFirst_time(long first_time) {
        this.first_time = first_time;
    }

    public long getFirst_time() {
        return first_time;
    }

    public void setPatrol_lists(List<Patrol_list> patrol_lists) {
        this.patrol_lists = patrol_lists;
    }

    public List<Patrol_list> getPatrol_lists() {
        return patrol_lists;
    }

    public void setPatrol_list_id(String[] patrol_list_ids){
        if (patrol_list_ids != null){
            String ids="";
            for (int i=0;i<patrol_list_ids.length;i++){
                ids=ids+"-"+patrol_list_ids[i];
            }
            this.patrol_list_id = ids;
        }
    }

    public void setPatrol_list_ids(String patrol_list_id) {
        if (patrol_list_id != null){
                patrol_list_id= patrol_list_id.replaceAll("--","");
                if (patrol_list_id.startsWith("-"))
                {
                    patrol_list_id = patrol_list_id.substring(1);
                }
                String[] ids=patrol_list_id.split("-");
                if (ids.length>=1){
                    this.patrol_list_ids = ids;
                }


        }

    }

    public String[] getPatrol_list_ids() {
        return patrol_list_ids;
    }

    public void setPatrol_list_ids(String[] patrol_list_ids) {
        this.patrol_list_ids = patrol_list_ids;
    }

    public String getPatrol_list_id() {
        return patrol_list_id;
    }

    public void setPatrol_list_id(String patrol_list_id) {
        this.patrol_list_id = patrol_list_id;
    }

    public Person()
    {

    }
    public Person(String idcard,String name)
    {
        this.idcard=idcard;
        this.name=name;
    }
            /*public Person(    String name,
            String phone,
            int sex,
            String photo,
            int departmentid,
            String bind_mac,
            int isopen,
            String IDCard,
            String userkey,    String customer_key){
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this. name=name;
        this .phone=phone;
        this.idcard=IDCard;
        this. sex=sex;
        this. photo=photo;
        this. department_id=departmentid;
        this. create_time=System.currentTimeMillis()/1000;
        this.customer_key=customer_key;
        this. bind_mac=bind_mac;

        if(bind_mac!=null&&bind_mac.length()>0){
            isbind=1;
        }
        else{
            isbind=0;
        }
        this. isopen=isopen;
        this. userkey=userkey;
    }
*/

    public String getFence_name() {
        return fence_name;
    }

    public void setFence_name(String fence_name) {
        this.fence_name = fence_name;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getMap_key() {
        return map_key;
    }

    public String getStation_name() {
        return Station_name;
    }

    public void setStation_name(String Station_name) {
        this.Station_name = Station_name;
    }

    public void setStation_mac(String Station_mac) {
        this.Station_mac = Station_mac;
    }

    public String getStation_mac() {
        return Station_mac;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public void setB_area_name(String b_area_name) {
        this.b_area_name = b_area_name;
    }

    public void setB_area_id(int b_area_id) {
        this.b_area_id = b_area_id;
    }

    public String getMap_name() {
        return map_name;
    }

    public String getB_area_name() {
        return b_area_name;
    }

    public int getB_area_id() {
        return b_area_id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setFence_id(int fence_id) {
        this.fence_id = fence_id;
    }

    public int getFence_id() {
        return fence_id;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setOnline(int onLine) {
        this.online = onLine;
    }

    public int getOnline() {
        return online;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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


    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public long getUpdate_time() {
        return update_time;
    }

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

    @Override
    public String toString() {
        return "Person{" +
                "company_id=" + company_id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", sex=" + sex +
                ", photo='" + photo + '\'' +
                ", department_id=" + department_id +
                ", fence_id=" + fence_id +
                ", fence_group_id=" + fence_group_id +
                ", fence_name='" + fence_name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", department_name='" + department_name + '\'' +
                ", create_time=" + create_time +
                ", lasttime=" + lasttime +
                ", update_time=" + update_time +
                ", bind_mac='" + bind_mac + '\'' +
                ", isbind=" + isbind +
                ", jobnumber='" + jobnumber + '\'' +
                ", isopen=" + isopen +
                ", x=" + x +
                ", y=" + y +
                ", user_key='" + user_key + '\'' +
                ", project_key='" + project_key + '\'' +
                ", idcard='" + idcard + '\'' +
                ", first_time=" + first_time +
                ", online=" + online +
                ", run=" + run +
                ", sos=" + sos +
                ", Station_mac='" + Station_mac + '\'' +
                ", Station_name='" + Station_name + '\'' +
                ", customer_key='" + customer_key + '\'' +
                ", type=" + type +
                ", b_area_id=" + b_area_id +
                ", b_area_name='" + b_area_name + '\'' +
                ", map_name='" + map_name + '\'' +
                ", map_key='" + map_key + '\'' +
                ", patrol_list_id='" + patrol_list_id + '\'' +
                ", patrol_list_ids=" + Arrays.toString(patrol_list_ids) +
                ", patrol_lists=" + patrol_lists +
                '}';
    }
}
