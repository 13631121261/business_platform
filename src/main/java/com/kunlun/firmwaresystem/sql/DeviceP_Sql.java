package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.NewSystemApplication;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.DevicePMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceP_Sql {
    public boolean addDeviceP(DevicePMapper devicePMapper, Devicep deviceP) {
        if(check(devicePMapper,deviceP)){
            return false;
        }else {
            devicePMapper.insert(deviceP);
            QueryWrapper<Devicep> queryWrapper = Wrappers.query();
            queryWrapper.eq("sn",deviceP.getSn());
            Devicep devicep1 =devicePMapper.selectOne(queryWrapper);
            deviceP.setId(devicep1.getId());
        }
           /* ;*/
            return true;
    }
 public Devicep getOrderbyId(DevicePMapper devicePMapper){
     QueryWrapper<Devicep> queryWrapper = Wrappers.query();
     queryWrapper.last("limit 1");
     queryWrapper.orderByDesc("id");
    Devicep devicep= devicePMapper.selectOne(queryWrapper);
    return devicep;

 }
    public boolean update(DevicePMapper devicePMapper, Devicep deviceP) {

        devicePMapper.updateById(deviceP);
        return true;
    }
    public boolean update(DevicePMapper devicePMapper, String sn) {
        UpdateWrapper<Devicep> queryWrapper =new UpdateWrapper();
        queryWrapper.set("outbound",0);
        queryWrapper.eq("sn",sn);
        devicePMapper.update(null,queryWrapper);
        return true;
    }
    public Map<String, Devicep> getAllDeviceP(DevicePMapper devicePMapper) {
        List<Devicep> deviceps = devicePMapper.selectList(null);
        HashMap<String, Devicep> devicepHashMap = new HashMap<>();
        for (Devicep deviceP : deviceps) {
            devicepHashMap.put(deviceP.getSn(), deviceP);
        }
        return devicepHashMap;
    }
    public Map<String, Devicep> getAllDeviceP(DevicePMapper devicePMapper,String userkey,String projectkey) {
        QueryWrapper<Devicep> queryWrapper = Wrappers.query();
        queryWrapper.eq("userkey",userkey);
        queryWrapper.eq("project_key",projectkey);
        List<Devicep> deviceps = devicePMapper.selectList(queryWrapper);
        HashMap<String, Devicep> devicepHashMap = new HashMap<>();
        for (Devicep deviceP : deviceps) {


            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            devicepHashMap.put(deviceP.getSn(), deviceP);
        }
        return devicepHashMap;
    }
    public  List<Devicep> getDevicePBySn(DevicePMapper devicePMapper, String sn) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getSn, sn);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }

    public  List<Devicep> getDevicePByLike(DevicePMapper devicePMapper, String sn, String name,String project_key) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getProject_key, project_key).like(Devicep::getSn,sn).or()
                .eq(Devicep::getProject_key, project_key).like(Devicep::getName,name);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
    public  HashMap<String, Devicep> getDeviceP(DevicePMapper devicePMapper, String type) {

        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getType, type);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Devicep> devicePHashMap = new HashMap<>();
        for (Devicep deviceP : deviceps) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            devicePHashMap.put(deviceP.getSn(), deviceP);
        }
        return devicePHashMap;
    }
    public    List<Devicep>  getDeviceByAreaID(DevicePMapper devicePMapper, int area_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getArea_id, area_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
    public    List<Devicep>  getDeviceByGroupID(DevicePMapper devicePMapper, int group_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getGroup_id, group_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
    public    List<Devicep>  getDeviceByFenceID(DevicePMapper devicePMapper, int fence_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getFence_id, fence_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
 /*   public Map<String, Beacon> getAllBeacon(BeaconMapper beaconMapper, String project_key) {
        LambdaQueryWrapper<Beacon> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Beacon::getProject_key, project_key);
        List<Beacon> beacons = beaconMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Beacon> beaconHashMap = new HashMap<>();
        for (Beacon beacon : beacons) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            beaconHashMap.put(beacon.getMac(), beacon);
        }
        return beaconHashMap;
    }*/

    /*public List<Beacon> getAllDeviceP(DevicePMapper devicePMapper, String userkey, String project_key) {
        LambdaQueryWrapper<Beacon> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Beacon::getCustomer_key, userkey);
        userLambdaQueryWrapper.eq(Beacon::getProject_key, project_key);
        List<Beacon> beacons = beaconMapper.selectList(userLambdaQueryWrapper);
        return beacons;
    }*/

    public void delete(DevicePMapper devicePMapper, String sn) {
        QueryWrapper<Devicep> queryWrapper = Wrappers.query();
        queryWrapper.eq("sn", sn);
        devicePMapper.delete(queryWrapper);
    }

    public PageDeviceP selectPageDeviceP(DevicePMapper devicePMapper, int page, int limt, String search,String user_key,String project_key) {

       try {
           LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
           Page<Devicep> userPage = new Page<>(page, limt);
           IPage<Devicep> userIPage;
           //System.out.println("key="+user_key+"  "+project_key+"  s="+search);

           userLambdaQueryWrapper.eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).like(Devicep::getSn, search).or()
                   .eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).like(Devicep::getPerson_name, search).or()
                   .eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).like(Devicep::getName, search);
           userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
           PageDeviceP pageDeviceP = new PageDeviceP(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
           return pageDeviceP;
       }catch (Exception e){
           System.out.println("异常="+e.getMessage());
           return null;
       }
    }

    public boolean check(DevicePMapper devicePMapper, Devicep deviceP) {
        QueryWrapper<Devicep> queryWrapper = Wrappers.query();
        queryWrapper.eq("sn", deviceP.getSn());
        Devicep devicep1 = devicePMapper.selectOne(queryWrapper);
        if (devicep1 == null) {
            return false;
        } else {
            return true;
        }
    }
}