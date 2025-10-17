package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageHistory;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.HistoryMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class History_Sql {
    public boolean addHistory(HistoryMapper historyMapper, History history) {
           historyMapper.insert(history);
            return true;
    }

    public  List<History> getAllHistory(HistoryMapper historyMapper,String sn) {
        QueryWrapper<History> queryWrapper = Wrappers.query();
        queryWrapper.eq("sn",sn);
        List<History> histories= historyMapper.selectList(queryWrapper);
        return histories;
    }
    public  List<History> getHistory(HistoryMapper historyMapper,String sn,String type,long start_time,long stop_time,String project_key) {
        QueryWrapper<History> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key).eq("type",type).ge("time",start_time).le("time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key");
       List<History> histories= historyMapper.selectList(queryWrapper);
        return histories;
    }
    public PageHistory  getHistory(HistoryMapper historyMapper,String sn,long start_time,long stop_time,String project_key,String company_id,String page,String limit) {
        Page<History> historyPage = new Page<>(Long.parseLong(page), Long.parseLong(limit));
        IPage<History> userIPage;
        QueryWrapper<History> queryWrapper = Wrappers.query();
        if(company_id==null||company_id.equals("0")){
            queryWrapper.eq("project_key",project_key).ge("start_time",start_time).le("start_time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key").ne("station_mac", "").isNotNull("station_mac").orderByDesc("id");

        }
        else {
            queryWrapper.eq("project_key",project_key).ge("start_time",start_time).le("start_time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key").ne("station_mac", "").isNotNull("station_mac").eq("company_id",company_id).orderByDesc("id");

        }
        userIPage = historyMapper.selectPage(historyPage, queryWrapper);
        return new PageHistory(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
    }
    public List<History>  getHistory(HistoryMapper historyMapper,String sn,long start_time,long stop_time,String project_key) {

        QueryWrapper<History> queryWrapper = Wrappers.query();

        queryWrapper.eq("project_key",project_key).ge("start_time",start_time).le("start_time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key").ne("station_mac", "").isNotNull("station_mac").orderByDesc("id");
        return historyMapper.selectList(queryWrapper);


    }
    public      List<History>  getHistory(HistoryMapper historyMapper,String sn,long start_time,long stop_time,String project_key,String company_id) {
        QueryWrapper<History> queryWrapper = Wrappers.query();
        if(company_id==null||company_id.equals("0")){
            queryWrapper.eq("project_key",project_key).ge("start_time",start_time).le("start_time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key").ne("station_mac", "").isNotNull("station_mac").orderByDesc("id");
        }
        else {
            queryWrapper.eq("project_key",project_key).ge("start_time",start_time).le("start_time",stop_time).like("sn",sn).ne("map_key", "").isNotNull("map_key").ne("station_mac", "").isNotNull("station_mac").eq("company_id",company_id).orderByDesc("id");
        }


        List<History> histories= historyMapper.selectList(queryWrapper);

        return histories;
    }
    public void deleteBy15Day(HistoryMapper historyMapper,long time){
        QueryWrapper<History> queryWrapper = Wrappers.query();
        queryWrapper.le("start_time",time);
        historyMapper.delete(queryWrapper);

    }
}