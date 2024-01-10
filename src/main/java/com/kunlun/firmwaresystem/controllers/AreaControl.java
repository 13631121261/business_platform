package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.Gateway;
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
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_gateway;
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
            System.out.println("area666"+jsonObject.toString());
            com.kunlun.firmwaresystem.entity.Area area=null;

            area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

            System.out.println("area"+area.getGateway_mac());
            if(area.getMap_key()!=null){
                area.setUserkey(customer.getUserkey());
                area.setProject_key(customer.getProject_key());
                area.setCustomer_key(customer.getCustomerkey());
                area.setUpdatetime(System.currentTimeMillis()/1000);
                Area_Sql area_sql=new Area_Sql();
                Area area1=area_sql.getAreaById(areaMapper,area.getId());
                String gateways=area1.getGateway_mac();
                Gateway_sql gateway_sql=new Gateway_sql();
                System.out.println("网关地址="+gateways);
                if(gateways!=null||gateways.length()>2){
                    String gs[]=gateways.split(",");

                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+gs[i]);
                            gateway.setArea_id(0);
                            gateway_sql.updateGateway(gatewayMapper,gateway);
                            GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                            redisUtil.set(redis_key_gateway+gs[i],gateway);
                        }
                    }
                }
                 gateways=area.getGateway_mac();
                System.out.println("新的网关地址="+gateways);
                if(gateways!=null||gateways.length()>2){
                    String gs[]=gateways.split(",");
                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+gs[i]);
                            gateway.setArea_id(area.getId());
                            gateway_sql.updateGateway(gatewayMapper,gateway);
                            GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                            redisUtil.set(redis_key_gateway+gs[i],gateway);
                            System.out.println("保存新的区域ID");
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
            System.out.println(e);
            return null;
        }
    }*/
    @RequestMapping(value = "userApi/area/add_update_Area", method = RequestMethod.POST, produces = "application/json")
    public JSONObject add_update_Area(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {

        try {  JSONObject response = null;
            Customer customer = getCustomer(request);

            com.kunlun.firmwaresystem.entity.Area area=null;

            area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

            System.out.println("area"+area.getMap_key());
            if(area.getMap_key()!=null){
                area.setUserkey(customer.getUserkey());
                area.setProject_key(customer.getProject_key());
                area.setCustomer_key(customer.getCustomerkey());
                area.setCreatetime(System.currentTimeMillis()/1000);
                Area_Sql area_sql=new Area_Sql();
                Gateway_sql gateway_sql=new Gateway_sql();
                Locators_Sql locators_sql=new Locators_Sql();
                if(area.getId()==0){
                    if (area_sql.addArea(areaMapper, area)) {
                        String  gateways=area.getGateway_mac();
                        if(gateways!=null||gateways.length()>2){
                            String gs[]=gateways.split("-");
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+address_[0]);
                                                gateway.setArea_id(area.getId());
                                                gateway.setArea_name(area.getName());
                                                gateway_sql.updateGateway(gatewayMapper,gateway);
                                                GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                                                redisUtil.set(redis_key_gateway+address_[0],gateway);
                                                break;
                                                //AOA 网关
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_id(area.getId());
                                                locator.setArea_name(area.getName());
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.set(redis_key_locator+address_[0],locator);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        area_Map=area_sql.getAllArea(areaMapper);
                        response = JsonConfig.getJsonObj(CODE_OK, null);
                    } else {
                        response = JsonConfig.getJsonObj(CODE_REPEAT, null);
                    }
                }else{
                    //编辑

                    Area area1=area_sql.getAreaById(areaMapper,area.getId());
                    if(area1.getGateway_mac()!=null&&area1.getGateway_mac().length()>0){
                        String  gateways=area1.getGateway_mac();
                        if(gateways!=null||gateways.length()>2){
                            String gs[]=gateways.split("-");
                             gateway_sql=new Gateway_sql();
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+address_[0]);
                                                gateway.setArea_id(0);
                                                gateway.setArea_name("");
                                                gateway_sql.updateGateway(gatewayMapper,gateway);
                                                GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                                                redisUtil.set(redis_key_gateway+address_[0],gateway);
                                                break;
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_name("");
                                                locator.setArea_id(0);
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.set(redis_key_locator+address_[0],locator);
                                                break;
                                    }
                                }
                            }
                        }
                    }
                    }
                    if(area.getGateway_mac()!=null&&area.getGateway_mac().length()>0){
                        String  gateways=area.getGateway_mac();
                        if(gateways!=null||gateways.length()>2){
                            String gs[]=gateways.split("-");
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+address_[0]);
                                                gateway.setArea_id(area.getId());
                                                gateway.setArea_name(area.getName());
                                                gateway_sql.updateGateway(gatewayMapper,gateway);
                                                GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                                                redisUtil.set(redis_key_gateway+address_[0],gateway);
                                                break;
                                            //AOA 网关
                                            case "2":
                                                Locator locator=(Locator) redisUtil.get(redis_key_locator+address_[0]);
                                                locator.setArea_id(area.getId());
                                                locator.setArea_name(area.getName());
                                                locators_sql.update(locatorMapper,locator);
                                                redisUtil.set(redis_key_locator+address_[0],locator);
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
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null);
            }
            return response;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }/*
    @RequestMapping(value = "userApi/area/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addArea(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
        Customer customer = getCustomer(request);
      //  System.out.println("area666"+jsonObject.toString());
        com.kunlun.firmwaresystem.entity.Area area=null;

        area = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

        System.out.println("area"+area.getMap_key());
        if(area.getMap_key()!=null){
            area.setUserkey(customer.getUserkey());
            area.setProject_key(customer.getProject_key());
            area.setCustomer_key(customer.getCustomerkey());
            area.setCreatetime(System.currentTimeMillis()/1000);
            Area_Sql area_sql=new Area_Sql();
            if (area_sql.addArea(areaMapper, area)) {
               String  gateways=area.getGateway_mac();
                if(gateways!=null||gateways.length()>2){
                    String gs[]=gateways.split(",");
                    Gateway_sql gateway_sql=new Gateway_sql();
                    for(int i=0;i<gs.length;i++){
                        if(gs[i]!=null&&gs[i].length()>0){
                            Gateway gateway=(Gateway) redisUtil.get(redis_key_gateway+gs[i]);
                            gateway.setArea_id(area.getId());
                            gateway_sql.updateGateway(gatewayMapper,gateway);
                            GatewayMap=gateway_sql.getAllGateway(gatewayMapper);
                            redisUtil.set(redis_key_gateway+gs[i],gateway);
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
        System.out.println(e);
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
       // System.out.println(System.currentTimeMillis());
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
      //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }*/

    @RequestMapping(value = "userApi/getAreaByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAreaByMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "map_key") String map_key) {

        Customer customer = getCustomer(request);
        Area_Sql area_sql = new Area_Sql();
        List<Area> areaList=area_sql.getAllArea(areaMapper,customer.getUserkey(),customer.getProject_key(), map_key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", areaList.size());
        jsonObject.put("data",  areaList);
        //  System.out.println(System.currentTimeMillis());
        return jsonObject;
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
        for(com.kunlun.firmwaresystem.entity.Area area:pageArea.getAreaList()) {
            int sum=0;
            for (String key : GatewayMap.keySet()) {
                if (GatewayMap.get(key).getArea_id()==(area.getId())) {
                   sum++;
                }
            }
            String gateways=area.getGateway_mac();
            if(gateways!=null||gateways.length()>2){
                String gs[]=gateways.split(",");
                int m=0;
                for(int i=0;i<gs.length;i++){
                    if(gs[i]!=null&&gs[i].length()>0){
                        m++;
                    }
                }
                area.setG_count(m);
            }else{
                area.setG_count(0);
            }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageArea.getAreaList().size());
        jsonObject.put("data",  pageArea.getAreaList());
      //  System.out.println(System.currentTimeMillis());
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
        //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/area/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteArea(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        Area_Sql area_sql = new Area_Sql();
        List<Integer> id=new ArrayList<Integer>();

        DeviceP_Sql deviceP_sql=new DeviceP_Sql();

        for(Object ids:jsonArray){
            List<Devicep> deviceps= deviceP_sql.getDeviceByAreaID(devicePMapper,Integer.parseInt(ids.toString()));
            if(deviceps!=null&&deviceps.size()>0){
                return JsonConfig.getJsonObj(CODE_10,null);
            }
                id.add(Integer.parseInt(ids.toString()));

        }
        if(id.size()>0){
            int status = area_sql.deletes(areaMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null);
        }

    }
    @RequestMapping(value = "/userApi/area/delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject delete1Area(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") int id) {
        System.out.println("区域ID="+id);
        Customer user = getCustomer(request);
        Area_Sql area_sql = new Area_Sql();
        Area area1=area_sql.getAreaById(areaMapper,id);
        Fence_Sql fence_sql=new Fence_Sql();
        boolean have=fence_sql.isHaveArea(fenceMapper,id);
        if(have){
            return JsonConfig.getJsonObj(CODE_10,null);
        }

        if(area1.getGateway_mac()!=null&&area1.getGateway_mac().length()>0){
            String  gateways=area1.getGateway_mac();
            if(gateways!=null||gateways.length()>2){
                String gs[]=gateways.split("-");
                Gateway_sql gateway_sql=new Gateway_sql();
                Locators_Sql locators_sql=new Locators_Sql();
                for(int i=0;i<gs.length;i++){
                    if(gs[i]!=null&&gs[i].length()>0){
                        String[] address_=gs[i].split(",");
                        if(address_.length==2){
                            try {
                                switch (address_[1]) {
                                    //蓝牙网关
                                    case "1":
                                        Gateway gateway = (Gateway) redisUtil.get(redis_key_gateway + address_[0]);
                                        gateway.setArea_id(0);
                                        gateway.setArea_name("");
                                        gateway_sql.updateGateway(gatewayMapper, gateway);
                                        GatewayMap = gateway_sql.getAllGateway(gatewayMapper);
                                        redisUtil.set(redis_key_gateway + address_[0], gateway);
                                        break;
                                    case "2":
                                        Locator locator = (Locator) redisUtil.get(redis_key_locator + address_[0]);
                                        locator.setArea_name("");
                                        locator.setArea_id(0);
                                        locators_sql.update(locatorMapper, locator);
                                        redisUtil.set(redis_key_locator + address_[0], locator);
                                        break;
                                }
                            }catch (Exception e){
                                continue;
                            }
                        }
                    }
                }
            }
        }
        area_sql.delete(areaMapper,id);
        return JsonConfig.getJsonObj(CODE_OK,null);


    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }
}
