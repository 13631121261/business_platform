package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PagePerson;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.mappers.PermissionMapper;
import com.kunlun.firmwaresystem.mappers.PersonMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person_Sql {
    public boolean addPerson(PersonMapper PersonMapper, Person Person) {
        boolean status = check(PersonMapper, Person);
        if (status) {
            return false;
        } else {
           int id= PersonMapper.insert(Person);
            QueryWrapper<Person> queryWrapper = Wrappers.query();
            queryWrapper.eq("idcard",Person.getIdcard());
           Person person=PersonMapper.selectOne(queryWrapper);
          // System.out.println("申请的ID="+person.getId());
           Person.setId(person.getId());
            return true;
        }
    }

    public boolean update(PersonMapper PersonMapper, Person Person) {
        PersonMapper.updateById(Person);
        return true;
    }

    public Map<String, Person> getAllPerson(PersonMapper PersonMapper) {
        List<Person> Persons = PersonMapper.selectList(null);
        HashMap<String, Person> PersonHashMap = new HashMap<>();
        for (Person Person : Persons) {
            //System.out.println("初始化"+gateway.getSub_topic()+"==="+gateway.getPub_topic());
            PersonHashMap.put(Person.getIdcard(), Person);
        }
        return PersonHashMap;
    }


    public Map<String, Person> getAllPerson(PersonMapper PersonMapper, String project_key) {

        List<Person> Persons = PersonMapper.selectList(null);
        HashMap<String, Person> PersonHashMap = new HashMap<>();
        for (Person Person : Persons) {
            //System.out.println("初始化"+gateway.getSub_topic()+"==="+gateway.getPub_topic());
            PersonHashMap.put(Person.getName(), Person);
        }
        return PersonHashMap;
    }

    public List<Person> getAllPerson(PersonMapper PersonMapper, String idcard, String name,int p_id) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        if(p_id!=-1){
            userLambdaQueryWrapper.eq(Person::getDepartment_id, p_id);
        }
        userLambdaQueryWrapper.like(Person::getIdcard, idcard);
        userLambdaQueryWrapper.like(Person::getName, name);
        List<Person> Persons = PersonMapper.selectList(userLambdaQueryWrapper);
        return Persons;
    }
    public List<Person> getAllPerson(PersonMapper PersonMapper,int p_id,String userKey,String project_key) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        if(p_id!=-1){
            userLambdaQueryWrapper.eq(Person::getDepartment_id, p_id);
        }
        userLambdaQueryWrapper.eq(Person::getUser_key, userKey);
        userLambdaQueryWrapper.eq(Person::getProject_key, project_key);
        List<Person> Persons = PersonMapper.selectList(userLambdaQueryWrapper);
        return Persons;
    }
    public List<Person> getPersonByIdCard(PersonMapper PersonMapper,String idcard,String userKey,String project_key) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Person::getIdcard, idcard);
        userLambdaQueryWrapper.eq(Person::getUser_key, userKey);
        userLambdaQueryWrapper.eq(Person::getProject_key, project_key);
        List<Person> Persons = PersonMapper.selectList(userLambdaQueryWrapper);
        return Persons;
    }
    public    List<Person>  getPersonByFenceID(PersonMapper personMapper, int fence_id) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Person::getFence_id, fence_id);
        List<Person> personList = personMapper.selectList(userLambdaQueryWrapper);
        return personList;
    }
    public Person getPersonById(PersonMapper PersonMapper,String id) {
        Person person= PersonMapper.selectById(id);
        return person;
    }
    public List<Person> getAllPerson(PersonMapper PersonMapper,String userKey,String project_ley) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Person::getUser_key, userKey);
        userLambdaQueryWrapper.eq(Person::getProject_key, project_ley);
        List<Person> Persons = PersonMapper.selectList(userLambdaQueryWrapper);
        return Persons;
    }

    public void delete(PersonMapper PersonMapper, String mac) {
        QueryWrapper<Person> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", mac);
        PersonMapper.delete(queryWrapper);
    }
    public int  deletes(PersonMapper PersonMapper, List<Integer> id) {
        return PersonMapper.deleteBatchIds(id);

    }
    public void deletePerson(PersonMapper PersonMapper, String idcard) {
        QueryWrapper<Person> queryWrapper = Wrappers.query();
        queryWrapper.eq("idcard", idcard);
        PersonMapper.delete(queryWrapper);
    }

    public PagePerson selectPagePerson(PersonMapper PersonMapper, int page, int limt,String quickSearch, String userkey,String Project_key) {
        Page<Person> userPage = new Page<>(page, limt);
        IPage<Person> userIPage;
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();

        userLambdaQueryWrapper.eq(Person::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Person::getProject_key, Project_key);
        userLambdaQueryWrapper.like(Person::getIdcard,quickSearch).or().like(Person::getName,quickSearch).or().like(Person::getDepartment_name,quickSearch);
        userIPage = PersonMapper.selectPage(userPage, userLambdaQueryWrapper);
        PagePerson pagePerson = new PagePerson(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pagePerson;
    }

    public boolean check(PersonMapper PersonMapper, Person Person) {
        QueryWrapper<Person> queryWrapper = Wrappers.query();
        queryWrapper.eq("IdCard", Person.getIdcard());
        Person Person1 = PersonMapper.selectOne(queryWrapper);
        if (Person1 == null) {
            return false;
        } else {
            return true;
        }
    }
}