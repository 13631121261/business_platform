package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageMap;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.Alarm_Sql;
import com.kunlun.firmwaresystem.sql.Map_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.StationMap;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class AlarmControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private AlarmMapper alarmMapper;



/*
    @RequestMapping(value = "userApi/selectPageMap", method = RequestMethod.GET, produces = "text/plain")
    public String selectPageMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "page") String page,
                                @ParamsNotNull @RequestParam(value = "limit") String limit, @RequestParam(value = "name") String name) {
        String response = null;
        Customer user1 = getCustomer(request);
        if (!user1.getUsername().equals("admin")) {
            ////预留后续的权限，只有管理员才能创建用户
        }
        Map_Sql map_sql = new Map_Sql();
        PageMap pageMap = map_sql.selectPageMap(mapMapper, Integer.parseInt(page), Integer.parseInt(limit), user1.getCustomerkey(), name);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageMap.getTotal());
        jsonObject.put("data", pageMap.getMapList());
        return jsonObject.toString();

    }*/

    /*@RequestMapping(value = "userApi/map/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllbindMap(HttpServletRequest request) {
       // System.out.println(System.currentTimeMillis());
        Customer user1 = getCustomer(request);
        Map_Sql map_sql = new Map_Sql();
        List<com.kunlun.firmwaresystem.entity.Map> mapList = map_sql.getAllMap(mapMapper, user1.getUserkey(),user1.getProject_key());
        com.kunlun.firmwaresystem.entity.Map map=new com.kunlun.firmwaresystem.entity.Map();
        map.setName("不绑定地图");
        map.setMap_key("nomap");
        mapList.add(map);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", mapList.size());
        jsonObject.put("data", mapList);
      //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }

    @RequestMapping(value = "userApi/map/index2", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllMap1(HttpServletRequest request) {
        // System.out.println(System.currentTimeMillis());
        Customer user1 = getCustomer(request);
        Map_Sql map_sql = new Map_Sql();
        List<com.kunlun.firmwaresystem.entity.Map> mapList = map_sql.getAllMap(mapMapper, user1.getUserkey(),user1.getProject_key());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", mapList.size());
        jsonObject.put("data", mapList);
        //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }*/
    @RequestMapping(value = "userApi/Alarm/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllMap(HttpServletRequest request) {
        String quickSearch=request.getParameter("quickSearch");
        String alarm_object=request.getParameter("alarm_object");
        String alarm_type=request.getParameter("alarm_type");
        String page=request.getParameter("page");
        String limit=request.getParameter("limit");
        if(quickSearch==null||quickSearch.equals("")){
            quickSearch="";
        }
        if(alarm_object==null||alarm_object.equals("")){
            alarm_object="";
        }
        if(alarm_type==null||alarm_type.equals("")){
            alarm_type="";
        }
        if(page==null||page.equals("")){
            page="1";
        }
        if(limit==null||limit.equals("")){
            limit="10";
        }

        Customer user1 = getCustomer(request);
        Alarm_Sql alarm_sql = new Alarm_Sql();
        PageAlarm pageAlarm = alarm_sql.selectPageAlarm(alarmMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getProject_key(),alarm_object,alarm_type,quickSearch);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageAlarm.getTotal());
        jsonObject.put("data",  pageAlarm.getAlarmList());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/Alarm/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteAlarm(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        List<Integer> id=new ArrayList<Integer>();

        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(id.size()>0){
            int status = alarmMapper.deleteBatchIds(id);
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
        //   System.out.println("customer="+customer);
        return customer;
    }
}
