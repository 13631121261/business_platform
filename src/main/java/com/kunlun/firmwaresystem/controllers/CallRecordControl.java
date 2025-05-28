package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PagePerson;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.CallRecordMapper;
import com.kunlun.firmwaresystem.mappers.DepartmentMapper;
import com.kunlun.firmwaresystem.mappers.PersonMapper;
import com.kunlun.firmwaresystem.sql.CallRecord_Sql;
import com.kunlun.firmwaresystem.sql.Person_Sql;
import com.kunlun.firmwaresystem.sql.Tag_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;
import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;

@RestController
public class CallRecordControl {

    @Autowired
    private CallRecordMapper callRecordMapper;
    @Autowired
    private RedisUtils redisUtil;
    @RequestMapping(value = "/userApi/CallRecord/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject selectCallRecord(HttpServletRequest request){

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

        Customer customer=getCustomer(request);
        CallRecord_Sql callRecordSql=new CallRecord_Sql();
        PageCallRecord pageCallRecord=callRecordSql.selectPage(callRecordMapper,Integer.valueOf(page),Integer.valueOf(limit),customer.getUserkey(),customer.getProject_key());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageCallRecord.getTotal());
        jsonObject.put("data", pageCallRecord.getCallRecords());
        return jsonObject;
    }




    @RequestMapping(value = "userApi/CallRecord/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteCallRecord(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        CallRecord_Sql callRecordSql=new CallRecord_Sql();
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(!id.isEmpty()){
            int status    =callRecordMapper.deleteBatchIds(id);
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
        return customer;
    }

}
