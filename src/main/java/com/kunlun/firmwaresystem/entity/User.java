package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class User implements Serializable {

    String username;
    String password;
    String email;
    String create_time;
    String nickName;
    String userkey;
    String phoneNumber;
    @TableField(exist=false)
    Permission permission;
    String permission_key;
    int id;

    public User() {

    }

    public User(String username, String password,  String nickName, String phoneNumber,String permission_key) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.permission_key=permission_key;
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.create_time = sdf.format(date);
        //   myPrintln("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
        userkey = Base64.getEncoder().encodeToString((username + "_" + date.getTime()).getBytes()).replaceAll("\\+", "");
        // myPrintln("现在时间：" + sdf.format(date)+"==="+userkey); // 输出已经格式化的现在时间（24小时制）

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

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomername() {
        return username;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserkey() {
        return userkey;
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

    public User getCustomer(String json, User user) {
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
        return user;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
