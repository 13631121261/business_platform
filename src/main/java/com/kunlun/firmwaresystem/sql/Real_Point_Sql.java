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
}