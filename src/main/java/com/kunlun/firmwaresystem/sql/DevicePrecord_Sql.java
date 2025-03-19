package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageDeviceP_record;
import com.kunlun.firmwaresystem.entity.device.Devicep_record;
import com.kunlun.firmwaresystem.mappers.DeviceP_recordMapper;

public class DevicePrecord_Sql {
    public boolean addDeviceP(DeviceP_recordMapper devicePMapper, Devicep_record deviceP) {

        devicePMapper.insert(deviceP);

        return true;

    }




    public void delete(DeviceP_recordMapper devicePMapper, String sn) {
        QueryWrapper<Devicep_record> queryWrapper = Wrappers.query();
        queryWrapper.eq("sn", sn);
        devicePMapper.delete(queryWrapper);
    }

    public PageDeviceP_record selectPageDeviceP(DeviceP_recordMapper devicePMapper, int page, int limt, String deviceSn, String name, int type, int bind, String userkey) {
        LambdaQueryWrapper<Devicep_record> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Devicep_record> userPage = new Page<>(page, limt);
        IPage<Devicep_record> userIPage;
        if (type != -1) {
            userLambdaQueryWrapper.eq(Devicep_record::getType_id, type);
        }
        if (bind != -1) {
            userLambdaQueryWrapper.eq(Devicep_record::getIsbind, bind);
        }
        userLambdaQueryWrapper.eq(Devicep_record::getOutbound, 0);
        userLambdaQueryWrapper.eq(Devicep_record::getUserkey, userkey);
        userLambdaQueryWrapper.like(Devicep_record::getSn, deviceSn);
        userLambdaQueryWrapper.like(Devicep_record::getName, name);
        userIPage = devicePMapper.selectPage(userPage, userLambdaQueryWrapper);
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageDeviceP_record pageDeviceP = new PageDeviceP_record(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageDeviceP;
    }

}