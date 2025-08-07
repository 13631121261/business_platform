package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageMap;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Locator;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class AreaControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private AreaMapper areaMapper;

    @Resource
    private MapMapper mapMapper;
    @RequestMapping(value = "userApi/area/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneMapEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Area_Sql area_sql=new Area_Sql();
        Area area=area_sql.getAreaById(areaMapper,id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", area);
        return jsonObject1;
    }
   /* @RequestMapping(value = "userApi/area/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateArea(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            myPrintln("area666"+jsonObject.toString());
            com.kunlun.firmwaresystem.entity.Area area=null;

            area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

            myPrintln("area"+area.getStation_mac());
            if(area.getMap_key()!=null){
                area.setUserkey(customer.getUserkey());
                area.setProject_key(customer.getProject_key());
                area.setCustomer_key(customer.getCustomerkey());
                area.setUpdatetime(System.currentTimeMillis()/1000);
                Area_Sql area_sql=new Area_Sql();
                Area area1=area_sql.getAreaById(areaMapper,area.getId());
                String Stations=area1.getStation_mac();
                Station_sql Station_sql=new Station_sql();
                myPrintln("网关地址="+Stations);
                if(Stations!=null||Stations.length()>2){
                    String gs[]=Stations.split(",");

                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Station Station=(Station) redisUtil.get(redis_key_Station+gs[i]);
                            Station.setArea_id(0);
                            Station_sql.updateStation(StationMapper,Station);
                            StationMap=Station_sql.getAllStation(StationMapper);
                            redisUtil.set(redis_key_Station+gs[i],Station);
                        }
                    }
                }
                 Stations=area.getStation_mac();
                myPrintln("新的网关地址="+Stations);
                if(Stations!=null||Stations.length()>2){
                    String gs[]=Stations.split(",");
                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Station Station=(Station) redisUtil.get(redis_key_Station+gs[i]);
                            Station.setArea_id(area.getId());
                            Station_sql.updateStation(StationMapper,Station);
                            StationMap=Station_sql.getAllStation(StationMapper);
                            redisUtil.set(redis_key_Station+gs[i],Station);
                            myPrintln("保存新的区域ID");
                        }
                    }
                }
                if (area_sql.update(areaMapper, area)>0) {
                    area_Map=area_sql.getAllArea(areaMapper);
                    response = JsonConfig.getJsonObj(CODE_OK, null);
                } else {
                    response = JsonConfig.getJsonObj(CODE_REPEAT, null);
                }
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null);
            }
            return response;
        }catch (Exception e){
            myPrintln(e);
            return null;
        }
    }*/
    @RequestMapping(value = "userApi/area/add_update_Area", method = RequestMethod.POST, produces = "application/json")
    public JSONObject add_update_Area(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {

        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            String lang=customer.getLang();
            com.kunlun.firmwaresystem.entity.Area area=null;

            area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

            myPrintln("area"+area.getMap_key());
            if(area.getMap_key()!=null){
                area.setUserkey(customer.getUserkey());
                area.setProject_key(customer.getProject_key());
                area.setCustomer_key(customer.getCustomerkey());
                area.setCreatetime(System.currentTimeMillis()/1000);
                Area_Sql area_sql=new Area_Sql();
                Station_sql Station_sql=new Station_sql();
                Locators_Sql locators_sql=new Locators_Sql();
                if(area.getId()==0){
                    if (area_sql.addArea(areaMapper, area)) {
                        String  Stations=area.getStation_mac();
                        if(Stations!=null&&Stations.length()>2){
                            String gs[]=Stations.split("-");
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Station Station=(Station) redisUtil.get(redis_key_locator+address_[0]);

                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],Station);
                                                break;
                                                //AOA 网关
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_id(area.getId());
                                                locator.setArea_name(area.getName());
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],locator);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        area_Map=area_sql.getAllArea(areaMapper);
                        response = JsonConfig.getJsonObj(CODE_OK, null,lang);
                    } else {
                        response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
                    }
                }else{
                    //编辑

                    Area area1=area_sql.getAreaById(areaMapper,area.getId());
                    if(area1.getStation_mac()!=null&&area1.getStation_mac().length()>0){
                        String  Stations=area1.getStation_mac();
                        if(Stations!=null||Stations.length()>2){
                            String gs[]=Stations.split("-");
                             Station_sql=new Station_sql();
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Station Station=(Station) redisUtil.get(redis_key_locator+address_[0]);

                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],Station);
                                                break;
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_name("");
                                                locator.setArea_id(0);
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],locator);
                                                break;
                                    }
                                }
                            }
                        }
                    }
                    }
                    if(area.getStation_mac()!=null&&area.getStation_mac().length()>0){
                        String  Stations=area.getStation_mac();
                        if(Stations!=null||Stations.length()>2){
                            String gs[]=Stations.split("-");
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Station Station=(Station) redisUtil.get(redis_key_locator+address_[0]);
                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],Station);
                                                break;
                                            //AOA 网关
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_id(area.getId());
                                                locator.setArea_name(area.getName());
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.setnoTimeOut(redis_key_locator+address_[0],locator);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    area_sql.update(areaMapper,area);
                }

            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
            return response;
        }catch (Exception e){
            myPrintln(e.toString());
            return null;
        }
    }/*
    @RequestMapping(value = "userApi/area/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addArea(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
        Customer customer = getCustomer(request);
      //  myPrintln("area666"+jsonObject.toString());
        com.kunlun.firmwaresystem.entity.Area area=null;

        area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

        myPrintln("area"+area.getMap_key());
        if(area.getMap_key()!=null){
            area.setUserkey(customer.getUserkey());
            area.setProject_key(customer.getProject_key());
            area.setCustomer_key(customer.getCustomerkey());
            area.setCreatetime(System.currentTimeMillis()/1000);
            Area_Sql area_sql=new Area_Sql();
            if (area_sql.addArea(areaMapper, area)) {
               String  Stations=area.getStation_mac();
                if(Stations!=null||Stations.length()>2){
                    String gs[]=Stations.split(",");
                    Station_sql Station_sql=new Station_sql();
                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Station Station=(Station) redisUtil.get(redis_key_Station+gs[i]);
                            Station.setArea_id(area.getId());
                            Station_sql.updateStation(StationMapper,Station);
                            StationMap=Station_sql.getAllStation(StationMapper);
                            redisUtil.set(redis_key_Station+gs[i],Station);
                        }
                    }
                }
                area_Map=area_sql.getAllArea(areaMapper);
                response = JsonConfig.getJsonObj(CODE_OK, null);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null);
        }
        return response;
        }catch (Exception e){
        myPrintln(e);
        return null;
    }
    }
*/
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

    /*@RequestMapping(value = "userApi/area/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllbindMap(HttpServletRequest request) {
       // myPrintln(System.currentTimeMillis());
        Customer user1 = getCustomer(request);
        Map_Sql map_sql = new Map_Sql();
        List<com.kunlun.firmwaresystem.entity.Map> mapList = map_sql.getAllMap(mapMapper, user1.getUserkey(),user1.getProject_key());
        com.kunlun.firmwaresystem.entity.Map map=new com.kunlun.firmwaresystem.entity.Map();
        map.setName("不绑定区域");
        map.setMap_key("nomap");
        mapList.add(map);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", mapList.size());
        jsonObject.put("data", mapList);
      //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }*/

    @RequestMapping(value = "userApi/getAreaByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAreaByMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "map_key") String map_key) {
        try {
            Customer customer = getCustomer(request);
            Area_Sql area_sql = new Area_Sql();
            List<Area> areaList = area_sql.getAllArea(areaMapper, customer.getUserkey(), customer.getProject_key(), map_key);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", areaList.size());
            jsonObject.put("data", areaList);
            myPrintln(String.valueOf(System.currentTimeMillis()));
            return jsonObject;
        }catch (Exception e){
            myPrintln("获取区域信息="+e.getMessage());
            return null;
        }
    }
    @RequestMapping(value = "userApi/area/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllArea(HttpServletRequest request) {
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
            limit="20";
        }

        Customer user1 = getCustomer(request);
        Area_Sql area_sql = new Area_Sql();
        PageArea pageArea = area_sql.selectPageArea(areaMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getUserkey(),user1.getProject_key(),quickSearch);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageArea.getAreaList().size());
        jsonObject.put("data",  pageArea.getAreaList());
      //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "userApi/area/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllArea1(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        Area_Sql area_sql = new Area_Sql();
        List<Area> areaList=area_sql.getAllArea(areaMapper,customer.getUserkey(),customer.getProject_key());
        Area area=new Area();
        String lang=customer.getLang();
        if(lang!=null&&lang.equals("en")){
            area.setName("Not Bind Area");
        }else{
            area.setName("不绑定区域");
        }

        area.setId(-1);
        areaList.add(0,area);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", areaList.size());
        jsonObject.put("data",  areaList);
        //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/area/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteArea(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Area_Sql area_sql = new Area_Sql();
        List<Integer> id=new ArrayList<Integer>();

        DeviceP_Sql deviceP_sql=new DeviceP_Sql();

        for(Object ids:jsonArray){
       /*     List<Devicep> deviceps= deviceP_sql.getDeviceByAreaID(devicePMapper,Integer.parseInt(ids.toString()));
            if(deviceps!=null&&deviceps.size()>0){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }*/
                id.add(Integer.parseInt(ids.toString()));

        }
        if(id.size()>0){
            int status = area_sql.deletes(areaMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }

    }
    @RequestMapping(value = "/userApi/area/delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject delete1Area(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") int id) {
        myPrintln("区域ID="+id);
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Area_Sql area_sql = new Area_Sql();
        area_sql.delete(areaMapper,id);
        return JsonConfig.getJsonObj(CODE_OK,null,lang);


    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}
