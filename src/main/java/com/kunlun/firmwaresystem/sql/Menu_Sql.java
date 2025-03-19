package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Department;
import com.kunlun.firmwaresystem.entity.Menu;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.MenuMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public  class Menu_Sql {
    public boolean addArea(MenuMapper menuMapper, Menu menu) {
        /*    boolean status = check(menuMapper, menu);
            if (status) {
                return false;
            } else {*/
        menuMapper.insert(menu);
                /*  QueryWrapper<Area> queryWrapper = Wrappers.query();
              queryWrapper.eq("name",area.getName());
                Area area1=areaMapper.selectOne(queryWrapper);
                myPrintln("申请的ID="+area1.getId());
                area.setId(area1.getId())*/;
        return true;
        //}

    }


    public void delete(MenuMapper menuMapper, int id) {
        QueryWrapper<Menu> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        menuMapper.delete(queryWrapper);
    }
    public void update(AreaMapper areaMapper, Area area) {

        areaMapper.updateById(area);
    }
    public PageArea selectPageArea(AreaMapper areaMapper, int page, int limt, String name, String userkey) {
        LambdaQueryWrapper<Area> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Area> userPage = new Page<>(page, limt);
        IPage<Area> userIPage;
        userLambdaQueryWrapper.like(Area::getName, name);
        userLambdaQueryWrapper.eq(Area::getUserkey, userkey);
        userIPage = areaMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageArea pageArea = new PageArea(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageArea;
    }


    public Menu getMenu(MenuMapper menuMapper,int id){

        Menu menu= menuMapper.selectById(id);
        return menu;

    }
    public List<Menu> getMenu(MenuMapper menuMapper,List<Integer> id){
        List<Menu> menus = menuMapper.selectBatchIds(id);
        List<Menu> departmentList=new ArrayList<>();
        Map<Integer,Menu> menuHashMap=new HashMap<>();
        for(int i=0;i<menus.size();i++){
            Menu menu=menus.get(i);
            Menu menu1=menuHashMap.get(menu.getPid());
            if(menu1==null){
                menuHashMap.put(menu.getId(),menu);
                departmentList.add(menu);
            }else{
                menuHashMap.put(menu.getId(),menu);
                menu1.addMenu(menu);
            }
        }
        return departmentList;
    }
    public List<Menu> getAllMenu(MenuMapper menuMapper) {
        try {
            QueryWrapper<Menu> queryWrapper = Wrappers.query();
            queryWrapper.eq("status", 1);
            List<Menu> menus = menuMapper.selectList(queryWrapper);
            List<Menu> departmentList = new ArrayList<>();
            Map<Integer, Menu> menuHashMap = new HashMap<>();
            for (int i = 0; i < menus.size(); i++) {
                Menu menu = menus.get(i);
                Menu menu1 = menuHashMap.get(menu.getPid());
                if (menu1 == null) {
                    menuHashMap.put(menu.getId(), menu);
                    departmentList.add(menu);
                } else {
                    menuHashMap.put(menu.getId(), menu);
                    menu1.addMenu(menu);
                }
            }
         /*   Map<Integer,Area> areaHashMap=new HashMap<>();
            for(Menu menu:menus){
                areaHashMap.put(menu.getId(),menu);
            }*/
            return departmentList;
        }catch (Exception e){
            myPrintln("异常="+e);
            return null;
        }

    }
    public List<Menu> getAllMenu1(MenuMapper menuMapper) {
        try{
        QueryWrapper<Menu> queryWrapper = Wrappers.query();
        queryWrapper.eq("shows",0);
        List<Menu> menus= menuMapper.selectList(queryWrapper);

        List<Menu> departmentList=new ArrayList<>();
        Map<Integer,Menu> menuHashMap=new HashMap<>();
        for(int i=0;i<menus.size();i++){
            Menu menu=menus.get(i);
            Menu menu1=menuHashMap.get(menu.getPid());
            if(menu1==null){
                menuHashMap.put(menu.getId(),menu);
                departmentList.add(menu);
            }else{
                menuHashMap.put(menu.getId(),menu);
                menu1.addMenu(menu);
            }
        }
         /*   Map<Integer,Area> areaHashMap=new HashMap<>();
            for(Menu menu:menus){
                areaHashMap.put(menu.getId(),menu);
            }*/
        return departmentList;}
        catch (Exception e){
            myPrintln("异常="+e);
            return null;
        }

    }
      /*  public boolean check(MenuMapper menuMapper, Menu menu) {
            QueryWrapper<Area> queryWrapper = Wrappers.query();
            queryWrapper.eq("name", area.get());
            Area area1 = areaMapper.selectOne(queryWrapper);
            if (area1 == null) {
                return false;
            } else {
                return true;
            }
        }*/
}