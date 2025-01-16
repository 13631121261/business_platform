package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kunlun.firmwaresystem.device.PageAlarm;
import com.kunlun.firmwaresystem.device.PageLocator;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Locator;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.LocatorMapper;
import com.kunlun.firmwaresystem.sql.Alarm_Sql;
import com.kunlun.firmwaresystem.sql.Locators_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class LocatorControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private LocatorMapper locatorMapper;



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
    @RequestMapping(value = "userApi/Locator/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllLocator(HttpServletRequest request) {
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

        Customer user1 = getCustomer(request);
        Locators_Sql locatorsSql = new Locators_Sql();
        PageLocator pageLocator = locatorsSql.selectPageLocator(locatorMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getProject_key(),quickSearch);
        for(Locator locator:pageLocator.getLocatorList()){
            if(redisUtil.get(redis_key_locator+locator.getAddress())!=null){
                Locator locator1=(Locator) redisUtil.get(redis_key_locator+locator.getAddress());
                // System.out.println(locator1);
                locator1.setId(locator.getId());
                locatorMapper.updateById(locator1);
                locator.setOnline(locator1.getOnline());
                locator.setLast_time(locator1.getLast_time());
                redisUtil.set(redis_key_locator+locator.getAddress(),locator1);
            }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageLocator.getTotal());
        jsonObject.put("data",  pageLocator.getLocatorList());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/Locator/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteLocator(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        List<Integer> id=new ArrayList<Integer>();
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(id.size()>0){
            List<Locator> locators=locatorMapper.selectBatchIds(id);
            int status = locatorMapper.deleteBatchIds(id);
            System.out.println("执行删除基站"+locators.size());
            if(status!=-1){
                //删除基站缓存
                System.out.println("执行删除基站9"+locators.size());
                for(Locator locator:locators){
                    System.out.println("执行删除基站"+locator.getAddress());
                    redisUtil.set(redis_key_locator+locator.getAddress(),null);
                }
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }
    }


    @RequestMapping(value = "userApi/getAoALocatorByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAoALocatorByMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "map_key") String map_key) {
        // System.out.println(System.currentTimeMillis());
        Customer customer = getCustomer(request);
        Locators_Sql locators_sql=new Locators_Sql();

        try{
            List<Locator> StationList=locators_sql.selectByMap(locatorMapper,customer.getProject_key(),map_key);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", StationList.size());
            jsonObject.put("data", StationList);
            // System.out.println(System.currentTimeMillis());
            return jsonObject;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }
}
