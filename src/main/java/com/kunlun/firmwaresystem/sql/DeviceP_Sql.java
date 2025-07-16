package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.DevicePMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

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


            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            devicepHashMap.put(deviceP.getSn(), deviceP);
        }
        return devicepHashMap;
    }
    public  List<Devicep> getDevicePBySn(DevicePMapper devicePMapper, String sn) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getSn, sn);
        return devicePMapper.selectList(userLambdaQueryWrapper);
    }

    public  List<Devicep> getDevicePByLike(DevicePMapper devicePMapper, String sn, String name,String project_key) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getProject_key, project_key).like(Devicep::getSn,sn).or()
                .eq(Devicep::getProject_key, project_key).like(Devicep::getName,name);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }


//根据设备组或者围栏组
    public   PageDeviceP  getDeviceByGroupIdOrF_G_id(String project_key, DevicePMapper devicePMapper, List<Integer> group_id, String f_g_id, String page, String limt) {
        try {
            LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            Page<Devicep> userPage = new Page<>(Long.parseLong(page), Long.parseLong(limt));
            IPage<Devicep> userIPage;
        if(group_id!=null&& !group_id.isEmpty()){
            userLambdaQueryWrapper.in(Devicep::getGroup_id,group_id).eq(Devicep::getProject_key, project_key);
        }else{
            userLambdaQueryWrapper.eq(Devicep::getProject_key, project_key).eq(Devicep::getFence_group_id,f_g_id);
        }

            userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
            return new PageDeviceP(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
            return null;
        }

    }

    //根据围栏
    public   List<Devicep>  getDeviceByFence_id(String project_key, DevicePMapper devicePMapper, String f_id) {
        try {
            LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();

            userLambdaQueryWrapper.eq(Devicep::getFence_id,f_id).eq(Devicep::getProject_key, project_key);

            List<Devicep> deviceps= devicePMapper.selectList(userLambdaQueryWrapper);
            return deviceps ;
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
            return null;
        }

    }
    //根据公司
    public    List<Devicep>  getDeviceByGroupID(DevicePMapper devicePMapper, int group_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getGroup_id, group_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
    //根据设备组
    public    List<Devicep>  getDeviceByCompany(DevicePMapper devicePMapper, int company_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getCompany_id, company_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }
    //根据围栏组
    public    List<Devicep>  getDeviceByFenceGroupID(DevicePMapper devicePMapper, int group_id) {
        LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Devicep::getFence_group_id, group_id);
        List<Devicep> deviceps = devicePMapper.selectList(userLambdaQueryWrapper);
        return deviceps;
    }

 /*   public Map<String, Beacon> getAllBeacon(BeaconMapper beaconMapper, String project_key) {
        LambdaQueryWrapper<Beacon> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Beacon::getProject_key, project_key);
        List<Beacon> beacons = beaconMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Beacon> beaconHashMap = new HashMap<>();
        for (Beacon beacon : beacons) {
            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
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
           //myPrintln("key="+user_key+"  "+project_key+"  s="+search);

           userLambdaQueryWrapper.eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).like(Devicep::getSn, search).or()
                   .eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).like(Devicep::getName, search);
           userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
           PageDeviceP pageDeviceP = new PageDeviceP(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
           return pageDeviceP;
       }catch (Exception e){
           myPrintln("异常="+e.getMessage());
           return null;
       }
    }

    public PageDeviceP selectPageDevicePByGroup(DevicePMapper devicePMapper, int page, int limt, String search,String user_key,String project_key,int group_id) {

        try {
            LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            Page<Devicep> userPage = new Page<>(page, limt);
            IPage<Devicep> userIPage;
            //myPrintln("key="+user_key+"  "+project_key+"  s="+search);

            userLambdaQueryWrapper.eq(Devicep::getUserkey, user_key).eq(Devicep::getGroup_id,group_id).eq(Devicep::getProject_key, project_key).like(Devicep::getSn, search).or()
                                  .eq(Devicep::getUserkey, user_key).eq(Devicep::getProject_key, project_key).eq(Devicep::getGroup_id,group_id).like(Devicep::getName, search);
            userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
            PageDeviceP pageDeviceP = new PageDeviceP(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
            return pageDeviceP;
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
            return null;
        }
    }
    public PageDeviceP selectPageDevicePByCompany(DevicePMapper devicePMapper, int page, int limt, String project_key, int company_id) {

        try {
            LambdaQueryWrapper<Devicep> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            Page<Devicep> userPage = new Page<>(page, limt);
            IPage<Devicep> userIPage;
            userLambdaQueryWrapper.eq(Devicep::getCompany_id,company_id).eq(Devicep::getProject_key, project_key);
            userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
            PageDeviceP pageDeviceP = new PageDeviceP(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
            return pageDeviceP;
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
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