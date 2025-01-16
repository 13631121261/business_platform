package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.NewSystemApplication;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.device.PageMap;
import com.kunlun.firmwaresystem.entity.Customer;

import com.kunlun.firmwaresystem.entity.User;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.Station_sql;
import com.kunlun.firmwaresystem.sql.Map_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.StationMap;
import static com.kunlun.firmwaresystem.NewSystemApplication.StationMapper;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_Station;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class MapControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private MapMapper mapMapper;
    @RequestMapping(value = "userApi/map/upload", method = RequestMethod.POST, produces = "application/json")
    public String upload(MultipartHttpServletRequest request){
            System.out.println("上传地图");
            Enumeration<String> parameter= request.getParameterNames();
            String map_key="";
            while (parameter.hasMoreElements()) {
               // System.out.println("输出key=" + parameter.nextElement());
                map_key=map_key+request.getParameter(parameter.nextElement());
            }
            if(map_key.length()>0){
                System.out.println("旧的输出key=" +map_key);
            }else{
                System.out.println("需要新的key");
            }
            Map<String, MultipartFile> map = request.getFileMap();
            List<MultipartFile> files=new ArrayList<>();
            for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
                if(entry.getKey().equals("map")){
                    String svg_data = null;
                    try {
                        svg_data = new String(entry.getValue().getBytes());
                       // System.out.println(svg_data);
                        if(map_key.length()==0){
                            map_key = Base64.getEncoder().encodeToString(("map" + "_" + System.currentTimeMillis()).getBytes()).replaceAll("\\+", "");
                        }
                       redisUtil.set(map_key,svg_data);
                        return map_key;
                     } catch (IOException ioException) {
                        System.err.println("File Error!");
                        return  "";
                    }
                }
            //    System.out.println(entry.getKey());
             //   System.out.println(entry.getValue().getSize());
                files.add(entry.getValue());
            }

        return  "";
    }
    @RequestMapping(value = "userApi/map/getByKey", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneMapByKey(HttpServletRequest request) {
        String map_key= request.getParameter("mapkey");
        Map_Sql map_sql=new Map_Sql();
        com.kunlun.firmwaresystem.entity.Map map=map_sql.getMapByMapkey(mapMapper,map_key);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", map);
        return jsonObject1;
    }
    @RequestMapping(value = "userApi/map/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneMapEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Map_Sql map_sql=new Map_Sql();
        com.kunlun.firmwaresystem.entity.Map map=map_sql.getMapById(mapMapper,id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", map);
        return jsonObject1;
    }
    @RequestMapping(value = "userApi/map/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateMap(HttpServletRequest request,@RequestBody com.alibaba.fastjson.JSONObject jsonObject) {
        JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        com.kunlun.firmwaresystem.entity.Map map =new Gson().fromJson(jsonObject.toString(),new TypeToken<com.kunlun.firmwaresystem.entity.Map>(){}.getType());
        System.out.println(map.toString());
        if(map.getMap_key()!=null){
            map.setUser_key(customer.getUserkey());
            map.setProject_key(customer.getProject_key());
            map.setCustomer_key(customer.getCustomerkey());
            map.setCreate_time(System.currentTimeMillis()/1000);
            String map_key=map.getMap_key();
            String svg_data=(String)redisUtil.get(map_key);
            if(svg_data!=null){
               // redisUtil.setRemove(map_key);
                map.setData(svg_data);
            }
            Map_Sql map_sql = new Map_Sql();
            if (map_sql.update(mapMapper, map)>0) {
                response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
        return response;
    }
    @RequestMapping(value = "userApi/map/add", method = RequestMethod.POST, produces = "application/json")
    public com.alibaba.fastjson.JSONObject addMap(HttpServletRequest request,  @RequestBody com.alibaba.fastjson.JSONObject jsonObject) {
        com.alibaba.fastjson.JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        com.kunlun.firmwaresystem.entity.Map map =new Gson().fromJson(jsonObject.toString(),new TypeToken<com.kunlun.firmwaresystem.entity.Map>(){}.getType());
        if(map.getMap_key()!=null){
            map.setUser_key(customer.getUserkey());
            map.setProject_key(customer.getProject_key());
            map.setProportion(10);
            map.setCustomer_key(customer.getCustomerkey());
            map.setCreate_time(System.currentTimeMillis()/1000);
            String map_key=map.getMap_key();
            String svg_data=(String)redisUtil.get(map_key);
          //  redisUtil.setRemove(map_key);
            map.setData(svg_data);
            Map_Sql map_sql = new Map_Sql();
            if (map_sql.addMap(mapMapper, map,redisUtil)) {
                response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }
        return response;
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

    @RequestMapping(value = "userApi/map/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllbindMap(HttpServletRequest request) {
       // System.out.println(System.currentTimeMillis());
        Customer customer=getCustomer(request);
        Customer user1 = getCustomer(request);
        Map_Sql map_sql = new Map_Sql();
        List<com.kunlun.firmwaresystem.entity.Map> mapList = map_sql.getAllMap(mapMapper, user1.getUserkey(),user1.getProject_key());
        com.kunlun.firmwaresystem.entity.Map map=new com.kunlun.firmwaresystem.entity.Map();
        String lang=customer.getLang();
        if(lang!=null&&lang.equals("en")){
            map.setName("Not Bind Map");
        }else{
            map.setName("不绑定地图");
        }
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
    }
    @RequestMapping(value = "userApi/map/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllMap(HttpServletRequest request) {
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
        Map_Sql map_sql = new Map_Sql();
        PageMap pageMap = map_sql.selectPageMap(mapMapper,Integer.parseInt(page),Integer.parseInt(limit), user1.getUserkey(),user1.getProject_key(),quickSearch);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageMap.getMapList().size());
        jsonObject.put("data",  pageMap.getMapList());
      //  System.out.println(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/map/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteMap(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Map_Sql map_sql = new Map_Sql();
        List<Integer> id=new ArrayList<Integer>();

        for(Object ids:jsonArray){
            if(ids!=null&& !ids.toString().isEmpty()){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(!id.isEmpty()){
            int status = map_sql.deletes(mapMapper, id);
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
