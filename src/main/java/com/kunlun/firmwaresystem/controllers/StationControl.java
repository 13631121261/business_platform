package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.*;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Locator;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.StationType;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.mappers.StationTypeMapper;
import com.kunlun.firmwaresystem.mqtt.DirectExchangeProducer;
import com.kunlun.firmwaresystem.sql.Locators_Sql;
import com.kunlun.firmwaresystem.sql.Station_sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.CODE_PARAMETER_NULL;

@RestController
public class StationControl {
    private static int ExpireTime = 60;   // redis中存储的过期时间60s
    @Autowired
    private StationTypeMapper stationTypeMapper;
    @Autowired
    private DirectExchangeProducer directExchangeProducer;
    @Resource
    private RedisUtils redisUtil;
    @Autowired
    private com.kunlun.firmwaresystem.mappers.StationMapper stationMapper;

    @RequestMapping(value = "/userApi/Station/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllStation(HttpServletRequest request){
        Enumeration<String> name=request.getParameterNames();
        while(name.hasMoreElements()) {
            myPrintln(name.nextElement());
        }
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
        String token = request.getHeader("batoken");
        String response = "默认参数";
        Customer customer=(Customer) redisUtil.get(token);
        Station_sql Station_sql = new Station_sql();
        PageStation pageStation = Station_sql.selectPageStation(StationMapper, Integer.parseInt(page), Integer.parseInt(limit), quickSearch, customer.getUserkey(),customer.getProject_key());
        myPrintln("网关信息="+pageStation.toString());
        for (Station station:pageStation.getStationList()){
            Station station1=(Station) redisUtil.get(redis_key_locator+station.getAddress());
            if (station1!=null){
                station.setOnline(station1.getOnline());
                station.setLast_time(station1.getLast_time());
                station.setMap_name(station1.getMap_name());
                station.setX(station1.getX());
                station.setY(station1.getY());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);

        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageStation.getTotal());
        jsonObject.put("data", pageStation.getStationList());

        return jsonObject;
    }



    @RequestMapping(value = "/userApi/Station/add", method = RequestMethod.POST,produces = "application/json")
    public JSONObject addStation(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        myPrintln(jsonObject.toString());
        JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Station Station=new Gson().fromJson(jsonObject.toString(),new TypeToken<Station>(){}.getType());
        Station.setAddress(Station.getAddress().toLowerCase());
        Station.setCreate_time(System.currentTimeMillis()/1000);
        Station.setLast_time(System.currentTimeMillis()/1000);
        Station.setProject_key(customer.getProject_key());
        Station.setUser_key(customer.getUserkey());
        Station_sql Station_sql = new Station_sql();
        if(Station.getType_id()==0){
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
        StationType stationType= stationTypeMapper.selectById(Station.getType_id());
        Station.setType_name(stationType.getName());
        if (Station_sql.addStation(StationMapper, Station)) {
            response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            redisUtil.set(redis_key_locator + Station.getAddress(), Station);
            station_maps.put(Station.getAddress(),Station.getAddress());
            myPrintln("运行这里");
        } else {
            response = JsonConfig.getJsonObj(JsonConfig.CODE_REPEAT, null,lang);
        }

        return response;
    }

    @RequestMapping(value = "userApi/Station/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneStation(HttpServletRequest request, @RequestParam("id") @ParamsNotNull String id) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Station_sql Station_sql=new Station_sql();
        Station Station= Station_sql.getStationById(StationMapper,Integer.parseInt(id));
        if(Station!=null){
            return JsonConfig.getJsonObj(CODE_OK,Station,lang);
        }else{
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Station/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject getOneStation(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        myPrintln(jsonObject.toString());
        JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Station Station=new Gson().fromJson(jsonObject.toString(),new TypeToken<Station>(){}.getType());
        Station.setAddress(Station.getAddress().toLowerCase());
        if(Station.getType_id()==0){
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
       StationType stationType= stationTypeMapper.selectById(Station.getType_id());
        Station.setType_name(stationType.getName());
        StationMapper.updateById(Station);
        return JsonConfig.getJsonObj(CODE_OK, Station, lang);
    }



    @RequestMapping(value = "/userApi/Station/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteStation(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Station_sql Station_sql = new Station_sql();
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(id.size()>0){
            int status = Station_sql.deletes(StationMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }

    }


    @RequestMapping(value = "/userApi/Station/getByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getStationMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "map_key") String map_key) {
        // myPrintln(System.currentTimeMillis());
        Customer customer = getCustomer(request);
        Station_sql station_sql = new Station_sql();

        try {
            List<Station> StationList = station_sql.selectByMap(stationMapper, customer.getProject_key(), map_key);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", StationList.size());
            jsonObject.put("data", StationList);
            // myPrintln(System.currentTimeMillis());
            return jsonObject;
        } catch (Exception e) {
            myPrintln(e.toString());
            return null;
        }
    }



    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }




}
