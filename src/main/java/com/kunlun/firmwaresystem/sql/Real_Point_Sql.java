package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Real_Point;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.Real_PointMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Real_Point_Sql {
    public boolean add(Real_PointMapper realPointMapper, Real_Point realPoint) {
        realPointMapper.insert(realPoint);
            return true;
    }


    public  void deletes(Real_PointMapper realPointMapper,String  idcard){
        QueryWrapper<Real_Point > queryWrapper = Wrappers.query();
        queryWrapper.eq("idcard",idcard);
        realPointMapper.delete(queryWrapper);
    }
    public    List<Real_Point> select_One_day(Real_PointMapper realPointMapper,String  idcard ){
        // 获取今天零点时间
        long midnightTimestamp = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()/1000;
      long nowTimestamp = System.currentTimeMillis()/1000;
        myPrintln("当前时间="+midnightTimestamp);
        myPrintln("当前时间="+nowTimestamp);
        // 构建查询条件（假设字段名为 create_time）
        QueryWrapper<Real_Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", midnightTimestamp, nowTimestamp).eq(true,"idcard",idcard);
        return realPointMapper.selectList(queryWrapper);

    }
}