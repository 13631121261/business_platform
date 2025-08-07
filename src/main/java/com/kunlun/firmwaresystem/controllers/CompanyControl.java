package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.device.PagePerson;
import com.kunlun.firmwaresystem.entity.Company;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.device.Devicep;

import com.kunlun.firmwaresystem.mappers.CompanyMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.DeviceP_Sql;
import com.kunlun.firmwaresystem.sql.Company_Sql;
import com.kunlun.firmwaresystem.sql.Person_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_company;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class CompanyControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private CompanyMapper CompanyMapper;

    @Resource
    private MapMapper mapMapper;
    @Autowired
    private CompanyMapper companyMapper;

    @RequestMapping(value = "userApi/Company/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Company Company=CompanyMapper.selectById(id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", Company);
        return jsonObject1;
    }
   @RequestMapping(value = "userApi/Company/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateCompany(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
       try {
           JSONObject response = null;
           Customer customer = getCustomer(request);

           Company Company=null;
           Company = new Gson().fromJson(jsonObject.toString(), new TypeToken<Company>() {
           }.getType());
           Company.setProject_key(customer.getProject_key());
           Company_Sql Company_sql=new Company_Sql();
           int status= Company_sql.update(CompanyMapper,Company);
           if(status>0){
               return JsonConfig.getJsonObj(CODE_OK,"","");
           }else {
               return JsonConfig.getJsonObj(CODE_REPEAT,"","");
           }


       }catch (Exception e){
           myPrintln(e.toString());
           return null;
       }
    }

    //设备
    @RequestMapping(value = "userApi/getDeviceByCompany/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getDevice(HttpServletRequest request) {
        myPrintln("输出访问"+request.getParameter("id"));
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
        String id=request.getParameter("id");
        String limit=request.getParameter("limit");
        if(quickSearch==null||quickSearch.equals("")){
            quickSearch="";
        }
        if(page==null||page.equals("")){
            page="1";
        }
        if(limit==null||limit.equals("")){
            limit="10";
        }
        Customer customer = getCustomer(request);
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        PageDeviceP pageDeviceP=deviceP_sql.selectPageDevicePByCompany(devicePMapper,Integer.parseInt(page),Integer.parseInt(limit),customer.getProject_key(),Integer.parseInt(id));
        for (Devicep devicep:pageDeviceP.getDeviceList()){
            Company company=(Company) redisUtil.get(redis_key_company+ devicep.getCompany_id());
            if (company!=null) {
                devicep.setCompany_name(company.getName());
            }
            Station station=(Station) redisUtil.get(redis_key_locator + devicep.getNear_s_address());
            if (station!=null) {
                devicep.setStation_type(station.getType_name());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data", pageDeviceP.getDeviceList());
        return jsonObject;
    }
    //设备
    @RequestMapping(value = "userApi/getPersonByCompany/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getPerson(HttpServletRequest request) {
        myPrintln("输出访问"+request.getParameter("id"));
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
        String id=request.getParameter("id");
        String limit=request.getParameter("limit");
        if(quickSearch==null||quickSearch.equals("")){
            quickSearch="";
        }
        if(page==null||page.equals("")){
            page="1";
        }
        if(limit==null||limit.equals("")){
            limit="10";
        }
        Customer customer = getCustomer(request);
        Person_Sql personSql=new Person_Sql();
        PagePerson pagePerson=personSql.getPersonPageByCompany(personMapper,Integer.parseInt(page),Integer.parseInt(limit),customer.getUserkey(),customer.getProject_key(),Integer.parseInt(id));
        for (Person person : pagePerson.getPersonList()) {
            Company company=(Company) redisUtil.get(redis_key_company+ person.getCompany_id());
            if (company!=null) {
                person.setCompany_name(company.getName());
            }
            Station station=(Station) redisUtil.get(redis_key_locator + person.getStation_mac());
            if (station!=null) {
                person.setStation_type(station.getType_name());
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pagePerson.getTotal());
        jsonObject.put("data", pagePerson.getPersonList());
        return jsonObject;
    }
       @RequestMapping(value = "userApi/Company/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addArea(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {
            JSONObject response = null;
            Customer customer = getCustomer(request);

            Company Company=null;
            Company = new Gson().fromJson(jsonObject.toString(), new TypeToken<Company>() {
            }.getType());
            Company.setProject_key(customer.getProject_key());

            Company_Sql Company_sql=new Company_Sql();
            boolean status= Company_sql.add(CompanyMapper,Company);
            if(status){
                return JsonConfig.getJsonObj(CODE_OK,"","");
            }else {
                return JsonConfig.getJsonObj(CODE_REPEAT,"","");
            }


        }catch (Exception e){
            myPrintln(e.toString());
            return null;
        }
    }

    @RequestMapping(value = "userApi/Company/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllCompany1(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        Company_Sql Company_sql=new Company_Sql();
        List<Company> CompanyList=Company_sql.getAll(CompanyMapper,customer.getProject_key());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", CompanyList.size());
        jsonObject.put("data",  CompanyList);
        //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "userApi/Company/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllCompany(HttpServletRequest request) {

        try {
            Customer customer = getCustomer(request);
            Company_Sql Company_sql = new Company_Sql();
            List<Company> CompanyList = Company_sql.getAll(CompanyMapper, customer.getProject_key());
            DeviceP_Sql deviceP_sql = new DeviceP_Sql();

            for (Company company : CompanyList) {
                int device_count = 0;
                int person_count = 0;
                for (Devicep device : devicePMap.values()) {
                    if (device.getCompany_id() == company.getId()) {
                        device_count++;
                    }
                }
                for (Person person : personMap.values()) {
                    if (person.getCompany_id() == company.getId()) {
                        person_count++;
                    }
                }
                company.setPerson_count(person_count);
                company.setDevice_count(device_count);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", CompanyList.size());
            jsonObject.put("data", CompanyList);
            return jsonObject;
        }catch (Exception e){
            myPrintln("异常=="+e.toString());
        }
        //  myPrintln(System.currentTimeMillis());
        return null;
    }
    @RequestMapping(value = "/userApi/Company/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteArea(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Company_Sql Company_sql = new Company_Sql();
        List<Integer> id=new ArrayList<Integer>();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();

        for(Object ids:jsonArray){
            for (String sn:devicePMap.keySet()){
                Devicep devicep =devicePMap.get(sn);
                if (devicep!=null){
                    if (devicep.getCompany_id()==Integer.parseInt(ids.toString())){
                        return JsonConfig.getJsonObj(CODE_10,"","");
                    }
                }
            }
                id.add(Integer.parseInt(ids.toString()));
        }
        if(!id.isEmpty()){
            int status = Company_sql.deletes(CompanyMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }

    }
   /* @RequestMapping(value = "/userApi/Company/delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject delete1Area(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") int id) {
        myPrintln("区域ID="+id);
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Company_Sql Company_sql = new Company_Sql();
        Company Company=CompanyMapper.selectById(id);
        if(have){
            return JsonConfig.getJsonObj(CODE_10,null,lang);
        }
        area_sql.delete(areaMapper,id);
        return JsonConfig.getJsonObj(CODE_OK,null,lang);


    }*/
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}
