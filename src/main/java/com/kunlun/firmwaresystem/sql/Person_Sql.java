package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.device.PagePerson;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.DevicePMapper;
import com.kunlun.firmwaresystem.mappers.PermissionMapper;
import com.kunlun.firmwaresystem.mappers.PersonMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

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
          // myPrintln("申请的ID="+person.getId());
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
            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            PersonHashMap.put(Person.getIdcard(), Person);
        }
        return PersonHashMap;
    }

        public PagePerson getPersonPageByCompany(PersonMapper PersonMapper, int page, int limt, String userkey,String Project_key,int company_id) {
            Page<Person> userPage = new Page<>(page, limt);
            IPage<Person> userIPage;
            LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            userLambdaQueryWrapper.eq(Person::getCompany_id, company_id).eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).orderByDesc(true, Person::getId);
            userIPage = PersonMapper.selectPage(userPage, userLambdaQueryWrapper);
            PagePerson pagePerson = new PagePerson(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
            return pagePerson;
        }



        public Map<String, Person> getAllPerson(PersonMapper PersonMapper, String project_key) {

        List<Person> Persons = PersonMapper.selectList(null);
        HashMap<String, Person> PersonHashMap = new HashMap<>();
        for (Person Person : Persons) {
            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
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
    //根据设备组或者围栏组
    public PagePerson getPersonByF_G_id(String project_key, PersonMapper personMapper,String f_g_id, String page, String limt) {
        try {
            LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
            Page<Person> userPage = new Page<>(Long.parseLong(page), Long.parseLong(limt));
            IPage<Person> userIPage;

                userLambdaQueryWrapper.eq(Person::getProject_key, project_key).eq(Person::getFence_group_id,f_g_id);


            userIPage = personMapper.selectPage(userPage, userLambdaQueryWrapper);
            return new PagePerson(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
            return null;
        }

    }
    public List<Person> getAllPersonLike(PersonMapper PersonMapper, String idcard, String name,String project_key) {
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Person::getProject_key,project_key).like(Person::getIdcard, idcard)
        .or().eq(Person::getProject_key,project_key).like(Person::getName, name);
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

    public PagePerson selectPagePerson(PersonMapper PersonMapper, int page, int limt,String quickSearch, String userkey,String Project_key,String bind_status) {
        Page<Person> userPage = new Page<>(page, limt);
        IPage<Person> userIPage;
        LambdaQueryWrapper<Person> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        int bind=Integer.parseInt(bind_status);
        if(bind_status.equals("-1")){
            userLambdaQueryWrapper.eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getIdcard,quickSearch).or()
                    .eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getName,quickSearch).or()
                    .eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getDepartment_name,quickSearch);

        }else{
            userLambdaQueryWrapper.eq(Person::getIsbind, bind).eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getIdcard,quickSearch).or()
                    .eq(Person::getIsbind, bind).eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getName,quickSearch).or()
                    .eq(Person::getIsbind, bind).eq(Person::getUser_key, userkey).eq(Person::getProject_key, Project_key).like(Person::getDepartment_name,quickSearch);

        }
    /*  userLambdaQueryWrapper;
        userLambdaQueryWrapper.;
        userLambdaQueryWrapper.;*/
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