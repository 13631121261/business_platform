package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageFence;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Fence;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.FenceMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Fence_Sql {
    public boolean addFence(FenceMapper fenceMapper, Fence fence) {
        boolean status = check(fenceMapper, fence);
        if (status) {
            return false;
        } else {
            fenceMapper.insert(fence);
            QueryWrapper<Fence> queryWrapper = Wrappers.query();
            queryWrapper.eq("name", fence.getName());
            queryWrapper.eq("project_key", fence.getProject_key());
            Fence fence1 = fenceMapper.selectOne(queryWrapper);
            //myPrintln("申请的ID="+ devicep1.getId());
            fence.setId(fence1.getId());
            return true;
        }
    }
    public void delete(FenceMapper fenceMapper, int id) {
        QueryWrapper<Fence> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        fenceMapper.delete(queryWrapper);
    }
    public int deletes(FenceMapper fenceMapper, List<Integer> id) {
      return fenceMapper.deleteBatchIds(id);
    }
    public int update(FenceMapper fenceMapper, Fence fence) {

       return fenceMapper.updateById(fence);
    }
    public int update(FenceMapper fenceMapper, int id,int open_status) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.set("open_status", open_status);
        updateWrapper.eq("id", id);
        return fenceMapper.update(null,updateWrapper);
    }
    public PageFence selectPageFence(FenceMapper areaMapper, int page, int limt,String userkey,String project_key, String name) {
        LambdaQueryWrapper<Fence> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Fence> userPage = new Page<>(page, limt);
        IPage<Fence> userIPage;
        userLambdaQueryWrapper.like(Fence::getName, name);
        userLambdaQueryWrapper.eq(Fence::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Fence::getProject_key, project_key);
        userIPage = areaMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageFence pageFence = new PageFence(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageFence;
    }
    public PageFence selectPageFence_used(FenceMapper areaMapper, int page, int limt,String userkey,String project_key, String name,String quick_map) {
        LambdaQueryWrapper<Fence> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Fence> userPage = new Page<>(page, limt);
        IPage<Fence> userIPage;
        userLambdaQueryWrapper.like(Fence::getName, name);
        userLambdaQueryWrapper.eq(Fence::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Fence::getOpen_status, 1);
        userLambdaQueryWrapper.eq(Fence::getProject_key, project_key);
        if(quick_map!=null&& !quick_map.isEmpty()){
            userLambdaQueryWrapper.eq(Fence::getMap_key, quick_map);
        }
        userIPage = areaMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageFence pageFence = new PageFence(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageFence;
    }
    public   boolean isHaveArea(FenceMapper fenceMapper,int area_id) {
        try {
            myPrintln("id==" + area_id);
            QueryWrapper<Fence> queryWrapper = Wrappers.query();
            queryWrapper.eq("area_id", area_id);
            List<Fence> fences = fenceMapper.selectList(queryWrapper);
            myPrintln("围栏=" + fences);
            if (fences != null && fences.size() > 0) {
                return true;
            }
            return false;
        }catch (Exception e){
            myPrintln("异常="+e.toString());
            return true;
        }
    }
    public   Map<Integer,Fence> getAllFence(FenceMapper fenceMapper) {
        QueryWrapper<Fence> queryWrapper = Wrappers.query();

        List<Fence> fences= fenceMapper.selectList(queryWrapper);
        Map<Integer,Fence> fenceHashMap=new HashMap<>();
        for(Fence area:fences){
            fenceHashMap.put(area.getId(),area);
        }
        return fenceHashMap;
    }
    public    List<Fence> getAllFence(FenceMapper fenceMapper,String user_key,String project_key) {
        QueryWrapper<Fence> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("user_key",user_key);
        List<Fence> fences= fenceMapper.selectList(queryWrapper);
        return fences;
    }

    public boolean check(FenceMapper areaMapper, Fence fence) {
        QueryWrapper<Fence> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", fence.getName());
        queryWrapper.eq("project_key", fence.getProject_key());
        Fence area1 = areaMapper.selectOne(queryWrapper);
        if (area1 == null) {
            return false;
        } else {
            return true;
        }
    }
}