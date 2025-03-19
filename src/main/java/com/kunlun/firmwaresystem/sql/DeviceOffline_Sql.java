package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageDeviceOffline;
import com.kunlun.firmwaresystem.entity.device.Device_offline;
import com.kunlun.firmwaresystem.mappers.DeviceOfflineMapper;

import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class DeviceOffline_Sql {
    public boolean addDeviceOffline(DeviceOfflineMapper deviceOfflineMapper, Device_offline device_offline) {
        if(!check(deviceOfflineMapper,device_offline)){
            myPrintln("离线插入=");
            deviceOfflineMapper.insert(device_offline);
        }else{
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("sn", device_offline.getSn());
            updateWrapper.eq("userkey", device_offline.getUserkey());
            updateWrapper.eq("lasttime", device_offline.getLasttime());
            updateWrapper.set("createtime",device_offline.getCreatetime());
            updateWrapper.set("keep_time",device_offline.getKeep_time());
           int n=  deviceOfflineMapper.update(null,updateWrapper);
            myPrintln("离线更新="+n);
        }
            return true;
    }

      public boolean check(DeviceOfflineMapper deviceOfflineMapper, Device_offline device_offline) {
          QueryWrapper<Device_offline> queryWrapper = Wrappers.query();
          queryWrapper.eq("sn", device_offline.getSn());
          queryWrapper.eq("sn", device_offline.getSn());
          queryWrapper.eq("userkey", device_offline.getUserkey());
          queryWrapper.eq("lasttime", device_offline.getLasttime());
          List<Device_offline> device_offline1 = deviceOfflineMapper.selectList(queryWrapper);
          if(device_offline1.size()>0){
              return true;

          }else{
              return false;
          }
      }

    public Device_offline gettime(DeviceOfflineMapper deviceOfflineMapper, String sn,String userKey) {
        LambdaQueryWrapper<Device_offline> userLambdaQueryWrapper = Wrappers.lambdaQuery();

        userLambdaQueryWrapper.eq(Device_offline::getSn, sn);
        userLambdaQueryWrapper.eq(Device_offline::getUserkey, userKey);
        List<Device_offline> device_offline1 = deviceOfflineMapper.selectList(userLambdaQueryWrapper);
        if(device_offline1.size()>0){
           return device_offline1.get(device_offline1.size()-1);
        }else{
            return null;
        }
    }
    public void delete(DeviceOfflineMapper deviceOfflineMapper, int id) {
        QueryWrapper<Device_offline> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        deviceOfflineMapper.delete(queryWrapper);
    }

    public PageDeviceOffline selectPage(DeviceOfflineMapper deviceOfflineMapper, int page, int limt, String name,String sn,String userkey) {
        LambdaQueryWrapper<Device_offline> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Device_offline> userPage = new Page<>(page, limt);
        IPage<Device_offline> userIPage;

        userLambdaQueryWrapper.like(Device_offline::getName, name);
        userLambdaQueryWrapper.eq(Device_offline::getUserkey, userkey);
        userLambdaQueryWrapper.like(Device_offline::getSn, sn);
        userLambdaQueryWrapper.orderByDesc(Device_offline::getId);
        userIPage = deviceOfflineMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageDeviceOffline pageDeviceOffline = new PageDeviceOffline(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageDeviceOffline;
    }



}