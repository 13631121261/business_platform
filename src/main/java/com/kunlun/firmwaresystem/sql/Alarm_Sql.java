package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.HistoryMapper;

import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Alarm_Sql {
    public boolean addAlarm(AlarmMapper alarmMapper, Alarm alarm) {
        int status=alarmMapper.insert(alarm);
        myPrintln(status+"添加记录"+alarm.toString());
            return true;
    }
    public void deleteBy15Day(AlarmMapper alarmMapper, long time){
        QueryWrapper<Alarm> queryWrapper = Wrappers.query();
        queryWrapper.le("create_time",time);
       // myPrintln("时间");
        alarmMapper.delete(queryWrapper);

    }
    public PageAlarm selectPageAlarm(AlarmMapper alarmMapper, int page, int limt, String project_key,String object,String alarm_type,String name) {

        LambdaQueryWrapper<Alarm> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Alarm> userPage = new Page<>(page, limt);
        IPage<Alarm> userIPage;
        if(alarm_type!=null&&!alarm_type.equals("sos_all")){
            userLambdaQueryWrapper.like(Alarm::getAlarm_type, alarm_type).like(Alarm::getAlarm_object, object).like(Alarm::getSn, name).eq(Alarm::getProject_key,project_key).orderByDesc(true,Alarm::getId)
                             .or().like(Alarm::getAlarm_type, alarm_type).like(Alarm::getAlarm_object, object).like(Alarm::getName, name).eq(Alarm::getProject_key,project_key).orderByDesc(true,Alarm::getId);
        }else{

            userLambdaQueryWrapper.like(Alarm::getAlarm_object, object).like(Alarm::getSn, name).eq(Alarm::getProject_key,project_key).orderByDesc(true,Alarm::getId)
            .or().like(Alarm::getAlarm_object, object).like(Alarm::getName, name).eq(Alarm::getProject_key,project_key).orderByDesc(true,Alarm::getId);
        }
         userIPage = alarmMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageAlarm pageAlarm = new PageAlarm(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageAlarm;
    }
    public  PageAlarm pageAlarm_getHistory(AlarmMapper alarmMapper,String sn,long start_time,long stop_time,String project_key,int page, int limt) {

        QueryWrapper<Alarm > queryWrapper = Wrappers.query();
        Page<Alarm> userPage = new Page<>(page, limt);
        IPage<Alarm> userIPage;
        queryWrapper.eq("project_key",project_key).ge("create_time",start_time).le("create_time",stop_time).eq("sn",sn);
        userIPage = alarmMapper.selectPage(userPage, queryWrapper);
        PageAlarm pageAlarm = new PageAlarm(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageAlarm;
    }
    public List<Alarm> selectByOneDay(AlarmMapper alarmMapper,String project_key,long time){
        LambdaQueryWrapper<Alarm> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Alarm::getProject_key, project_key).ge(Alarm::getCreate_time,time).orderByDesc(true,Alarm::getId);
        List<Alarm> alarms=alarmMapper.selectList(userLambdaQueryWrapper);
        return alarms;

    }
    public  void deletes(List<Integer> ids){

    }
}