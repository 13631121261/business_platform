package com.kunlun.firmwaresystem.sql;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.CallRecord;
import com.kunlun.firmwaresystem.entity.PageCallRecord;
import com.kunlun.firmwaresystem.mappers.CallRecordMapper;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class CallRecord_Sql {
    public boolean add(CallRecordMapper callRecordMapper, CallRecord callRecord) {
        callRecordMapper.insert(callRecord);
        return true;
    }

    public PageCallRecord selectPage(CallRecordMapper callRecordMapper, int page, int limit, String user_key, String project_key) {
        try {
            LambdaQueryWrapper<CallRecord> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            Page<CallRecord> userPage = new Page<>(page, limit);
            IPage<CallRecord> userIPage;
            userLambdaQueryWrapper.eq(CallRecord::getUser_key, user_key).eq(CallRecord::getProject_key, project_key).orderByDesc(true, CallRecord::getId);;
            userIPage = callRecordMapper.selectPage(userPage, userLambdaQueryWrapper);
            return new PageCallRecord(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
            return null;
        }
    }
}
