package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageRecord;
import com.kunlun.firmwaresystem.entity.Record;
import com.kunlun.firmwaresystem.mappers.RecordMapper;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Record_Sql {
    public void addRecord(RecordMapper recordMapper, Record record) {
        // boolean status = check(recordMapper, record);
       // Tag tag = record.getTag();
        //record.setTag(null);
        recordMapper.insert(record);
        //record.setTag(tag);
    }

    /*
       public Map<String,Beacon> getAllBeacon(BeaconMapper beaconMapper){
           List<Beacon> beacons = beaconMapper.selectList(null);
           HashMap<String,Beacon> beaconHashMap=new HashMap<>();
           for(Beacon beacon:beacons) {
               //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
               beaconHashMap.put(beacon.getMac(),beacon);
           }
           return beaconHashMap;
       }
   */
    public void delete(RecordMapper recordMapper, String mac) {
        QueryWrapper<Record> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", mac);
        recordMapper.delete(queryWrapper);
    }

    public PageRecord selectPageRecord(RecordMapper recordMapper, int page, int limt, String mac,String userkey) {
        LambdaQueryWrapper<Record> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Record> userPage = new Page<>(page, limt);
        IPage<Record> userIPage;
        userLambdaQueryWrapper.eq(Record::getDevice_mac, mac);
        userLambdaQueryWrapper.eq(Record::getCustomerkey, userkey);
        userIPage = recordMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageRecord pageRecord = new PageRecord(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageRecord;
    }


}