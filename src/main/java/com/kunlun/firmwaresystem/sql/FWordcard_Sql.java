package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageFWordcard;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.FWordcard;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.FWordcardMapper;

import java.util.List;

public class FWordcard_Sql {
    public boolean addWordcard(FWordcardMapper fWordcardMapper, FWordcard wordcard) {
           fWordcardMapper.insert(wordcard);
            return true;
    }
public FWordcard getOne(FWordcardMapper fWordcardMapper,String imei){
    LambdaQueryWrapper<FWordcard> userLambdaQueryWrapper = Wrappers.lambdaQuery();
    userLambdaQueryWrapper.eq(FWordcard::getImei,imei);
    FWordcard fWordcard= fWordcardMapper.selectOne(userLambdaQueryWrapper);
    return fWordcard;

}
    public PageFWordcard selectPageFWordcard(FWordcardMapper fWordcardMapper, int page, int limt, String project_key,String imei) {

        LambdaQueryWrapper<FWordcard> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<FWordcard> userPage = new Page<>(page, limt);
        IPage<FWordcard> userIPage;
        userLambdaQueryWrapper.like(FWordcard::getImei, imei).eq(FWordcard::getProject_key,project_key).orderByDesc(true,FWordcard::getId);

      /*  userLambdaQueryWrapper.eq(Alarm::getProject_key, project_key).like(Alarm::getAlarm_object, object).like(Alarm::getAlarm_type, alarm_type).like(Alarm::getSn, name)
                .or().eq(Alarm::getProject_key, project_key).like(Alarm::getAlarm_object, object).like(Alarm::getAlarm_type, alarm_type).like(Alarm::getName, name);
         */ userIPage = fWordcardMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageFWordcard pageFWordcard = new PageFWordcard(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageFWordcard;
    }
    public void update(FWordcardMapper fWordcardMapper, FWordcard wordcard){
        fWordcardMapper.updateById(wordcard);
    }

    public  void deletes(FWordcardMapper fWordcardMapper ,List<Integer> ids){
        fWordcardMapper.deleteBatchIds(ids);
    }
}