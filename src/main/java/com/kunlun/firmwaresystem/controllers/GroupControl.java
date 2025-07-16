package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.device.Group;
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
import static com.kunlun.firmwaresystem.NewSystemApplication.redisUtil;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.device_person_group;
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
       try {
           JSONObject response = null;
           Customer customer = getCustomer(request);
           Group group=null;
           group = new Gson().fromJson(jsonObject.toString(), new TypeToken<Group>() {
           }.getType());
           group.setProject_key(customer.getProject_key());
           group.getTag_names();
           myPrintln("Group="+group);
           group.setCreate_time(System.currentTimeMillis()/1000);
           Group_Sql group_sql=new Group_Sql();
           int status= group_sql.update(groupMapper,group);
           if(status>0){
               redisUtil.setnoTimeOut(device_person_group + group.getId(),group);
               return JsonConfig.getJsonObj(CODE_OK,"","");

           }else {
               return JsonConfig.getJsonObj(CODE_REPEAT,"","");
           }


       }catch (Exception e){
           myPrintln(e.toString());
           return null;
       }
    }

    //设备
    @RequestMapping(value = "userApi/getDevice/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getDevice(HttpServletRequest request) {
        myPrintln("输出访问"+request.getParameter("id"));
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
        String id=request.getParameter("id");
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
        Customer customer = getCustomer(request);
        DeviceP_Sql  deviceP_sql=new DeviceP_Sql();
        PageDeviceP pageDeviceP=deviceP_sql.selectPageDevicePByGroup(devicePMapper,Integer.parseInt(page),Integer.parseInt(limit),quickSearch,customer.getUserkey(),customer.getProject_key(),Integer.parseInt(id));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data", pageDeviceP.getDeviceList());
        return jsonObject;
    }
       @RequestMapping(value = "userApi/group/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addArea(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {
            JSONObject response = null;
            Customer customer = getCustomer(request);

            Group group=null;
            group = new Gson().fromJson(jsonObject.toString(), new TypeToken<Group>() {
            }.getType());
            group.setProject_key(customer.getProject_key());
            group.getTag_names();
            myPrintln("Group="+group);
            group.setCreate_time(System.currentTimeMillis()/1000);

            Group_Sql group_sql=new Group_Sql();
            boolean status= group_sql.add(groupMapper,group);
            if(status){
                return JsonConfig.getJsonObj(CODE_OK,"","");
            }else {
                return JsonConfig.getJsonObj(CODE_REPEAT,"","");
            }


        }catch (Exception e){
            myPrintln(e.toString());
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
        //  myPrintln(System.currentTimeMillis());
        return jsonObject;
    }
    @RequestMapping(value = "userApi/group/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllGroup(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        Group_Sql group_sql=new Group_Sql();
        List<Group> groupList=group_sql.getAll(groupMapper,customer.getProject_key());
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        for (Group group : groupList) {
            int g_id=group.getId();
           List<Devicep> deviceps= deviceP_sql.getDeviceByGroupID(devicePMapper,g_id);
           group.setCount(deviceps.size());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", groupList.size());
        jsonObject.put("data",  groupList);

        //  myPrintln(System.currentTimeMillis());
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
            for (String sn:devicePMap.keySet()){
                Devicep devicep =devicePMap.get(sn);
                if (devicep!=null){
                    if (devicep.getGroup_id()==Integer.parseInt(ids.toString())){
                        return JsonConfig.getJsonObj(CODE_10,"","");
                    }
                }
            }
                id.add(Integer.parseInt(ids.toString()));
        }
        if(!id.isEmpty()){
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
        myPrintln("区域ID="+id);
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
        //   myPrintln("customer="+customer);
        return customer;
    }
}
