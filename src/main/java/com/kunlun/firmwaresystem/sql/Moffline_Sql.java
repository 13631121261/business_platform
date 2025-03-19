package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageMoffline;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Moffline;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.MofflineMapper;

public class Moffline_Sql {
    public boolean addMoffline(MofflineMapper mofflineMapper, Moffline moffline) {


        mofflineMapper.insert(moffline);


            return true;


    }


    public void delete(MofflineMapper mofflineMapper, int id) {
        QueryWrapper<Moffline> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        mofflineMapper.delete(queryWrapper);
    }
    public void update(AreaMapper areaMapper, Area area) {

        areaMapper.updateById(area);
    }
    public PageMoffline selectPage(MofflineMapper mofflineMapper, int page, int limt, String mac,String userkey) {
        LambdaQueryWrapper<Moffline> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Moffline> userPage = new Page<>(page, limt);
        IPage<Moffline> userIPage;
        userLambdaQueryWrapper.like(Moffline::getAddress, mac);
        userLambdaQueryWrapper.eq(Moffline::getUserkey, userkey);
        userLambdaQueryWrapper.orderByDesc(Moffline::getId);
        userIPage = mofflineMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageMoffline pageMoffline = new PageMoffline(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageMoffline;
    }

    public boolean check(AreaMapper areaMapper, Area area) {
        QueryWrapper<Area> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", area.getName());
        Area area1 = areaMapper.selectOne(queryWrapper);
        if (area1 == null) {
            return false;
        } else {
            return true;
        }
    }
}