package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.device.PageArea;
import com.kunlun.firmwaresystem.device.PageFence;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.AreaMapper;
import com.kunlun.firmwaresystem.mappers.FenceMapper;
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
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class FenceControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private FenceMapper fenceMapper;

    @Resource
    private MapMapper mapMapper;
    @RequestMapping(value = "userApi/fence/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneMapEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);

        Fence fence=fenceMapper.selectById(id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", fence);
        return jsonObject1;
    }
    @RequestMapping(value = "userApi/fence/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateArea(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            String lang=customer.getLang();
            myPrintln("area666"+jsonObject.toString());
            Fence fence=null;
            Fence_Sql fence_sql=new Fence_Sql();
            fence = new Gson().fromJson(jsonObject.toString(), new TypeToken<Fence>() {
            }.getType());
            if(fence.getPoints()==null||fence.getPoints().isEmpty()){
                return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
;
            myPrintln(fence.toString());
            if(fence.getMap_key()!=null){
                fence.setUser_key(customer.getUserkey());
                fence.setProject_key(customer.getProject_key());
                fence.setCustomer_key(customer.getCustomerkey());
                fence.setUpdate_time(System.currentTimeMillis()/1000);
                if (fence_sql.update(fenceMapper, fence)>0) {
                    fenceMap=  fence_sql.getAllFence(fenceMapper);
                    response = JsonConfig.getJsonObj(CODE_OK, null,lang);
                } else {
                    response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
                }
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
            return response;
        }catch (Exception e){
            myPrintln(e.toString());
            return null;
        }
    }
    @RequestMapping(value = "userApi/fence/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addFence(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln("area666"+jsonObject.toString());
        Fence fence=null;

            fence = new Gson().fromJson(jsonObject.toString(), new TypeToken<Fence>() {
            }.getType());
            if(fence.getPoints()==null||fence.getPoints().isEmpty()){
                return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
            fence.setProject_key(customer.getProject_key());
            fence.setUser_key(customer.getUserkey());
            fence.setCustomer_key(customer.getCustomerkey());
            fence.setCreate_time(System.currentTimeMillis()/1000);

            Fence_Sql fence_sql=new Fence_Sql();
            if (fence_sql.addFence(fenceMapper, fence)) {
                fenceMap=fence_sql.getAllFence(fenceMapper);
                response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
            }
        return response;
        }catch (Exception e){
        myPrintln(e.toString());
        return null;
    }
    }

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

    @RequestMapping(value = "userApi/fence/index", method = RequestMethod.GET, produces = "application/json")
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
        Fence_Sql fence_sql = new Fence_Sql();
        PageFence pageFence = fence_sql.selectPageFence(fenceMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getUserkey(),user1.getProject_key(),quickSearch);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageFence.getFenceList().size());
        jsonObject.put("data",  pageFence.getFenceList());
        myPrintln(String.valueOf(System.currentTimeMillis()));
        return jsonObject;
    }
    @RequestMapping(value = "userApi/fence_used/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getFence_used(HttpServletRequest request) {
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
        String limit=request.getParameter("limit");
        if(quickSearch==null||quickSearch.equals("")){
            quickSearch="";
        }
        String quick_map=request.getParameter("quick_map");

        if(page==null||page.equals("")){
            page="1";
        }
        if(limit==null||limit.equals("")){
            limit="20";
        }

        Customer user1 = getCustomer(request);
        Fence_Sql fence_sql = new Fence_Sql();
        PageFence pageFence = fence_sql.selectPageFence_used(fenceMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getUserkey(),user1.getProject_key(),quickSearch,quick_map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageFence.getFenceList().size());
        jsonObject.put("data",  pageFence.getFenceList());
        myPrintln(String.valueOf(System.currentTimeMillis()));
        return jsonObject;
    }
    @RequestMapping(value = "userApi/fence/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllArea1(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Fence_Sql fence_sql = new Fence_Sql();
        List<Fence> fences=fence_sql.getAllFence(fenceMapper,customer.getUserkey(),customer.getProject_key());
        Fence fence=new Fence();
        if(lang!=null&&lang.equals("en")){
            fence.setName("UnBind");
        }else {
            fence.setName("不绑定围栏");
        }
        fence.setId(-1);
        fences.add(0,fence);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", fences.size());
        jsonObject.put("data",  fences);
        //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "userApi/fence/index2", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllArea2(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Fence_Sql fence_sql = new Fence_Sql();
        List<Fence> fences=fence_sql.getAllFence(fenceMapper,customer.getUserkey(),customer.getProject_key());
    /*    Fence fence=new Fence();
        if(lang!=null&&lang.equals("en")){
            fence.setName("UnBind");
        }else {
            fence.setName("不绑定围栏");
        }
        fence.setId(-1);
        fences.add(0,fence);*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", fences.size());
        jsonObject.put("data",  fences);
        //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/fence/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteArea(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Fence_Sql fence_sql = new Fence_Sql();
        List<Integer> id=new ArrayList<Integer>();
        Person_Sql person_sql=new Person_Sql();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();

        for(Object ids:jsonArray){
        /*    List<Devicep> deviceps= deviceP_sql.getDeviceByFenceID(devicePMapper,Integer.parseInt(ids.toString()));
            if(deviceps!=null&&deviceps.size()>0){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }
            List<Person> personList= person_sql.getPersonByFenceID(personMapper,Integer.parseInt(ids.toString()));
            if(personList!=null&&personList.size()>0){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }*/
            id.add(Integer.parseInt(ids.toString()));
        }



        if(id.size()>0){
            int status = fence_sql.deletes(fenceMapper, id);
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
