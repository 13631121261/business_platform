package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageCheckRecord;
import com.kunlun.firmwaresystem.entity.Check_record;
import com.kunlun.firmwaresystem.mappers.CheckRecordMapper;

public class CheckRecord_Sql {
    public boolean addRecord(CheckRecordMapper recordMapper, Check_record check_record) {
        recordMapper.insert(check_record);
        return true;
    }






    public void delete(CheckRecordMapper checkRecordMapper, int id) {
        QueryWrapper<Check_record> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        checkRecordMapper.delete(queryWrapper);
    }

    public PageCheckRecord selectPageRecord(CheckRecordMapper checkRecordMapper, int page, int limt,String time,int type,String userkey) {
        LambdaQueryWrapper<Check_record> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Check_record> userPage = new Page<>(page, limt);
        IPage<Check_record> userIPage;
        if(type!=-1){
            userLambdaQueryWrapper.eq(Check_record::getCheck_type, type);
        }
        //List<Check_record> list = checkRecordMapper.list(new QueryWrapper<Check_record>().eq("account_id", user.getId()).orderByDesc("id"));
        userLambdaQueryWrapper.like(Check_record::getCreatetime, time);
        userLambdaQueryWrapper.eq(Check_record::getUserkey, userkey);
        userLambdaQueryWrapper.orderByDesc(Check_record::getId);
        userIPage = checkRecordMapper.selectPage(userPage, userLambdaQueryWrapper);
        System.out.println("总页数： " + userIPage.getPages());
        System.out.println("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::println);
        PageCheckRecord pageCheckRecord = new PageCheckRecord(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageCheckRecord;
    }


}