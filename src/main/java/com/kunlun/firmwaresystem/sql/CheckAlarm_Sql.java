package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.check.Check_alarm;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.CheckAlarmMapper;

import java.util.List;

public class CheckAlarm_Sql {
    public boolean addCheckAlarm(CheckAlarmMapper checkAlarmMapper, Check_alarm check_alarm) {
        checkAlarmMapper.insert(check_alarm);
            return true;
    }

    public  void deletes(List<Integer> ids){

    }
}