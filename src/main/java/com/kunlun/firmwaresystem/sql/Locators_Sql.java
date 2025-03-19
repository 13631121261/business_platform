package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageLocator;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Locator;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.LocatorMapper;

import java.util.List;

public class Locators_Sql {
    public boolean addLocator(LocatorMapper locatorMapper, Locator locator) {
        if(check(locatorMapper,locator)){
            return false;
        }else{
            locatorMapper.insert(locator);
            QueryWrapper<Locator> queryWrapper = Wrappers.query();
            queryWrapper.eq("address",locator.getAddress());
            Locator locator1 =locatorMapper.selectOne(queryWrapper);
            //myPrintln("申请的ID="+ devicep1.getId());
            locator.setId(locator1.getId());
            return true;
        }
    }

    public PageLocator selectPageLocator(LocatorMapper locatorMapper, int page, int limt, String project_key,String search) {
        LambdaQueryWrapper<Locator> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Locator> userPage = new Page<>(page, limt);
        IPage<Locator> userIPage;
        userLambdaQueryWrapper.eq(Locator::getProject_key, project_key).like(Locator::getName, search)
                .or().eq(Locator::getProject_key,project_key).like(Locator::getModel_name, search)
                .or().eq(Locator::getProject_key,project_key).like(Locator::getAddress, search);


      /*  userLambdaQueryWrapper.eq(Alarm::getProject_key, project_key).like(Alarm::getAlarm_object, object).like(Alarm::getAlarm_type, alarm_type).like(Alarm::getSn, name)
                .or().eq(Alarm::getProject_key, project_key).like(Alarm::getAlarm_object, object).like(Alarm::getAlarm_type, alarm_type).like(Alarm::getName, name);
         */ userIPage = locatorMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageLocator pageAlarm = new PageLocator(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageAlarm;
    }
    public int update(LocatorMapper locatorMapper, Locator locator){
        return locatorMapper.updateById(locator);
    }
    public   List<Locator> selectByMap(LocatorMapper locatorMapper,String project_key, String map_key) {
        QueryWrapper<Locator> queryWrapper = Wrappers.query();
        queryWrapper.eq("map_key",map_key);
        queryWrapper.eq("project_key",project_key);
        List<Locator> a = locatorMapper.selectList(queryWrapper);
        return a;
    }
    public boolean check(LocatorMapper locatorMapper, Locator locator) {
        QueryWrapper<Locator> queryWrapper = Wrappers.query();
        queryWrapper.eq("address",locator.getAddress());

        // queryWrapper.eq("username", user.getCustomername());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Locator> a = locatorMapper.selectList(queryWrapper);
        if (a != null && a.size() > 0) {
            return true;
        } else
            return false;
    }
}