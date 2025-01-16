package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageBeaconTag;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Beacon_tag;
import com.kunlun.firmwaresystem.mappers.BTagMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Btag_Sql {
    public boolean addBeaconTag(BTagMapper bTagMapper, Beacon_tag beacon_tag) {
        boolean status = check(bTagMapper, beacon_tag);
        if (status) {
            return false;
        } else {
            bTagMapper.insert(beacon_tag);
            return true;
        }
    }


    public Map<String, Beacon_tag> getAllBeacon(BTagMapper beaconMapper,String userkey) {
        QueryWrapper<Beacon_tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", userkey);
        List<Beacon_tag> beacons = beaconMapper.selectList(queryWrapper);
        HashMap<String, Beacon_tag> beaconHashMap = new HashMap();
        for (Beacon_tag beacon_tag : beacons) {
            System.out.println("初始化" + beacon_tag.getProject_key() + beacon_tag.getMajor() + "/" + beacon_tag.getMinor());
            beaconHashMap.put(beacon_tag.getProject_key() + beacon_tag.getMajor() + "/" + beacon_tag.getMinor(), beacon_tag);
        }
        return beaconHashMap;
    }
    public Map<String, Beacon_tag> getAllBeacon(BTagMapper beaconMapper) {
        QueryWrapper<Beacon_tag> queryWrapper = Wrappers.query();

        List<Beacon_tag> beacons = beaconMapper.selectList(queryWrapper);
        HashMap<String, Beacon_tag> beaconHashMap = new HashMap();
        for (Beacon_tag beacon_tag : beacons) {
            System.out.println("初始化" + beacon_tag.getProject_key() + beacon_tag.getMajor() + "/" + beacon_tag.getMinor());
            beaconHashMap.put(beacon_tag.getProject_key() + beacon_tag.getMajor() + "/" + beacon_tag.getMinor(), beacon_tag);
        }
        return beaconHashMap;
    }
    public void delete(BTagMapper beaconMapper, int id) {
        QueryWrapper<Beacon_tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        beaconMapper.delete(queryWrapper);
    }

    public boolean update(BTagMapper beaconMapper, Beacon_tag beacon_tag) {
        beaconMapper.updateById(beacon_tag);
        return true;
    }

    public PageBeaconTag selectPageTag(BTagMapper bTagMapper, int page, int limt, String project_name, String user_key, String deviceName) {
        LambdaQueryWrapper<Beacon_tag> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Beacon_tag> userPage = new Page<>(page, limt);
        IPage<Beacon_tag> userIPage;
        userLambdaQueryWrapper.like(Beacon_tag::getProject_name, project_name);
        userLambdaQueryWrapper.like(Beacon_tag::getName, deviceName);
        userLambdaQueryWrapper.eq(Beacon_tag::getCustomer_key, user_key);
        userIPage = bTagMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageBeaconTag pageRecord = new PageBeaconTag(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageRecord;
    }

    /**
     * 查询是否已存在此用户
     */
    public boolean check(BTagMapper bTagMapper, Beacon_tag beacon_tag) {
        QueryWrapper<Beacon_tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key", beacon_tag.getProject_key());
        queryWrapper.eq("user_key", beacon_tag.getCustomer_key());
        queryWrapper.eq("major", beacon_tag.getMajor());
        queryWrapper.eq("minor", beacon_tag.getMinor());
        // queryWrapper.eq("username", user.getCustomername());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Beacon_tag> a = bTagMapper.selectList(queryWrapper);
        if (a != null && a.size() > 0) {
            return true;
        } else{
            return false;
        }
    }
    public Beacon_tag getOne(BTagMapper bTagMapper,int  major,int minor) {
        QueryWrapper<Beacon_tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("major",major);
        queryWrapper.eq("minor", minor);
        // queryWrapper.eq("username", user.getCustomername());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Beacon_tag> a = bTagMapper.selectList(queryWrapper);
        if (a != null && a.size() ==1) {
            return a.get(0);
        } else
            return null;
    }
}