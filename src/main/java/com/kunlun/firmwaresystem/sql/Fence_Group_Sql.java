package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageFenceGroup;
import com.kunlun.firmwaresystem.device.PageGroup;
import com.kunlun.firmwaresystem.entity.Fence_group;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.mappers.FenceGroupMapper;
import com.kunlun.firmwaresystem.mappers.GroupMapper;

import java.util.HashMap;
import java.util.List;

public class Fence_Group_Sql {
    public boolean add(FenceGroupMapper groupMapper, Fence_group group) {
        boolean status = check(groupMapper, group);
        if (status) {
            return false;
        } else {
            groupMapper.insert(group);
            return true;
        }

    }


    public void delete(FenceGroupMapper groupMapper, int id) {
        QueryWrapper<Fence_group> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        groupMapper.delete(queryWrapper);
    }
    public int deletes(FenceGroupMapper groupMapper, List<Integer> id) {

      return groupMapper.deleteBatchIds(id);
    }
    public int update(FenceGroupMapper groupMapper, Fence_group group) {

       return groupMapper.updateById(group);
    }
    public PageFenceGroup selectPage(FenceGroupMapper groupMapper, int page, int limt, String project_key, String name) {
        LambdaQueryWrapper<Fence_group> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Fence_group> userPage = new Page<>(page, limt);
        IPage<Fence_group> userIPage;

        userLambdaQueryWrapper.like(Fence_group::getName, name);
        userLambdaQueryWrapper.eq(Fence_group::getProject_key, project_key);
        userIPage = groupMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageFenceGroup pageGroup = new PageFenceGroup(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageGroup;
    }


    public    List<Fence_group> getAll(FenceGroupMapper groupMapper,String project_key) {
        QueryWrapper<Fence_group> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        List<Fence_group> groups= groupMapper.selectList(queryWrapper);
        return groups;
    }
    public HashMap<Integer, Fence_group> getAll(FenceGroupMapper groupMapper) {
        QueryWrapper<Fence_group> queryWrapper = Wrappers.query();
        List<Fence_group> groups= groupMapper.selectList(queryWrapper);
        HashMap<Integer, Fence_group> map= new HashMap<>();
        for (Fence_group group : groups) {
            map.put(group.getId(), group);
        }
        return map;
    }

    public boolean check(FenceGroupMapper groupMapper, Fence_group group) {
        QueryWrapper<Fence_group> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", group.getName());
        queryWrapper.eq("project_key", group.getProject_key());
        Fence_group group1 = groupMapper.selectOne(queryWrapper);
        return group1 != null;
    }
}