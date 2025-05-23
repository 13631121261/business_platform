package com.kunlun.firmwaresystem;

import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.CheckRecordMapper;
import com.kunlun.firmwaresystem.mappers.DevicePMapper;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.sql.Alarm_Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;

@Component
public class StationTask {



    int runcount = 0;
    int runH=0;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");//设置日期格式1
    @Autowired
    private DevicePMapper devicePMapper;
    @Autowired
    private com.kunlun.firmwaresystem.mappers.StationMapper stationMapper;

    @Scheduled(cron = "*/59 * * * * ?")
    public void execute() throws Exception
    {
        Alarm_Sql alarm_sql = new Alarm_Sql();
        //myPrintln("一分钟一次");
        long start = System.currentTimeMillis()/1000;
       for (Devicep devicep : devicePMap.values()) {
           try {
               if (devicep != null) {
                   long end = devicep.getLasttime();
                   if (start - end>300&&devicep.getOnline()==1) {
                       devicep.setOnline(0);
                       Alarm alarm=new Alarm();
                       alarm.setAlarm_object(Alarm_object.person);
                       alarm.setAlarm_type(Alarm_Type.sos_offline);
                       alarm.setName(devicep.getName());
                       alarm.setSn(devicep.getSn());
                       alarm.setMap_key(devicep.getMap_key());
                       alarm.setProject_key(devicep.getProject_key());
                       alarm.setCreate_time(System.currentTimeMillis()/1000);
                       alarm_sql.addAlarm(alarmMapper, alarm);
                   }
                   devicePMapper.updateById(devicep);
               }
           }catch (Exception e) {
               myPrintln("定时资产更新异常"+e.toString());
           }
       }
       for(Person person : personMap.values()) {
           try {
               if (person != null) {

                   long end = person.getLasttime();
                   if (start - end>300&&person.getOnline()==1) {
                       person.setOnline(0);
                       Alarm alarm=new Alarm();
                       alarm.setAlarm_object(Alarm_object.person);
                       alarm.setAlarm_type(Alarm_Type.sos_offline);
                       alarm.setName(person.getName());
                       alarm.setSn(person.getIdcard());
                       alarm.setMap_key(person.getMap_key());
                       alarm.setProject_key(person.getProject_key());
                       alarm.setCreate_time(System.currentTimeMillis()/1000);
                       alarm_sql.addAlarm(alarmMapper, alarm);
                   }
                   personMapper.updateById(person);
               }
           }catch (Exception e) {
               myPrintln("定时人员更新异常"+e.toString());
           }
       }
       for (String key:station_maps.keySet()){
            Station station=(Station) redisUtil.get(redis_key_locator+key);
        if (station != null) {
            long end = station.getLast_time();
            if (start - end>300&&station.getOnline()==1) {

                station.setOnline(0);
                redisUtil.set(redis_key_locator+key,station);
                stationMapper.updateById(station);
                Alarm alarm=new Alarm();
                alarm.setAlarm_object(Alarm_object.locator);
                alarm.setAlarm_type(Alarm_Type.sos_offline);
                alarm.setName(station.getName());
                alarm.setSn(station.getAddress());
                alarm.setMap_key(station.getMap_key());
                alarm.setProject_key(station.getProject_key());
                alarm.setCreate_time(System.currentTimeMillis()/1000);
                alarm_sql.addAlarm(alarmMapper, alarm);
            }else{

            }

        }

       }

        for (String key:tagsMap.keySet()){
          Tag tag=tagsMap.get(key);
            if (tag != null) {
                long end = tag.getLastTime();
                if (start - end>300&&tag.getOnline()==1) {
                    tag.setOnline(0);
                }
                tagMapper.updateById(tag);
            }

        }


    }


}