package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageLogs;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.entity.Logs;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.HistoryMapper;
import com.kunlun.firmwaresystem.mappers.LogsMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logs_Sql {
    public int add(LogsMapper logsMapper, Logs area) {
        return 0;// logsMapper.insert(area);
    }
    public void deleteBy15Day(LogsMapper logsMapper, long time){
        QueryWrapper<Logs> queryWrapper = Wrappers.query();
        queryWrapper. le("create_time",time);
        logsMapper.delete(queryWrapper);

    }

    public void delete(LogsMapper logsMapper, int id) {
        QueryWrapper<Logs> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        logsMapper.delete(queryWrapper);
    }

    public PageLogs selectPageLogs(LogsMapper logsMapper, int page, int limt, String like, String userkey,String project_key) {
        System.out.println(userkey+" = "+project_key+" = "+like);
        LambdaQueryWrapper<Logs> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Logs> userPage = new Page<>(page, limt);
        IPage<Logs> userIPage;
        userLambdaQueryWrapper.eq(Logs::getProject_key,project_key).eq(Logs::getUserkey, userkey).like(Logs::getOperation, like).orderByDesc(true,Logs::getId).or()
                              .eq(Logs::getProject_key,project_key).eq(Logs::getUserkey, userkey).like(Logs::getNickname, like).orderByDesc(true,Logs::getId).or()
                              .eq(Logs::getProject_key,project_key).eq(Logs::getUserkey, userkey).like(Logs::getUsername, like).orderByDesc(true,Logs::getId);
        userIPage = logsMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::println);
        PageLogs pageArea = new PageLogs(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageArea;
    }

    public   Map<Integer,Area> getAllArea(AreaMapper areaMapper) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();

        List<Area> areas= areaMapper.selectList(queryWrapper);
        Map<Integer,Area> areaHashMap=new HashMap<>();
        for(Area area:areas){
            areaHashMap.put(area.getId(),area);
        }
        return areaHashMap;

    }
    public boolean check(AreaMapper areaMapper, Area area) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", area.getName());
        Area area1 = areaMapper.selectOne(queryWrapper);
        if (area1 == null) {
            return false;
        } else {
            return true;
        }
    }
}