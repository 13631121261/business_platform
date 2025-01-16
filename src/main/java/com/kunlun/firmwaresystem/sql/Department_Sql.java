package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageDepartment;
import com.kunlun.firmwaresystem.entity.Department;
import com.kunlun.firmwaresystem.mappers.DepartmentMapper;

import java.util.List;

public class Department_Sql {
    public boolean addDepartment(DepartmentMapper departmentMapper, Department department) {
       boolean status = check(departmentMapper, department);
        if (status) {
            return false;
        } else {
            departmentMapper.insert(department);
            return true;
        }
    }

    public void delete(DepartmentMapper departmentMapper, int id) {
        QueryWrapper<Department> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        departmentMapper.delete(queryWrapper);
    }
    public int deletes(DepartmentMapper departmentMapper, List<Integer> ids) {
        return departmentMapper.deleteBatchIds(ids);
    }

    public PageDepartment selectPage(DepartmentMapper departmentMapper, int page, int limt, String userkey,String projectkey,String like) {
        LambdaQueryWrapper<Department> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Department> userPage = new Page<>(page, limt);
        IPage<Department> userIPage;
        userLambdaQueryWrapper.eq(Department::getUserkey, userkey);
        userLambdaQueryWrapper.eq(Department::getProject_key, projectkey);
        userLambdaQueryWrapper.like(Department::getName,like);
        userIPage = departmentMapper.selectPage(userPage, userLambdaQueryWrapper);
        PageDepartment pageArea = new PageDepartment(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageArea;
    }

    public List<Department> getAllDepartment(DepartmentMapper departmentMapper,String userkey,String projectkey,String quickSearch){
        LambdaQueryWrapper<Department> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Department::getUserkey,userkey);
        userLambdaQueryWrapper.eq(Department::getProject_key,projectkey);
        userLambdaQueryWrapper.like(Department::getName,quickSearch);
        List<Department> list=  departmentMapper.selectList(userLambdaQueryWrapper);
        return list;
    }
    public List<Department> getAllDepartment(DepartmentMapper departmentMapper,String userkey){
        LambdaQueryWrapper<Department> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Department::getUserkey,userkey);
        List<Department> list=  departmentMapper.selectList(userLambdaQueryWrapper);
        return list;
    }
    public boolean check(DepartmentMapper departmentMapper, Department department) {
        QueryWrapper<Department> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", department.getName());
        queryWrapper.eq("name", department.getProject_key());
        Department department1 = departmentMapper.selectOne(queryWrapper);
        if (department1 == null) {
            return false;
        } else {
            return true;
        }
    }
}