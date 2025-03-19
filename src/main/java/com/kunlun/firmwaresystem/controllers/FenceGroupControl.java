package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageDeviceP;
import com.kunlun.firmwaresystem.device.PageFence;
import com.kunlun.firmwaresystem.device.PageFenceGroup;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Fence;
import com.kunlun.firmwaresystem.entity.Fence_group;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.mappers.FenceGroupMapper;
import com.kunlun.firmwaresystem.mappers.FenceMapper;
import com.kunlun.firmwaresystem.mappers.GroupMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;


@RestController
public class FenceGroupControl {
    private static final Logger log = LogManager.getLogger(FenceGroupControl.class);
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private FenceMapper fenceMapper;
    @Resource
    private FenceGroupMapper fenceGroupMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private MapMapper mapMapper;
    @RequestMapping(value = "userApi/Fence_group/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getOneFence_group(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Fence_group fence=fenceGroupMapper.selectById(id);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", fence);
        return jsonObject1;
    }
    @RequestMapping(value = "userApi/Fence_group/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateFence_group(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        try {
            Customer customer = getCustomer(request);
            String lang=customer.getLang();
            myPrintln("area666"+jsonObject.toString());
            Fence_group fenceGroup=null;

            fenceGroup = new Gson().fromJson(jsonObject.toString(), new TypeToken<Fence_group>() {
            }.getType());
            if(fenceGroup.getF_id()==null||fenceGroup.getF_id().isEmpty()){
                return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
            fenceGroup.setProject_key(customer.getProject_key());

            fenceGroup.setCustomer_key(customer.getCustomerkey());
            fenceGroup.setUpdate_time(System.currentTimeMillis()/1000);

            Fence_Group_Sql fence_sql=new Fence_Group_Sql();
            fence_sql.update(fenceGroupMapper,fenceGroup);
            return JsonConfig.getJsonObj(CODE_OK, null,lang);
        }catch (Exception e){
            log.error("异常: ", e);
            return null;
        }
    }
    @RequestMapping(value = "userApi/Fence_group/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addFence_group(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln("area666"+jsonObject.toString());
        Fence_group fenceGroup=null;

            fenceGroup = new Gson().fromJson(jsonObject.toString(), new TypeToken<Fence_group>() {
            }.getType());
            if(fenceGroup.getF_id()==null||fenceGroup.getF_id().isEmpty()){
                return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
            fenceGroup.setProject_key(customer.getProject_key());

            fenceGroup.setCustomer_key(customer.getCustomerkey());
            fenceGroup.setCreate_time(System.currentTimeMillis()/1000);

            Fence_Group_Sql fence_sql=new Fence_Group_Sql();
            if (fence_sql.add(fenceGroupMapper, fenceGroup)) {

                response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
            }
        return response;
        }catch (Exception e){
        log.error("异常: ", e);
        return null;
    }
    }

    @RequestMapping(value = "userApi/Fence_group/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllFence_group(HttpServletRequest request) {
        try {
            Customer customer = getCustomer(request);
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
            Fence_Group_Sql fence_sql = new Fence_Group_Sql();
            PageFenceGroup pageFence = fence_sql.selectPage(fenceGroupMapper, Integer.parseInt(page), Integer.parseInt(limit), user1.getProject_key(), quickSearch);
            Group_Sql group_sql = new Group_Sql();
            for (Fence_group group: pageFence.getFenceGroups()){
                ArrayList<Fence_group.T> device_person=new ArrayList<>();
                String f_id=group.getF_id();
                if (f_id!=null&& !f_id.isEmpty()){
                    String[] f_ids=group.getF_id().split("-9635241-");
                    if (f_ids.length>0){
                        ArrayList<Fence> fences=new ArrayList<>();
                        for (int i=0;i<f_ids.length;i++){
                            if (f_ids[i]!=null&&!f_ids[i].isEmpty()){
                                int f_id_int=Integer.parseInt(f_ids[i]);
                               Fence fence= fenceMapper.selectById(f_id_int);
                               if (fence!=null){
                                   fences.add(fence);
                               }
                            }

                        }
                        group.setFences(fences);

                    }
                }

                int f_g_id=group.getId();
                //设备组使用围栏组信息
             List<Group> groups=   group_sql.getFromFenceGroup(groupMapper,customer.getProject_key(),f_g_id);
             DeviceP_Sql deviceP_Sql=new DeviceP_Sql();
             if(!groups.isEmpty()){

                 for (Group group1: groups){
                     //设备组使用该围栏组，对应查出全部的单个设备
                     List<Devicep> devicepList =deviceP_Sql.getDeviceByGroupID(devicePMapper,group1.getId());
                     for (Devicep devicep: devicepList){
                         Fence_group.T t=new Fence_group.T();
                         t.setSn(devicep.getSn());
                         t.setType(1);
                         t.setName(devicep.getName());
                         device_person.add(t);
                     }
                 }
             }
             //单个设备使用围栏组
             List<Devicep> devicepList=   deviceP_Sql.getDeviceByFenceGroupID(devicePMapper,f_g_id);
             for (Devicep devicep: devicepList){
                     Fence_group.T t=new Fence_group.T();
                     t.setSn(devicep.getSn());
                     t.setType(1);
                     t.setName(devicep.getName());
                     device_person.add(t);
             }
             group.setT(device_person);
             group.setUsed_count(device_person.size());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", pageFence.getFenceGroups().size());
            jsonObject.put("data", pageFence.getFenceGroups());

            return jsonObject;
        } catch (Exception e) {
            myPrintln("yichang="+e.getMessage());
            return null;
        }
    }

    //根据围栏组获取相关的使用的设备
    @RequestMapping(value = "userApi/Fence_group_device/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllFence_group_device(HttpServletRequest request) {
        String f_g_id = request.getParameter("f_g_id");
        String page = request.getParameter("page");
        String limit = request.getParameter("limit");
        if (page == null || page.isEmpty()) {
            page = "1";
        }
        if (limit == null || limit.isEmpty()) {
            limit = "20";
        }
        Customer customer = getCustomer(request);
        Group_Sql group_sql = new Group_Sql();
        //设备组使用围栏组信息
        List<Group> groups=   group_sql.getFromFenceGroup(groupMapper,customer.getProject_key(), Integer.parseInt(f_g_id));
        List<Integer>   ids=new ArrayList<>();
        for(Group group: groups){
            ids.add(group.getId());
        }
        DeviceP_Sql deviceP_Sql=new DeviceP_Sql();
        PageDeviceP pageDeviceP= deviceP_Sql.getDeviceByGroupIdOrF_G_id(customer.getProject_key(),devicePMapper,ids,f_g_id,page,limit);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data",  pageDeviceP.getDeviceList());
        return  jsonObject;
    }
    @RequestMapping(value = "userApi/Fence_group/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllFence_group1(HttpServletRequest request) {

        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Fence_Group_Sql fence_sql = new Fence_Group_Sql();
        List<Fence_group> fences=fence_sql.getAll(fenceGroupMapper,customer.getProject_key());
        Fence_group fence=new Fence_group();
        if(lang!=null&&lang.equals("en")){
            fence.setName("UnBind");
        }else {
            fence.setName("不绑定围栏组");
        }
        fence.setId(-1);
        fences.add(0,fence);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", fences.size());
        jsonObject.put("data",  fences);
        return jsonObject;
    }
    @RequestMapping(value = "/userApi/Fence_group/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteFence_group(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        Fence_Group_Sql fence_sql = new Fence_Group_Sql();
        List<Integer> id=new ArrayList<Integer>();

        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Group_Sql group_sql=new Group_Sql();
        for(Object ids:jsonArray){
            List<Devicep> deviceps= deviceP_sql.getDeviceByFenceGroupID(devicePMapper,Integer.parseInt(ids.toString()));
            if(deviceps!=null&& !deviceps.isEmpty()){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }
            List<Group>  groups=   group_sql.getFromFenceGroup(groupMapper,user.getProject_key(),Integer.parseInt(ids.toString()));
            if(groups!=null&& !groups.isEmpty()){
                return JsonConfig.getJsonObj(CODE_10,null,lang);
            }
            id.add(Integer.parseInt(ids.toString()));
        }



        if(!id.isEmpty()){
            int status = fence_sql.deletes(fenceGroupMapper, id);
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
