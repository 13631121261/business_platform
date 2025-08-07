package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.*;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.*;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.*;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;

import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_company;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class DeviceControl {
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private DevicePMapper devicePMapper;

    @Resource
    private TagfMapper tagfMapper;

    @Resource
    private GroupMapper groupMapper;
    @Resource
    private FenceMapper fenceMapper;
    @Resource
    private FenceGroupMapper fenceGroupMapper;

    //获取资产分页
    @RequestMapping(value = "userApi/Devicep/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject addDeviceP(HttpServletRequest request ,@RequestBody JSONObject jsonObject) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln(jsonObject.toString());
        Devicep devicep = new Gson().fromJson(jsonObject.toString(), new TypeToken<Devicep>() {
        }.getType());
        devicep.setCreatetime(System.currentTimeMillis()/1000);
        devicep.setUpdate_time(System.currentTimeMillis()/1000);
        devicep.getTagfs_id();
        devicep.setUserkey(customer.getUserkey());
        devicep.setProject_key(customer.getProject_key());
        devicep.setCustomer_key(customer.getCustomerkey());
        myPrintln(devicep.toString());
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        try{
            boolean status=deviceP_sql.addDeviceP(devicePMapper,devicep);

            if (status) {
                jsonObject=getJsonObj(CODE_OK,"",lang);
            }else
            {
                jsonObject=getJsonObj(CODE_REPEAT,"",lang);
            }
        }catch (Exception e){
            myPrintln("有点异常"+e);
        }

       // devicePMap.put(devicep.getSn(),devicep);

        return jsonObject;
    }





    @RequestMapping(value = "userApi/Devicep/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject delete(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        Customer customer = getCustomer(request);
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&& !ids.toString().isEmpty()){
                id.add(Integer.parseInt(ids.toString()));
                Devicep devicep=devicePMapper.selectById(Integer.parseInt(ids.toString()));
                myPrintln(devicep.toString());
                //如果设备有绑定信标，实现解绑操作
                if(devicep.getBind_mac()!=null&& !devicep.getBind_mac().isEmpty()){
                    String bind_mac=devicep.getBind_mac();
                    Tag_Sql tag_Sql=new Tag_Sql();
                    List<Tag> tags= tag_Sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),bind_mac);
                    //如果MAC 相同，有多个标签，那绝对是错误的
                    if(tags==null||tags.size()>1){
                        return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",customer.getLang());
                    }
                    else{
                        //信标解绑
                        Tag tag=tags.get(0);
                        tag.setBind_key("");
                        tag.setBind_type(0);
                        tag.setIsbind(0);
                        Tag t=tagsMap.get(tag.getMac());
                        if (t!=null){
                            t.setBind_key("");
                            t.setBind_type(0);
                            t.setIsbind(0);
                        }
                        tagMapper.updateById(tag);
                    }

                }
            }
        }
        int status=devicePMapper.deleteBatchIds(id);
        if (status>=0) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("code", CODE_OK);
            jsonObject1.put("msg", CODE_OK_txt);
            return jsonObject1;
        }
      else{
       return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",customer.getLang());
        }

    }

    @RequestMapping(value = "userApi/Devicep/edit", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getEdit(HttpServletRequest request) {
        String ids= request.getParameter("id");
        int id=Integer.parseInt(ids);
        Devicep devicep=devicePMapper.selectById(id);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("code", CODE_OK);
        jsonObject1.put("msg", CODE_OK_txt);
        jsonObject1.put("data", devicep);
        return jsonObject1;
    }


    //获取资产分页
    @RequestMapping(value = "userApi/Devicep/edit", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject editDeviceP(HttpServletRequest request ,@RequestBody JSONObject jsonObject) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln(jsonObject.toString());
        Devicep devicep = new Gson().fromJson(jsonObject.toString(), new TypeToken<Devicep>() {
        }.getType());
        devicep.setTagf_id("");
        devicep.setUpdate_time(System.currentTimeMillis()/1000);
        devicep.getTagfs_id();
        devicep.setUserkey(customer.getUserkey());
        devicep.setProject_key(customer.getProject_key());
        devicep.setCustomer_key(customer.getCustomerkey());
        myPrintln(devicep.toString());
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        try{
            boolean status=deviceP_sql.update(devicePMapper,devicep);
            if (status) {
                devicePMap.put(devicep.getSn(),devicep);
                jsonObject=getJsonObj(CODE_OK,"",lang);
            }else
            {
                jsonObject=getJsonObj(CODE_REPEAT,"",lang);
            }
        }catch (Exception e){
            myPrintln("有点异常"+e);
        }

        // devicePMap.put(devicep.getSn(),devicep);

        return jsonObject;
    }

    //获取资产分页
    @RequestMapping(value = "userApi/Devicep/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getDeviceP(HttpServletRequest request) {
        String quickSearch=request.getParameter("quickSearch");
        String page=request.getParameter("page");
        String limit=request.getParameter("limit");
        String sort=request.getParameter("order");
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
        PageDeviceP pageDeviceP=deviceP_sql.selectPageDeviceP(devicePMapper,Integer.parseInt(page),Integer.parseInt(limit),quickSearch,customer.getUserkey(),customer.getProject_key(),sort);
        for(Devicep devicep:pageDeviceP.getDeviceList()){


            String[] ids=devicep.getTagfs_id();
            if (ids!=null&&ids.length>0) {
                ArrayList<Integer> ids1 = new ArrayList<>();
                for (String id : ids) {
                    ids1.add(Integer.parseInt(id));
                }
               List<Tagf> tagfs= tagfMapper.selectBatchIds(ids1);
                devicep.setTagfs(tagfs);
            }

            int group_id=devicep.getGroup_id();
            if (group_id>0) {
                Group group=groupMapper.selectById(group_id);
                if(group!=null){
                    devicep.setGroup_name(group.getGroup_name());
                }
            }
            Company company=(Company) redisUtil.get(redis_key_company+ devicep.getCompany_id());
            if (company!=null) {
                devicep.setCompany_name(company.getName());
            }

            int f_id=devicep.getFence_id();
            int f_g_id=devicep.getFence_group_id();
            if (f_id!=0&&f_id!=-1) {
              Fence fence=  fenceMapper.selectById(f_id);
              if(fence!=null){
                devicep.setF_name(fence.getName());
              }
            }
            else if(f_g_id!=0&&f_g_id!=-1) {
                Fence_group group=  fenceGroupMapper.selectById(f_g_id);
                if(group!=null){
                    devicep.setF_g_name(group.getName());
                }
            }
           Devicep d= devicePMap.get(devicep.getSn());
            if (d!=null) {
                devicep.setOnline(d.getOnline());
                devicep.setBt(d.getBt());
                devicep.setRun(d.getRun());
                devicep.setX(d.getX());
                devicep.setY(d.getY());
                devicep.setSos(d.getSos());
                devicep.setLasttime(d.getLasttime());
                devicep.setMap_key(d.getMap_key());
                devicep.setMap_name(d.getMap_name());
                devicep.setNear_s_name(d.getNear_s_name());
                devicep.setNear_s_address(d.getNear_s_address());
                devicep.setFirst_time(d.getFirst_time());

            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data", pageDeviceP.getDeviceList());
        return jsonObject;
    }


    @RequestMapping(value = "userApi/getDevicebyMap", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getDevicebyMap(HttpServletRequest request,@RequestParam("map_key") @ParamsNotNull String map_key) {
        try {
            Customer customer = getCustomer(request);
            DeviceP_Sql deviceP_sql = new DeviceP_Sql();
            List<Devicep> onlineDeviceps = new ArrayList<>();
            List<Devicep> offlineDeviceps = new ArrayList<>();
            int online = 0;
            int offline = 0;
            /*for (Map.Entry entry : devicePMap.entrySet()) {
                Devicep devicep = (Devicep) entry.getValue();
                if (devicep.getProject_key().equals(customer.getProject_key())) {
                    Station Station = (Station) redisUtil.get(redis_key_Station + devicep.getStation_mac());
                    if (Station != null && Station.getMap_key().equals(map_key)) {
                    try {
                        if (devicep.getOnline() == 1) {
                            online++;
                            onlineDeviceps.add(devicep);
                        } else {
                            offlineDeviceps.add(devicep);
                            offline++;
                        }
                    }catch (Exception e){
                        myPrintln("异常--="+e);
                    }
                    }
                }
            }*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);
            jsonObject.put("offlineDevicep", offlineDeviceps);
            jsonObject.put("onlineDevicep", onlineDeviceps);
            return jsonObject;
        }catch (Exception e){
            myPrintln("异常="+e);
            return null;
        }
    }
    @RequestMapping(value = "/userApi/Devicep/getByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject selectDevicepByMap(HttpServletRequest request){
        String map_key=request.getParameter("map_key");
        myPrintln("地图kep="+map_key);
        List<Devicep> devicepList=new ArrayList<>();
        for(String key:devicePMap.keySet()){
            Devicep devicep=devicePMap.get(key);
            if (devicep.getMap_key()!=null&&devicep.getMap_key().equals(map_key)) {
                Company company=(Company) redisUtil.get(redis_key_company+ devicep.getCompany_id());
                if (company!=null) {
                    devicep.setCompany_name(company.getName());
                }
                devicepList.add(devicep);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", devicepList.size());
        jsonObject.put("data", devicepList);
        return jsonObject;
    }
    @RequestMapping(value = "userApi/bindTag", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject bindTag(HttpServletRequest request,@RequestBody JSONObject json) {
        myPrintln(json.toString());
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Devicep devicep=null;
      List<Devicep> deviceps=  deviceP_sql.getDevicePBySn(devicePMapper,json.getString("sn"));
      if(deviceps==null||deviceps.size()!=1){
          return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
      }else{
          devicep=deviceps.get(0);
          myPrintln(devicep.toString());
      }

        Tag_Sql tag_sql =new Tag_Sql();
        String mac=json.getString("mac");
        String f_g_ids=json.getString("id");
        int f_g_id=-1;
        if (f_g_ids!=null&&f_g_ids.length()>0) {
             f_g_id=Integer.parseInt(f_g_ids);
        }
        String fence_type=json.getString("fence_type");
        myPrintln("mac="+mac);
        //设备原有就绑定某信标
        try {
            if (devicep.getBind_mac()!=null&&!devicep.getBind_mac().isEmpty()) {
                String old_mac = devicep.getBind_mac();
                //mac不变，说明没有变更绑定。不需要操作
                if (old_mac.equals(mac)) {
                    bind_fence(fence_type,f_g_id,devicep);
                    tagsMap=tag_sql.getAllTag(tagMapper);
                    devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);

                    return JsonConfig.getJsonObj(CODE_OK, "", customer.getLang());
                }
            }
            myPrintln("111mac="+mac);
            //原来没有绑定，现在也不需要绑定
            if ((mac.equals("不绑定标签") || mac.equals("Unbound")) &&devicep.getBind_mac()!=null&& devicep.getBind_mac().isEmpty()) {
                //deviceP_sql.update(devicePMapper,devicep);
                bind_fence(fence_type,f_g_id,devicep);
                tagsMap=tag_sql.getAllTag(tagMapper);
                devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
                return JsonConfig.getJsonObj(CODE_OK, "", customer.getLang());
            }
        }catch (Exception e){
            myPrintln("异常="+e);
        }
        myPrintln("mac="+mac);
            //原有的绑定
            if(devicep.getBind_mac()!=null&&!devicep.getBind_mac().isEmpty()){
                myPrintln(devicep.getBind_mac());
               List<Tag> tagList1 = tag_sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),devicep.getBind_mac());
                if(tagList1 ==null|| tagList1.size()!=1){
                    return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
                }else{
                    Tag tag1 = tagList1.get(0);
                    tag1.setIsbind(0);
                    tag1.setBind_key("");
                    tag1.setBind_type(0);
                    tag_sql.update(tagMapper, tag1);
                    tagsMap=tag_sql.getAllTag(tagMapper);
                    devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
                }
            }
            if(!mac.equals("不绑定标签")&&!mac.equals("Unbound")){
                myPrintln("mac="+mac);
                try{
                    List<Tag> tagList = tag_sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),mac);
                if(tagList ==null|| tagList.size()!=1){
                    myPrintln("beaconList="+ tagList.size());
                    return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);

                }else{
                    //更新信标绑定的资产
                    Tag tag = tagList.get(0);
                    tag.setBind_key(json.getString("sn"));
                    tag.setIsbind(1);
                    tag.setBind_type(1);
                    tag_sql.update(tagMapper, tag);
                    //更新资产
                    devicep.setBind_mac(tag.getMac());
                    deviceP_sql.update(devicePMapper,devicep);
                    tagsMap=tag_sql.getAllTag(tagMapper);
                    devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
                }
                } catch (Exception e) {
                    myPrintln("5456"+e.getMessage());;
                }
             }else{
                    devicep.setBind_mac("");

                    deviceP_sql.update(devicePMapper,devicep);
                    tagsMap=tag_sql.getAllTag(tagMapper);
                    devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
                }

            //处理单个围栏
             bind_fence(fence_type,f_g_id,devicep);
            tagsMap=tag_sql.getAllTag(tagMapper);
            devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);
        return jsonObject;
    }

    private void bind_fence(String fence_type,int f_g_id,Devicep devicep){
        if (fence_type!=null&&!fence_type.isEmpty()) {
            if (fence_type.equals("1")){
               // if (f_g_id!=-1){
                devicep.setFence_group_id(-1);
                devicep.setFence_id(f_g_id);
             //   }
            }
            else if (fence_type.equals("2")){
                devicep.setFence_group_id(f_g_id);
                devicep.setFence_id(-1);
            }
        }
        devicePMapper.updateById(devicep);


    }

    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }

    class DeviceTree{
        int id;
        String label;
        List<DeviceTree> children;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<DeviceTree> getChildren() {
            return children;
        }

        public void setChildren(List<DeviceTree> children) {
            this.children = children;
        }
        public void addChildren(DeviceTree deviceTree){
            if(deviceTree==null){
                return;
            }
            if(children!=null){
                children.add(deviceTree);
            }
            else{
                children=new ArrayList<>();
                children.add(deviceTree);
            }
        }
    }

}
