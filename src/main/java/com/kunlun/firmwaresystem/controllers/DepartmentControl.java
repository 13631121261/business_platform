package com.kunlun.firmwaresystem.controllers;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.NewSystemApplication;
import com.kunlun.firmwaresystem.device.PageDepartment;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Department;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.DepartmentMapper;
import com.kunlun.firmwaresystem.sql.Department_Sql;
import com.kunlun.firmwaresystem.sql.Person_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class DepartmentControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private DepartmentMapper departmentMapper;
    @RequestMapping(value = "userApi/Department/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject selectAllDepartment(HttpServletRequest request){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Department_Sql departmentSql=new Department_Sql();
        String quickSearch=request.getParameter("quickSearch");
        List<Department> departments= departmentSql.getAllDepartment(departmentMapper,customer.getUserkey(),customer.getProject_key(),quickSearch);
        List<Department> departments1=new ArrayList<>();
        for (Department department:departments){
            int p_id= department.getP_id();
            for (Department department1:departments){
                if(department1.getId()==p_id){
                    department1.setRoot(0);
                }
            }
        } for (Department department:departments){
                if(department.getRoot()==1){
                    departments1.add(department);
                }
        }

        return JsonConfig.getJsonObj(CODE_OK, departments1,lang);

    }
    @RequestMapping(value = "userApi/getAllDepartment", method = RequestMethod.GET,produces = "application/json")
    public JSONObject getDepartment(HttpServletRequest request) {
        String response = null;
        Customer customer = getCustomer(request);
            myPrintln("对象为" + customer.getUsername());
            Department_Sql departmentSql = new Department_Sql();
            List<Department> departments = departmentSql.getAllDepartment(departmentMapper,customer.getUserkey(),customer.getProject_key());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);

            List<Department> departmentList=new ArrayList<>();
            java.util.Map<Integer,Department> departmentMap=new HashMap<>();
            for(int i=0;i<departments.size();i++){
                Department department=departments.get(i);
                Department department1=departmentMap.get(department.getP_id());
                if(department1==null){
                    departmentMap.put(department.getId(),department);
                    departmentList.add(department);
                }else{
                    departmentMap.put(department.getId(),department);
                    department1.addDepartment(department);
                }
            }
            jsonObject.put("data", departmentList);
            response=jsonObject.toString();
            response=response.replaceAll("name","label");
            response=response.replaceAll("departmentlist","children");
        return JSONObject.parseObject(response);
    }

    @RequestMapping(value = "/userApi/Department/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject selectDepartment(HttpServletRequest request){
        String quickSearch=request.getParameter("quickSearch");
        String pages=request.getParameter("page");
        String limits=request.getParameter("limit");
        int page=1;
        int limit=10;
        if (!StringUtils.isBlank(pages)) {
            page=Integer.parseInt(pages);
        }
        if (!StringUtils.isBlank(limits)) {
            limit=Integer.parseInt(limits);
        }
        if (StringUtils.isBlank(quickSearch)) {
            quickSearch="";
        }
        Customer customer=getCustomer(request);
        Department_Sql departmentSql=new Department_Sql();
        PageDepartment pageDepartment=departmentSql.selectPage(departmentMapper,Integer.valueOf(page),Integer.valueOf(limit),customer.getUserkey(),customer.getProject_key(),quickSearch);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDepartment.getTotal());
        jsonObject.put("data", pageDepartment.getDeviceList());
        return jsonObject;
    }
    @RequestMapping(value = "userApi/Department/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addDepartment(HttpServletRequest request, @RequestBody JSONObject json) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Department_Sql departmentSql=new Department_Sql();
        Department department=new Gson().fromJson(json.toString(),new TypeToken<Department>(){}.getType());
        department.setUserkey(customer.getUserkey());
        department.setCustomer_key(customer.getCustomerkey());
        department.setProject_key(customer.getProject_key());
        department.setCreatetime(System.currentTimeMillis()/1000);
        boolean status=departmentSql.addDepartment(departmentMapper,department);
        if(status){

            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }
        else{
            return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Department/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject editDepartment(HttpServletRequest request, @RequestBody JSONObject json) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Department department=new Gson().fromJson(json.toString(),new TypeToken<Department>(){}.getType());
        department.setUserkey(customer.getUserkey());
        department.setCustomer_key(customer.getCustomerkey());
        department.setProject_key(customer.getProject_key());
       // department.setCreatetime(System.currentTimeMillis()/1000);
        int status= departmentMapper.updateById(department);
        if(status!=-1){
            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }
        else{
            return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Department/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject editDepartment(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "id") String id) {
        Department department=departmentMapper.selectById(id);
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(department!=null){
            return JsonConfig.getJsonObj(CODE_OK,department,lang);
        }else{
            return JsonConfig.getJsonObj(CODE_RESPONSE_NULL,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Department/del", method = RequestMethod.GET, produces = "application/json")
    public JSONObject deleteDepartment(HttpServletRequest request, @RequestParam("id") @ParamsNotNull String id) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Department_Sql departmentSql=new Department_Sql();
        for(String key: NewSystemApplication.personMap.keySet()){
            Person person=NewSystemApplication.personMap.get(key);
            if(person!=null&&person.getDepartment_id()==Integer.parseInt(id)){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }
        }
        List<Department> departments= departmentSql.getAllDepartment(departmentMapper,customer.getUserkey(),customer.getProject_key(),"");
        Department department2=departmentMapper.selectById(Integer.parseInt(id));
        for (Department department:departments){
            if(department2.getId()==department.getId()){
                int p_id= department.getId();
                for (Department department1:departments){
                    if(department1.getP_id()==p_id){
                        department2.setRoot(0);
                        break;
                    }
                }
            }

        }

        if(department2.getRoot()==0){
            return JsonConfig.getJsonObj(CODE_12,null,lang);
        }else{
           departmentSql.delete(departmentMapper, Integer.parseInt(id));
            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }


    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}
