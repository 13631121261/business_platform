package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PagePermission;
import com.kunlun.firmwaresystem.entity.Permission;
import com.kunlun.firmwaresystem.mappers.PermissionMapper;

import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Permission_Sql {
    public boolean addPermission(PermissionMapper permissionMapper, Permission permission) {
       boolean status = check(permissionMapper, permission);
        if (status) {
            return false;
        } else {
            permissionMapper.insert(permission);
            return true;
        }

    }


    public void delete(PermissionMapper permissionMapper, String name,String user_key) {
        QueryWrapper<Permission> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", name);
        queryWrapper.eq("user_key", user_key);
        permissionMapper.delete(queryWrapper);
    }
    public List<Permission> seleceByOne(PermissionMapper permissionMapper, String permission_key,String user_key) {
        QueryWrapper<Permission> queryWrapper = Wrappers.query();
        queryWrapper.eq("permission_key", permission_key);
        queryWrapper.eq("user_key", user_key);
        List<Permission> list= permissionMapper.selectList(queryWrapper);
        return list;
    }
    public PagePermission selectPagePermission(PermissionMapper permissionMapper, int page, int limt,String userkey, String name) {
        LambdaQueryWrapper<Permission> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Permission> userPage = new Page<>(page, limt);
        IPage<Permission> userIPage;
        userLambdaQueryWrapper.eq(Permission::getCustomer_key, userkey);
        userLambdaQueryWrapper.like(Permission::getName, name);
        userIPage = permissionMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PagePermission pagePermission = new PagePermission(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pagePermission;
    }

    public boolean check(PermissionMapper permissionMapper, Permission permission) {
        QueryWrapper<Permission> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", permission.getName());
        queryWrapper.eq("user_key", permission.getCustomer_key());
        Permission permission1 = permissionMapper.selectOne(queryWrapper);
        if (permission1 == null) {
            return false;
        } else {
            return true;
        }
    }
}