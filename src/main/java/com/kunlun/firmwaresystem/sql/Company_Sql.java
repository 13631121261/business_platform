package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.kunlun.firmwaresystem.device.PageCompany;
import com.kunlun.firmwaresystem.entity.Company;
import com.kunlun.firmwaresystem.mappers.CompanyMapper;

import java.util.List;

public class Company_Sql {
    public boolean add(CompanyMapper CompanyMapper, Company Company) {
        boolean status = check(CompanyMapper, Company);
        if (status) {
            return false;
        } else {
            CompanyMapper.insert(Company);
            return true;
        }

    }


    public void delete(CompanyMapper CompanyMapper, int id) {
        QueryWrapper<Company> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        CompanyMapper.delete(queryWrapper);
    }
    public int deletes(CompanyMapper CompanyMapper, List<Integer> id) {

      return CompanyMapper.deleteBatchIds(id);
    }
    public int update(CompanyMapper CompanyMapper, Company Company) {

       return CompanyMapper.updateById(Company);
    }
    public PageCompany selectPage(CompanyMapper CompanyMapper, int page, int limt, String project_key, String name) {
        LambdaQueryWrapper<Company> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Company> userPage = new Page<>(page, limt);
        IPage<Company> userIPage;

        userLambdaQueryWrapper.like(Company::getName, name);
        userLambdaQueryWrapper.eq(Company::getProject_key, project_key);
        userIPage = CompanyMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageCompany pageCompany = new PageCompany(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageCompany;
    }
    public    List<Company> getFromFenceCompany(CompanyMapper CompanyMapper,String project_key,int f_g_id) {
        QueryWrapper<Company> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("f_g_id",f_g_id);
        List<Company> Companys= CompanyMapper.selectList(queryWrapper);
        return Companys;
    }
    public    List<Company> getAll(CompanyMapper CompanyMapper,String project_key) {
        QueryWrapper<Company> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        List<Company> Companys= CompanyMapper.selectList(queryWrapper);
        return Companys;
    }
    public    List<Company> getAll(CompanyMapper CompanyMapper) {
        QueryWrapper<Company> queryWrapper = Wrappers.query();
        List<Company> Companys= CompanyMapper.selectList(queryWrapper);
        return Companys;
    }

    public boolean check(CompanyMapper CompanyMapper, Company Company) {
        QueryWrapper<Company> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", Company.getName());
        queryWrapper.eq("project_key", Company.getProject_key());
        Company Company1 = CompanyMapper.selectOne(queryWrapper);
        if (Company1 == null) {
            return false;
        } else {
            return true;
        }
    }
}