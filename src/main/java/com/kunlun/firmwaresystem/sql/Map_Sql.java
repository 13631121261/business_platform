package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageMap;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.util.RedisUtils;

import java.util.List;

import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_id_map;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_map;

public class Map_Sql {
    public boolean addMap(MapMapper mapMapper, Map map,RedisUtils redisUtils) {
        boolean status = check(mapMapper, map);
        if (status) {
            return false;
        } else {
            mapMapper.insert(map);
            Map map1=getMapByMapkey(mapMapper,map.getMap_key());
            redisUtils.setnoTimeOut(redis_key_map+map.getMap_key(),map1);
            redisUtils.setnoTimeOut(redis_id_map+map.getMap_id(),map1);
            return true;
        }

    }
    public List<Map> getAllMap(MapMapper mapMapper, RedisUtils redisUtils) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        List<Map> mapList = mapMapper.selectList(queryWrapper);
        for(Map map:mapList){
            redisUtils.setnoTimeOut(redis_key_map+map.getMap_key(),map);
            redisUtils.setnoTimeOut(redis_id_map+map.getMap_id(),map);
        }
        return mapList;
    }
    public List<Map> getAllMap(MapMapper mapMapper, String user_key,String project_key) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", user_key);
        queryWrapper.eq("project_key", project_key);
        List<Map> mapList = mapMapper.selectList(queryWrapper);
        return mapList;
    }
    public List<Map> getMapByKey(MapMapper mapMapper, String user_key,String project_key,String map_key) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", user_key);
        queryWrapper.eq("project_key", project_key);
        queryWrapper.eq("map_key", map_key);
        List<Map> mapList = mapMapper.selectList(queryWrapper);
        return mapList;
    }
    public Map getMapById(MapMapper mapMapper, int id) {

        Map map = mapMapper.selectById(id );
        return map;
    }
    public Map getMapByMapkey(MapMapper mapMapper, String key) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        queryWrapper.eq("map_key", key);
        Map map = mapMapper.selectOne(queryWrapper );
        return map;
    }
    public int update(MapMapper mapMapper, Map map) {
        int status= mapMapper.updateById(map);

        return status;
    }
    public void delete(MapMapper mapMapper, String key) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        queryWrapper.eq("map_key", key);
        mapMapper.delete(queryWrapper);
    }
    public int deletes(MapMapper mapMapper,  List<Integer> id) {

        return  mapMapper.deleteBatchIds(id);
    }
    public PageMap selectPageMap(MapMapper mapMapper, int page, int limt, String userkey,String project_key, String name) {
        LambdaQueryWrapper<Map> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Map> userPage = new Page<>(page, limt);
        IPage<Map> userIPage;
        userLambdaQueryWrapper.eq(Map::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Map::getProject_key, project_key);
        userLambdaQueryWrapper.like(Map::getName, name);
        userIPage = mapMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::println);
        PageMap pageMap = new PageMap(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageMap;
    }

    public boolean check(MapMapper mapMapper, Map map) {
        QueryWrapper<Map> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", map.getUser_key());
        queryWrapper.eq("name", map.getName());
        queryWrapper.eq("project_key", map.getProject_key());
        Map map1 = mapMapper.selectOne(queryWrapper);
        if (map1 == null) {
            return false;
        } else {
            return true;
        }
    }
}