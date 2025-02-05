package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageGroup;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Locator;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.GroupMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_Station;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_locator;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class GroupControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private GroupMapper groupMapper;

    @Resource
    private MapMapper mapMapper;
    @RequestMapping(value = "userApi/group/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Group group=groupMapper.selectById(id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", group);
        return jsonObject1;
    }
   @RequestMapping(value = "userApi/group/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateGroup(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            Group  group=null;
            group = new Gson().fromJson(jsonObject.toString(), new TypeToken<com.kunlun.firmwaresystem.entity.Area>() {
            }.getType());

            group.setUpdate_time(System.currentTimeMillis()/1000);
            groupMapper.updateById(group);
            return response;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
   /* @RequestMapping(value = "userApi/area/add_update_Area", method = RequestMethod.POST, produces = "application/json")
    public JSONObject add_update_Area(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {

        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            String lang=customer.getLang();
            Area area=null;

            area = new Gson().fromJson(jsonObject.toString(), new TypeToken<Area>() {
            }.getType());

            System.out.println("area"+area.getMap_key());
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
                        if(Stations!=null||Stations.length()>2){
                            String gs[]=Stations.split("-");
                            for(int i=0;i<gs.length;i++){
                                if(gs[i]!=null&&gs[i].length()>0){
                                    String[] address_=gs[i].split(",");
                                    if(address_.length==2){
                                        switch (address_[1]){
                                            //蓝牙网关
                                            case "1":
                                                Station Station=(Station) redisUtil.get(redis_key_Station+address_[0]);

                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.set(redis_key_Station+address_[0],Station);
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
                                                Station Station=(Station) redisUtil.get(redis_key_Station+address_[0]);

                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.set(redis_key_Station+address_[0],Station);
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
                                                Station Station=(Station) redisUtil.get(redis_key_Station+address_[0]);
                                                Station_sql.updateStation(StationMapper,Station);
                                              //  StationMap=Station_sql.getAllStation(StationMapper);
                                                redisUtil.set(redis_key_Station+address_[0],Station);
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
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
            return response;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }

    }*/
    /*


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
/*
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
    */

    @RequestMapping(value = "userApi/group/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllGroup(HttpServletRequest request) {
        try {
            String quickSearch = request.getParameter("quickSearch");
            String page = request.getParameter("page");
            String limit = request.getParameter("limit");
            if (quickSearch == null || quickSearch.equals("")) {
                quickSearch = "";
            }
            if (page == null || page.equals("")) {
                page = "1";
            }
            if (limit == null || limit.equals("")) {
                limit = "20";
            }

            Customer user1 = getCustomer(request);
            Group_Sql group_sql = new Group_Sql();
            PageGroup pageGroup = group_sql.selectPage(groupMapper, Integer.parseInt(page), Integer.parseInt(limit), user1.getProject_key(), quickSearch);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", pageGroup.getGroupList().size());
            jsonObject.put("data", pageGroup.getGroupList());

            //  System.out.println(System.currentTimeMillis());
            return jsonObject;
        }catch (Exception e){
            System.out.println("获取组别异常="+e.getMessage());
            return null;
        }
    }
    @RequestMapping(value = "userApi/group/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addArea(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {
            JSONObject response = null;
            Customer customer = getCustomer(request);
            //  System.out.println("area666"+jsonObject.toString());
            Group group=null;
            group = new Gson().fromJson(jsonObject.toString(), new TypeToken<Group>() {
            }.getType());
            group.setProject_key(customer.getProject_key());
            System.out.println("Group="+group);
            group.setCreate_time(System.currentTimeMillis()/1000);
            Group_Sql group_sql=new Group_Sql();
            boolean status= group_sql.add(groupMapper,group);
            if(status){
                return JsonConfig.getJsonObj(CODE_OK,"","");
            }else {
                return JsonConfig.getJsonObj(CODE_REPEAT,"","");
            }


        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    @RequestMapping(value = "userApi/group/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllGroup1(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        Group_Sql group_sql=new Group_Sql();
        List<Group> groupList=group_sql.getAll(groupMapper,customer.getProject_key());
        Group group=new Group();
        String lang=customer.getLang();
        if(lang!=null&&lang.equals("en")){
            group.setGroup_name("No Group");
        }else{
            group.setGroup_name("不绑定组别");
        }
        group.setId(-1);
        groupList.add(0,group);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", groupList.size());
        jsonObject.put("data",  groupList);
        //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/group/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteArea(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Group_Sql group_sql = new Group_Sql();
        List<Integer> id=new ArrayList<Integer>();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        for(Object ids:jsonArray){
            List<Devicep> deviceps= deviceP_sql.getDeviceByAreaID(devicePMapper,Integer.parseInt(ids.toString()));
            if(deviceps!=null&&deviceps.size()>0){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }
                id.add(Integer.parseInt(ids.toString()));
        }
        if(id.size()>0){
            int status = group_sql.deletes(groupMapper, id);
            if(status!=-1){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }

    }
   /* @RequestMapping(value = "/userApi/group/delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject delete1Area(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") int id) {
        System.out.println("区域ID="+id);
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Group_Sql group_sql = new Group_Sql();
        Group group=groupMapper.selectById(id);
        if(have){
            return JsonConfig.getJsonObj(CODE_10,null,lang);
        }
        area_sql.delete(areaMapper,id);
        return JsonConfig.getJsonObj(CODE_OK,null,lang);


    }*/
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }
}
