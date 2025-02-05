package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageGroup;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.GroupMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group_Sql {
    public boolean add(GroupMapper groupMapper, Group group) {
        boolean status = check(groupMapper, group);
        if (status) {
            return false;
        } else {
            groupMapper.insert(group);
            return true;
        }

    }


    public void delete(GroupMapper groupMapper, int id) {
        QueryWrapper<Group> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        groupMapper.delete(queryWrapper);
    }
    public int deletes(GroupMapper groupMapper, List<Integer> id) {

      return groupMapper.deleteBatchIds(id);
    }
    public int update(GroupMapper groupMapper, Group group) {

       return groupMapper.updateById(group);
    }
    public PageGroup selectPage(GroupMapper groupMapper, int page, int limt,  String project_key, String name) {
        LambdaQueryWrapper<Group> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Group> userPage = new Page<>(page, limt);
        IPage<Group> userIPage;

        userLambdaQueryWrapper.like(Group::getGroup_name, name);
        userLambdaQueryWrapper.eq(Group::getProject_key, project_key);
        userIPage = groupMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::println);
        PageGroup pageGroup = new PageGroup(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageGroup;
    }


    public    List<Group> getAll(GroupMapper groupMapper,String project_key) {
        QueryWrapper<Group> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        List<Group> groups= groupMapper.selectList(queryWrapper);
        return groups;
    }

    public boolean check(GroupMapper groupMapper, Group group) {
        QueryWrapper<Group> queryWrapper = Wrappers.query();
        queryWrapper.eq("group_name", group.getGroup_name());
        queryWrapper.eq("project_key", group.getProject_key());
        Group group1 = groupMapper.selectOne(queryWrapper);
        if (group1 == null) {
            return false;
        } else {
            return true;
        }
    }
}