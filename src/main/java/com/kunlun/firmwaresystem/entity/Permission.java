package com.kunlun.firmwaresystem.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Permission  implements Serializable {

    String name;
    int editbeacon;
    int editwordcard;
    int editStation;
    int lookbeacon;
    int lookword;
    int lookStation;
    int editperson;
    int lookperson;
    int editasset;
    int lookasset;
    int editmap;
    int lookmap;
    int editdepartment;
    int lookdepartment;
    String createtime;
    String edittime;
    String permission_key;
    String user_key;
    String customer_key;
    int id;
    int edituser;
    public Permission (){

    }
    public Permission (  int editdepartment,
            int lookdepartment, int edituser,  String name,
            int editbeacon,
            int editwordcard,
            int editStation,
            int lookbeacon,
            int lookword,
            int lookStation,
            int editperson,
            int lookperson,
            int editasset,
            int lookasset,
            int editmap,
            int lookmap,String user_key,    String customer_key){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        this.createtime = sdf.format(date);
        this.name=name;
        this.edituser=edituser;
        this.editbeacon=editbeacon;
        this.editwordcard=editwordcard;
        this.editStation=editStation;
        this.lookbeacon=lookbeacon;
        this.lookword=lookword;
        this.lookStation=lookStation;
        this.editperson=editperson;
        this.lookperson=lookperson;
        this.editasset=editasset;
        this.lookasset=lookasset;
        this.editmap=editmap;
        this.lookmap=lookmap;
        this.user_key=user_key;
        this.editdepartment=editdepartment;
        this.lookdepartment=lookdepartment;
        this.customer_key=customer_key;
        this.permission_key =user_key+"_"+Base64.getEncoder().encodeToString((name + "_" + date.getTime()).getBytes()).replaceAll("\\+", "");
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public int getEdituser() {
        return edituser;
    }

    public void setEdituser(int edituser) {
        this.edituser = edituser;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
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

    public int getEditbeacon() {
        return editbeacon;
    }

    public void setEditbeacon(int editbeacon) {
        this.editbeacon = editbeacon;
    }

    public int getEditwordcard() {
        return editwordcard;
    }

    public void setEditwordcard(int editwordcard) {
        this.editwordcard = editwordcard;
    }

    public int getEditStation() {
        return editStation;
    }

    public void setEditStation(int editStation) {
        this.editStation = editStation;
    }

    public int getLookbeacon() {
        return lookbeacon;
    }

    public void setLookbeacon(int lookbeacon) {
        this.lookbeacon = lookbeacon;
    }

    public int getLookword() {
        return lookword;
    }

    public void setLookword(int lookword) {
        this.lookword = lookword;
    }

    public int getLookStation() {
        return lookStation;
    }

    public void setLookStation(int lookStation) {
        this.lookStation = lookStation;
    }

    public int getEditperson() {
        return editperson;
    }

    public void setEditperson(int editperson) {
        this.editperson = editperson;
    }

    public int getLookperson() {
        return lookperson;
    }

    public void setLookperson(int lookperson) {
        this.lookperson = lookperson;
    }

    public int getEditasset() {
        return editasset;
    }

    public void setEditasset(int editasset) {
        this.editasset = editasset;
    }

    public int getLookasset() {
        return lookasset;
    }

    public void setLookasset(int lookasset) {
        this.lookasset = lookasset;
    }

    public int getEditmap() {
        return editmap;
    }

    public void setEditmap(int editmap) {
        this.editmap = editmap;
    }

    public int getLookmap() {
        return lookmap;
    }

    public void setLookmap(int lookmap) {
        this.lookmap = lookmap;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getEdittime() {
        return edittime;
    }

    public void setEdittime(String edittime) {
        this.edittime = edittime;
    }

    public String getPermission_key() {
        return permission_key;
    }

    public void setPermission_key(String key) {
        this.permission_key = key;
    }

    public int getEditdepartment() {
        return editdepartment;
    }

    public void setEditdepartment(int editdepartment) {
        this.editdepartment = editdepartment;
    }

    public int getLookdepartment() {
        return lookdepartment;
    }

    public void setLookdepartment(int lookdepartment) {
        this.lookdepartment = lookdepartment;
    }
}
