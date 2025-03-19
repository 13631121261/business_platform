package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageStationType;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.StationType;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.StationTypeMapper;
import com.kunlun.firmwaresystem.sql.StationType_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class StationTypeControl {
    @Autowired
    private StationTypeMapper stationTypeMapper;
    @Resource
    private RedisUtils redisUtil;
    @RequestMapping(value = "/userApi/StationType/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllStationType(HttpServletRequest request){
        Enumeration<String> name=request.getParameterNames();

        String response = "默认参数";
        Customer customer=getCustomer(request);
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
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
        StationType_Sql StationType_sql = new StationType_Sql();
        PageStationType pageStationType = StationType_sql.selectPage(stationTypeMapper, Integer.parseInt(page), Integer.parseInt(limit),  customer.getProject_key(),quickSearch);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageStationType.getTotal());
        jsonObject.put("data", pageStationType.getStationTypeList());
        return jsonObject;
    }

    @RequestMapping(value = "/userApi/StationType/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllStationType1(HttpServletRequest request){
        Enumeration<String> name=request.getParameterNames();

        String response = "默认参数";
        Customer customer=getCustomer(request);
        StationType_Sql StationType_sql = new StationType_Sql();
        List<StationType> stationTypeList= StationType_sql.getAll(stationTypeMapper,  customer.getProject_key());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("data", stationTypeList);
        return jsonObject;
    }

    @RequestMapping(value = "/userApi/StationType/add", method = RequestMethod.POST,produces = "application/json")
    public JSONObject addStationType(HttpServletRequest request,@RequestBody JSONObject jsonObject) {

        JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        StationType StationType=new Gson().fromJson(jsonObject.toString(),new TypeToken<StationType>(){}.getType());
        StationType.setCreate_time(System.currentTimeMillis()/1000);
        StationType.setProject_key(customer.getProject_key());
        StationType_Sql StationType_sql = new StationType_Sql();
        try {
            if (StationType_sql.addStationType(stationTypeMapper, StationType)) {
                response = JsonConfig.getJsonObj(CODE_OK, null, lang);

            } else {
                response = JsonConfig.getJsonObj(JsonConfig.CODE_REPEAT, null, lang);
            }
        }catch (Exception e){
            myPrintln("---"+e);
        }
        return response;
    }

    @RequestMapping(value = "userApi/StationType/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneStationType(HttpServletRequest request, @RequestParam("id") @ParamsNotNull String id) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        StationType_Sql StationType_sql=new StationType_Sql();
        StationType StationType=stationTypeMapper.selectById(id);
        if(StationType!=null){
            return JsonConfig.getJsonObj(CODE_OK,StationType,lang);
        }else{
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
    }

    @RequestMapping(value = "userApi/StationType/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject update(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        myPrintln(jsonObject.toString());
        JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        StationType StationType=new Gson().fromJson(jsonObject.toString(),new TypeToken<StationType>(){}.getType());

        StationType.setProject_key(customer.getProject_key());
        if(StationType.getId()!=0&StationType.getProject_key()!=null){
            stationTypeMapper.updateById(StationType); return JsonConfig.getJsonObj(CODE_OK,StationType,lang);
        }else {
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }

    }



    @RequestMapping(value = "/userApi/StationType/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteStationType(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        StationType_Sql StationType_sql = new StationType_Sql();
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(id.size()>0){
            int status = StationType_sql.deletes(stationTypeMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }

    }

    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }




}
