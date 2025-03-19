package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Customer implements Serializable {

    String username;
    String password;
    String email;
    String create_time;
    String nickname;
    String userkey;
    String mobile;
    String status;
    String roles_id;
    @TableField(exist=false)
    int[] group_arr;
    @TableField(exist=false)
    String[] group_name_arr;
    @TableField(exist=false)
    Permission permission;

    @TableField(exist=false)
    String last_login_time;

    @TableField(exist=false)
    String token ;
    @TableField(exist=false)
    String lang ;
    @TableField(exist=false)
    String refresh_token;

    String permission_key;
    int id;
    String customerkey;
    String project_key;

    String update_time;
    String login_time;
    String login_ip;


    int type;
    public Customer() {

    }
    public Customer(String username, String password) {
        this.username = username;
        this.password = password;

    }

    public Customer(String username, String password,  String nickName, String phoneNumber,String permission_key,String userkey,String project_key,int type) {
        this.username = username;
        this.project_key=project_key;
        this.type=type;
        this.password = password;
        this.nickname = nickName;
        this.mobile = phoneNumber;
        this.permission_key=permission_key;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.create_time = sdf.format(date);
        this.userkey=userkey;
        //   myPrintln("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
        this.customerkey = userkey+"_"+Base64.getEncoder().encodeToString((username + "_" + date.getTime()).getBytes()).replaceAll("\\+", "");
        // myPrintln("现在时间：" + sdf.format(date)+"==="+userkey); // 输出已经格式化的现在时间（24小时制）

    }

    public void setType(int type) {
        this.type = type;
    }

    public void setGroup_arr(int[] group_arr) {
        this.group_arr = group_arr;
    }

    public void setGroup_name_arr(String[] group_name_arr) {
        this.group_name_arr = group_name_arr;
    }

    public int[] getGroup_arr() {
        return group_arr;
    }

    public String[] getGroup_name_arr() {
        return group_name_arr;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public int getType() {
        return type;
    }

    public String getProject_key() {
        return project_key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getCustomerkey() {
        return customerkey;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setCustomerkey(String customerkey) {
        this.customerkey = customerkey;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission_key(String permission_key) {
        this.permission_key = permission_key;
    }

    public String getPermission_key() {
        return permission_key;
    }






    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setRoles_id(String roles_id) {
        this.roles_id = roles_id;
    }

    public String getRoles_id() {

        return roles_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserkey() {
        return userkey;
    }

    public String getUsername() {
        return username;
    }


    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public String getLogin_ip() {
        return login_ip;
    }

    public String getLogin_time() {
        return login_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public Customer Customer(String json, Customer customer) {
        Gson gson = new Gson();
        customer = gson.fromJson(json, Customer.class);
        return customer;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
