package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kunlun.firmwaresystem.entity.Patrol;
import com.kunlun.firmwaresystem.entity.Patrol_list;
import com.kunlun.firmwaresystem.mappers.PatrolListMapper;
import com.kunlun.firmwaresystem.mappers.PatrolListMapper;

import java.util.List;

public class Patrol_List_Sql {
    public boolean add(PatrolListMapper PatrolListMapper, Patrol_list patrol) {
        boolean status = check(PatrolListMapper, patrol);
        if (status) {
            return false;
        } else {
            PatrolListMapper.insert(patrol);
            return true;
        }

    }
    public Patrol_list getPatrolById(PatrolListMapper PatrolListMapper, int id) {

        Patrol_list patrol = PatrolListMapper.selectById(id );
        return patrol;
    }

    public void delete(PatrolListMapper PatrolListMapper, int id) {
        QueryWrapper<Patrol_list> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        PatrolListMapper.delete(queryWrapper);
    }
    public int deletes(PatrolListMapper PatrolListMapper, List<Integer> id) {

      return PatrolListMapper.deleteBatchIds(id);
    }
    public int update(PatrolListMapper PatrolListMapper, Patrol_list patrol) {

       return PatrolListMapper.updateById(patrol);
    }



    public    List<Patrol_list> getAll(PatrolListMapper PatrolListMapper,String project_key,String search) {
        QueryWrapper<Patrol_list> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);

        if(search!=null&& !search.isEmpty()){
            queryWrapper.like("name",search);
        }
        List<Patrol_list> areas= PatrolListMapper.selectList(queryWrapper);
        return areas;
    }

    public boolean check(PatrolListMapper PatrolListMapper, Patrol_list patrol) {
        QueryWrapper<Patrol_list> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", patrol.getName());
        queryWrapper.eq("project_key", patrol.getProject_key());
        Patrol_list patrol1 = PatrolListMapper.selectOne(queryWrapper);
        if (patrol1 == null) {
            return false;
        } else {
            return true;
        }
    }
}