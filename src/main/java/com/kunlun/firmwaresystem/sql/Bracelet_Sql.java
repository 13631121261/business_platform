package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageBracelet;
import com.kunlun.firmwaresystem.entity.Bracelet;
import com.kunlun.firmwaresystem.mappers.BraceletMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Bracelet_Sql {
    public boolean addBracelet(BraceletMapper braceletMapper, Bracelet bracelet) {
        boolean status = check(braceletMapper, bracelet);
        if (status) {
            return false;
        } else {
            braceletMapper.insert(bracelet);
            QueryWrapper<Bracelet> queryWrapper = Wrappers.query();
            queryWrapper.eq("mac",bracelet.getMac());
            Bracelet bracelet1 =braceletMapper.selectOne(queryWrapper);
            //myPrintln("申请的ID="+ devicep1.getId());
            bracelet.setId(bracelet1.getId());
            return true;
        }
    }

    public boolean update(BraceletMapper braceletMapper, Bracelet bracelet) {
        braceletMapper.updateById(bracelet);
        return true;
    }

    public List<Bracelet> getBraceletByMac(BraceletMapper braceletMapper,String user_key,String project_key,String mac) {
        QueryWrapper<Bracelet> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", user_key);
        queryWrapper.eq("project_key", project_key);
        queryWrapper.eq("mac", mac);
        List<Bracelet> beacons = braceletMapper.selectList(queryWrapper);
        return beacons;
    }


    public Map<String, Bracelet> getAllBracelet(BraceletMapper braceletMapper) {
        LambdaQueryWrapper<Bracelet> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        List<Bracelet> beacons = braceletMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Bracelet> beaconHashMap = new HashMap<>();
        for (Bracelet beacon : beacons) {
            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            beaconHashMap.put(beacon.getMac(), beacon);
        }
        return beaconHashMap;
    }

    public List<Bracelet> getAllBracelet(BraceletMapper braceletMapper, String userkey, String project_key) {
        LambdaQueryWrapper<Bracelet> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Bracelet::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Bracelet::getProject_key, project_key);
        List<Bracelet> beacons = braceletMapper.selectList(userLambdaQueryWrapper);
        return beacons;
    }

    public List<Bracelet> getunAllBracelet(BraceletMapper braceletMapper, String userkey,String project_key,String type) {
        QueryWrapper<Bracelet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isbind",0);
        queryWrapper.eq("user_key",userkey);
        queryWrapper.eq("type",type);
        queryWrapper.eq("project_key",project_key);
        List<Bracelet> beaconList=braceletMapper.selectList(queryWrapper);
        return beaconList;
    }
    public List<Bracelet> getAllBracelet(BraceletMapper braceletMapper, String userkey,String project_key,String type) {
        QueryWrapper<Bracelet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key",userkey);
        queryWrapper.eq("type",type);
        queryWrapper.eq("project_key",project_key);
        List<Bracelet> beaconList=braceletMapper.selectList(queryWrapper);
        return beaconList;
    }
    public void delete(BraceletMapper braceletMapper, String mac) {
        QueryWrapper<Bracelet> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", mac);
        braceletMapper.delete(queryWrapper);
    }

    public PageBracelet selectPageBracelet(BraceletMapper braceletMapper, int page, int limt, String like, String userkey, String project_key) {
        LambdaQueryWrapper<Bracelet> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Bracelet> userPage = new Page<>(page, limt);
        IPage<Bracelet> userIPage;
        userLambdaQueryWrapper.ne(Bracelet::getType,5).eq(Bracelet::getProject_key,project_key).eq(Bracelet::getUser_key, userkey).like(Bracelet::getMac, like);
        userIPage = braceletMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageBracelet pageBracelet = new PageBracelet(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageBracelet;
    }


    public int deletes(BraceletMapper braceletMapper, List<Integer> id) {
        return braceletMapper.deleteBatchIds(id);
    }
    public boolean check(BraceletMapper braceletMapper, Bracelet bracelet) {
        QueryWrapper<Bracelet> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", bracelet.getMac());
        Bracelet bracelet1 = braceletMapper.selectOne(queryWrapper);
        if (bracelet1 == null) {
            return false;
        } else {
            return true;
        }
    }
}