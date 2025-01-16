package com.kunlun.firmwaresystem.sql;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageStationType;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.StationType;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.StationTypeMapper;
import java.util.List;
public class StationType_Sql {
    public boolean addStationType(StationTypeMapper stationTypeMapper, StationType stationType) {
        boolean status = check(stationTypeMapper, stationType);
        if (status) {
            return false;
        } else {
            try {
                stationTypeMapper.insert(stationType);
            }
            catch (Exception e){
                System.out.println("有点异常="+e);
            }
            return true;
        }

    }

    public void delete(StationTypeMapper StationTypeMapper, int id) {
        QueryWrapper<StationType> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        StationTypeMapper.delete(queryWrapper);
    }
    public int deletes(StationTypeMapper StationTypeMapper, List<Integer> id) {

      return StationTypeMapper.deleteBatchIds(id);
    }


    public PageStationType selectPage(StationTypeMapper StationTypeMapper, int page, int limt, String project_key, String name) {

        LambdaQueryWrapper<StationType> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<StationType> userPage = new Page<>(page, limt);
        IPage<StationType> userIPage;


            userLambdaQueryWrapper.like(StationType::getProject_key, project_key).like(StationType::getName, name).orderByDesc(true,StationType::getId);
          userIPage = StationTypeMapper.selectPage(userPage, userLambdaQueryWrapper);
        return new PageStationType(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
    }

    public    List<StationType> getAll(StationTypeMapper StationTypeMapper,String project_key) {
        QueryWrapper<StationType> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        List<StationType> StationTypes= StationTypeMapper.selectList(queryWrapper);
        return StationTypes;
    }

    public boolean check(StationTypeMapper stationTypeMapper, StationType stationType) {
        QueryWrapper<StationType> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", stationType.getName());
        queryWrapper.eq("project_key", stationType.getProject_key());
        StationType stationType1 = stationTypeMapper.selectOne(queryWrapper);
        if (stationType1 == null) {
            return false;
        } else {
            return true;
        }
    }
}