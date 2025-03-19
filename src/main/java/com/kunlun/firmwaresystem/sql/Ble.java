package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageBleVersion;
import com.kunlun.firmwaresystem.entity.Ble_firmware;
import com.kunlun.firmwaresystem.mappers.BleMapper;

import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Ble {
    public Ble_firmware getVersionByKey(BleMapper bleMapper, String userKey, String version) {
        QueryWrapper<Ble_firmware> queryWrapper = Wrappers.query();
        queryWrapper.eq("version", version);
        queryWrapper.eq("user_key", userKey);
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        Ble_firmware a = bleMapper.selectOne(queryWrapper);
        return a;
    }

    public List<Ble_firmware> getCustomerVersion(BleMapper bleMapper, String userkey) {
        QueryWrapper<Ble_firmware> queryWrapper = Wrappers.query();
        queryWrapper.like("user_key", userkey);
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Ble_firmware> a = bleMapper.selectList(queryWrapper);
        return a;
    }

    public PageBleVersion selectPageBleVersion(BleMapper bleMapper, int page, int limt, String remake, String version, String userKey) {
        LambdaQueryWrapper<Ble_firmware> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Ble_firmware> userPage = new Page<>(page, limt);
        IPage<Ble_firmware> userIPage;
        userLambdaQueryWrapper.eq(Ble_firmware::getUser_key, userKey);
        if (remake != null) {
            userLambdaQueryWrapper.like(Ble_firmware::getRemake, remake);
            userLambdaQueryWrapper.like(Ble_firmware::getVersion, version);
        }
        userIPage = bleMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        //   userIPage.getRecords().forEach(System.out::myPrintln);
        PageBleVersion pageBleVersion = new PageBleVersion(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageBleVersion;
    }

    public int delete(BleMapper bleMapper, String userKey, String version) {
        QueryWrapper<Ble_firmware> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", userKey);
        queryWrapper.eq("version", version);
        return bleMapper.delete(queryWrapper);
    }
}
