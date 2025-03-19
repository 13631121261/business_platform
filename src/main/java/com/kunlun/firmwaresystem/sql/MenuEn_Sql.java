package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Menu;
import com.kunlun.firmwaresystem.entity.Menu_en;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.MenuEn_Mapper;
import com.kunlun.firmwaresystem.mappers.MenuMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public  class MenuEn_Sql {
    public boolean addArea(MenuEn_Mapper menuMapper, Menu_en menu) {
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


    public void delete(MenuEn_Mapper menuMapper, int id) {
        QueryWrapper<Menu_en> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        menuMapper.delete(queryWrapper);
    }



    public Menu_en getMenu(MenuEn_Mapper menuMapper,int id){

        Menu_en menu= menuMapper.selectById(id);
        return menu;

    }
    public List<Menu_en> getMenu(MenuEn_Mapper menuMapper,List<Integer> id){
        List<Menu_en> menus = menuMapper.selectBatchIds(id);
        List<Menu_en> departmentList=new ArrayList<>();
        Map<Integer,Menu_en> menuHashMap=new HashMap<>();
        for(int i=0;i<menus.size();i++){
            Menu_en menu=menus.get(i);
            Menu_en menu1=menuHashMap.get(menu.getPid());
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
    public List<Menu_en> getAllMenu(MenuEn_Mapper menuMapper) {
        try {
            QueryWrapper<Menu_en> queryWrapper = Wrappers.query();
            queryWrapper.eq("status", 1);
            List<Menu_en> menus = menuMapper.selectList(queryWrapper);
            List<Menu_en> departmentList = new ArrayList<>();
            Map<Integer, Menu_en> menuHashMap = new HashMap<>();
            for (int i = 0; i < menus.size(); i++) {
                Menu_en menu = menus.get(i);
                Menu_en menu1 = menuHashMap.get(menu.getPid());
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
    public List<Menu_en> getAllMenu1(MenuEn_Mapper menuMapper) {
        try{
        QueryWrapper<Menu_en> queryWrapper = Wrappers.query();
        queryWrapper.eq("shows",0);
        List<Menu_en> menus= menuMapper.selectList(queryWrapper);
            myPrintln("长度="+menus.size());
        List<Menu_en> departmentList=new ArrayList<>();
        Map<Integer,Menu_en> menuHashMap=new HashMap<>();
        for(int i=0;i<menus.size();i++){
            Menu_en menu=menus.get(i);
            Menu_en menu1=menuHashMap.get(menu.getPid());
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