package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageRecordSos;
import com.kunlun.firmwaresystem.entity.Record_sos;
import com.kunlun.firmwaresystem.mappers.Record_SosMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordSos_Sql {
    public void addRecordSos(Record_SosMapper recordMapper, Record_sos record) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        updateWrapper.eq("mac", record.getMac());
        updateWrapper.eq("handle", 0);
        Record_sos record_sos = recordMapper.selectOne(updateWrapper);
        if (record_sos != null) {
            System.out.println("只更新");
            record.setId(record_sos.getId());
            recordMapper.updateById(record);
        } else {
            System.out.println("只插入");
            recordMapper.insert(record);
        }

    }

    public int editBeaconSos(Record_SosMapper record_sosMapper, int id) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        updateWrapper.eq("id", id);
        updateWrapper.set("handle", 1);
        updateWrapper.set("handle_time", df.format(day));
        return record_sosMapper.update(null, updateWrapper);
    }

    /*
       public Map<String,Beacon> getAllBeacon(BeaconMapper beaconMapper){
           List<Beacon> beacons = beaconMapper.selectList(null);
           HashMap<String,Beacon> beaconHashMap=new HashMap<>();
           for(Beacon beacon:beacons) {
               //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
               beaconHashMap.put(beacon.getMac(),beacon);
           }
           return beaconHashMap;
       }
   */
    public void delete(Record_SosMapper record_sosMapper, int id) {
        QueryWrapper<Record_sos> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        record_sosMapper.delete(queryWrapper);

    }

    public PageRecordSos selectPageRecord(Record_SosMapper recordMapper, int page, int limt, String mac, String name, int handle, String userkey) {
        LambdaQueryWrapper<Record_sos> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Record_sos> userPage = new Page<>(page, limt);
        IPage<Record_sos> userIPage;
        userLambdaQueryWrapper.like(Record_sos::getMac, mac);
        userLambdaQueryWrapper.like(Record_sos::getName, name);
        userLambdaQueryWrapper.eq(Record_sos::getHandle, handle);
        userLambdaQueryWrapper.eq(Record_sos::getUserkey, userkey);
        userIPage = recordMapper.selectPage(userPage, userLambdaQueryWrapper);
        System.out.println("总页数： " + userIPage.getPages());
        System.out.println("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::println);
        PageRecordSos pageRecord = new PageRecordSos(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageRecord;
    }


 /*   public PageRecordSos selectPageRecord_bt(Record_SosMapper recordMapper,String userkey) {
        LambdaQueryWrapper<Record_sos> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Record_sos::getCustomerKey, userkey);
        userLambdaQueryWrapper.select(Record_sos.class, info -> !info.getColumn().equals("mac")
                && !info.getColumn().equals("create_time"));
        userLambdaQueryWrapper.like(Record_sos::getDevice_name, name);
        userIPage = recordMapper.selectPage(userPage, userLambdaQueryWrapper);
        System.out.println("总页数： " + userIPage.getPages());
        System.out.println("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::println);
        PageRecordSos pageRecord = new PageRecordSos(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageRecord;
    }*/


    public List<Record_sos> selectPageRecordDay(Record_SosMapper recordMapper) {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(day);
        LambdaQueryWrapper<Record_sos> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        try {
            Date start = df.parse((time.split(" ")[0] + " 00:00:00"));
            Date end = df.parse((time.split(" ")[0] + " 23:59:59"));
            userLambdaQueryWrapper.between(Record_sos::getTime, start, end);
            List<Record_sos> list = recordMapper.selectList(userLambdaQueryWrapper);
            return list;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}