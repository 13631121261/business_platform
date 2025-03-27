package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Patrol;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.PatrolMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patrol_Sql {
    public boolean add(PatrolMapper patrolMapper, Patrol patrol) {
        boolean status = check(patrolMapper, patrol);
        if (status) {
            return false;
        } else {
            patrolMapper.insert(patrol);
            return true;
        }

    }
    public Patrol getPatrolById(PatrolMapper patrolMapper, int id) {

        Patrol patrol = patrolMapper.selectById(id );
        return patrol;
    }

    public void delete(PatrolMapper patrolMapper, int id) {
        QueryWrapper<Patrol> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        patrolMapper.delete(queryWrapper);
    }
    public int deletes(PatrolMapper patrolMapper, List<Integer> id) {

      return patrolMapper.deleteBatchIds(id);
    }
    public int update(PatrolMapper patrolMapper, Patrol patrol) {

       return patrolMapper.updateById(patrol);
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
    public    List<Patrol> getAll(PatrolMapper patrolMapper,String user_key,String project_key) {
        QueryWrapper<Patrol> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("userkey",user_key);
        List<Patrol> areas= patrolMapper.selectList(queryWrapper);
        return areas;
    }

    public boolean check(PatrolMapper patrolMapper, Patrol patrol) {
        QueryWrapper<Patrol> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", patrol.getName());
        queryWrapper.eq("project_key", patrol.getProject_key());
        Patrol patrol1 = patrolMapper.selectOne(queryWrapper);
        if (patrol1 == null) {
            return false;
        } else {
            return true;
        }
    }
}