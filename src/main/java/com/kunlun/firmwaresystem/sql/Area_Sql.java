package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.mappers.AreaMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Area_Sql {
    public boolean addArea(AreaMapper areaMapper, Area area) {
        boolean status = check(areaMapper, area);
        if (status) {
            return false;
        } else {
            areaMapper.insert(area);
            QueryWrapper<Area> queryWrapper = Wrappers.query();
            queryWrapper.eq("name", area.getName());
            queryWrapper.eq("project_key", area.getProject_key());
            Area area1 = areaMapper.selectOne(queryWrapper);
            //myPrintln("申请的ID="+ devicep1.getId());
            area.setId(area1.getId());
            return true;
        }

    }
    public Area getAreaById(AreaMapper areaMapper, int id) {

        Area area = areaMapper.selectById(id );
        return area;
    }

    public void delete(AreaMapper areaMapper, int id) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        areaMapper.delete(queryWrapper);
    }
    public int deletes(AreaMapper areaMapper, List<Integer> id) {

      return areaMapper.deleteBatchIds(id);
    }
    public int update(AreaMapper areaMapper, Area area) {

       return areaMapper.updateById(area);
    }
    public PageArea selectPageArea(AreaMapper areaMapper, int page, int limt,String userkey,String project_key, String name) {
        LambdaQueryWrapper<Area> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Area> userPage = new Page<>(page, limt);
        IPage<Area> userIPage;

        userLambdaQueryWrapper.like(Area::getName, name);
        userLambdaQueryWrapper.eq(Area::getUserkey, userkey);
        userLambdaQueryWrapper.eq(Area::getProject_key, project_key);
        userIPage = areaMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageArea pageArea = new PageArea(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
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
    public    List<Area> getAllArea(AreaMapper areaMapper,String user_key,String project_key) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("userkey",user_key);
        List<Area> areas= areaMapper.selectList(queryWrapper);
        return areas;
    }
    public    List<Area> getAllArea(AreaMapper areaMapper,String user_key,String project_key,String map_key) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("userkey",user_key);
        queryWrapper.eq("map_key",map_key);
        List<Area> areas= areaMapper.selectList(queryWrapper);
        return areas;
    }
    public boolean check(AreaMapper areaMapper, Area area) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", area.getName());
        queryWrapper.eq("project_key", area.getProject_key());
        Area area1 = areaMapper.selectOne(queryWrapper);
        if (area1 == null) {
            return false;
        } else {
            return true;
        }
    }
}