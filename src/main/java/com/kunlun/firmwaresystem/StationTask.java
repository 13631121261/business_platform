package com.kunlun.firmwaresystem;

import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.CheckRecordMapper;
import com.kunlun.firmwaresystem.mappers.DevicePMapper;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.mappers.TagMapper;
import com.kunlun.firmwaresystem.sql.Alarm_Sql;
import com.kunlun.firmwaresystem.sql.Real_Point_Sql;
import com.kunlun.firmwaresystem.util.PIDTimeTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;
import static com.kunlun.firmwaresystem.util.PIDTimeTracker.calculateStayTimeSegments;

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
    @Autowired
    private TagMapper tagMapper;

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "*/59 * * * * ?")
    public void execute() throws Exception
    {
        Alarm_Sql alarm_sql = new Alarm_Sql();
        myPrintln("一分钟一次"+df.format(System.currentTimeMillis()));
        long start = System.currentTimeMillis()/1000;
        List<Devicep> devicepList = new ArrayList<>();
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
                   devicepList.add(devicep);
                  // devicePMapper.updateById(devicep);
               }
           }catch (Exception e) {
               myPrintln("定时资产更新异常"+e.toString());
           }
       }
        devicePMapper.updateBatch(devicepList);
        List<Person> personList=new ArrayList<>();
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
                   personList.add(person);
                   if(person.getId()==812){
                      // myPrintln("执行更新一次全部");
                       List<Person> personLists=new ArrayList<>();
                     //  myPrintln("person="+person);
                       personLists.add(person);
                      // int status=  personMapper.updateBatch(personLists);
                       //   myPrintln("定时的保存="+status);
                   }
                   //personMapper.updateById(person);
               }
           }catch (Exception e) {
               myPrintln("定时人员更新异常"+e.toString());
           }
       }
        myPrintln("运行时间-"+System.currentTimeMillis());
        long time=System.currentTimeMillis();
       int status=  personMapper.updateBatch(personList);
     //   myPrintln("定时的保存="+status);
        myPrintln("运行时间-"+System.currentTimeMillis());
        myPrintln("人员结束时间-"+(System.currentTimeMillis()-time));

       for (String key:station_maps.keySet()){
            Station station=(Station) redisUtil.get(redis_key_locator+key);
        if (station != null) {
            long end = station.getLast_time();
            if (start - end>300&&station.getOnline()==1) {

                station.setOnline(0);
                redisUtil.setnoTimeOut(redis_key_locator+key,station);
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
       long a=System.currentTimeMillis();
       myPrintln("标签开始更新-"+a);
        List<Tag> tags=new ArrayList<>();
        for (String key:tagsMap.keySet()){
          Tag tag=tagsMap.get(key);
            if (tag != null) {
                long end = tag.getLastTime();
                if (start - end>300&&tag.getOnline()==1) {
                    tag.setOnline(0);
                }
                tags.add(tag);
               // tagMapper.updateById(tag);
            }
        }
        myPrintln("标签开始更新-"+(System.currentTimeMillis()-a));
        long b=System.currentTimeMillis();
        tagMapper.updateBatch(tags);

        myPrintln("标签结束更新-"+(System.currentTimeMillis()-b));

    }
    // 或者使用 cron 表达式（推荐）
    @Scheduled(cron = "*/59 * * * * ?") // 每小时的第0分钟执行
    public void hourlyTaskWithCron() {
        //一开始用于巡更检测的
       // myPrintln("Cron 定时任务执行，当前时间：" + System.currentTimeMillis()/1000);
      //  Real_Point_Sql   realPointSql = new Real_Point_Sql();
        /*for (Person person : personMap.values()) {
            myPrintln("人员是="+person.getName());
            List<Real_Point> realPoints= realPointSql.select_One_day(realPointMapper,person.getIdcard());

            myPrintln("数据长度="+realPoints.size());
            Map<Integer, List<PIDTimeTracker.TimeSegment>> result = calculateStayTimeSegments(realPoints);
            // 输出结果
            result.forEach((pid, segments) -> {
                System.out.println("PID: " + pid);
               // segments.forEach(System.out::println);
                //System.out.println();
            });
        }*/

        //每分钟更新一下历史记录
        for(History history:historyMap.values()) {
            historyMapper.updateById(history);
        }
    }


}