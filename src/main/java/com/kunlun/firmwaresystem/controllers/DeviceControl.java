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
import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_Station;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class DeviceControl {
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private DevicePMapper devicePMapper;

    //获取资产分页
    @RequestMapping(value = "userApi/Devicep/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject addDeviceP(HttpServletRequest request ,@RequestBody JSONObject jsonObject) {

        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Devicep devicep = new Gson().fromJson(jsonObject.toString(), new TypeToken<Devicep>() {
        }.getType());
        devicep.setCreatetime(System.currentTimeMillis()/1000);
        devicep.setUserkey(customer.getUserkey());
        devicep.setProject_key(customer.getProject_key());
        devicep.setCustomer_key(customer.getCustomerkey());
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        deviceP_sql.addDeviceP(devicePMapper,devicep);
        devicePMap.put(devicep.getSn(),devicep);
        jsonObject=getJsonObj(CODE_OK,"",lang);
        return jsonObject;
    }









    //获取资产分页
    @RequestMapping(value = "userApi/Devicep/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getDeviceP(HttpServletRequest request) {
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
        Customer customer = getCustomer(request);
        DeviceP_Sql  deviceP_sql=new DeviceP_Sql();
        PageDeviceP pageDeviceP=deviceP_sql.selectPageDeviceP(devicePMapper,Integer.valueOf(page),Integer.valueOf(limit),quickSearch,customer.getUserkey(),customer.getProject_key());

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
            for (Map.Entry entry : devicePMap.entrySet()) {
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
                        System.out.println("异常--="+e);
                    }
                    }
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);
            jsonObject.put("offlineDevicep", offlineDeviceps);
            jsonObject.put("onlineDevicep", onlineDeviceps);
            return jsonObject;
        }catch (Exception e){
            System.out.println("异常="+e);
            return null;
        }
    }
    @RequestMapping(value = "/userApi/Devicep/getByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject selectDevicepByMap(HttpServletRequest request){
        String map_key=request.getParameter("map_key");
        System.out.println("地图kep="+map_key);
        List<Devicep> devicepList=new ArrayList<>();
        for(String sn:devicePMap.keySet()){
            Devicep devicep=devicePMap.get(sn);
            System.out.println("每个资产的地图key="+devicep.getMap_key());
            if(devicep!=null&&devicep.getMap_key()!=null&&devicep.getMap_key().equals(map_key)){
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
        System.out.println(json.toString());
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Devicep devicep=null;
      List<Devicep> deviceps=  deviceP_sql.getDevicePBySn(devicePMapper,json.getString("sn"));
      if(deviceps==null||deviceps.size()!=1){
          return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
      }else{
          devicep=deviceps.get(0);
          devicep.setOpen_run(json.getInteger("open_run"));
          if(json.getInteger("area_id")!=-1){
              devicep.setArea_id(json.getInteger("area_id"));
              devicep.setIs_area(1);
          }else{
              devicep.setArea_id(-1);
              devicep.setIs_area(0);
          }
      }
        if(!json.getString("idcard").equals("-1")){
            String idcard=json.getString("idcard");
            if(devicep.getIdcard()!=null&&idcard.equals(devicep.getIdcard())){
                //没有变更绑定的人
                System.out.println("没有变更绑定的人");
            }else{
                Person_Sql person_sql=new Person_Sql();
                List<Person> personList=person_sql.getPersonByIdCard(personMapper,idcard,customer.getUserkey(),customer.getProject_key());
                if(personList==null||personList.size()!=1){
                    return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
                }else{
                    Person person=personList.get(0);
                    devicep.setPerson_name(person.getName());
                    devicep.setIdcard(person.getIdcard());
                }
            }
        }else{
            System.out.println("解除绑定的人");
            devicep.setPerson_name("");
            devicep.setIdcard("");
        }
        Tag_Sql tag_sql =new Tag_Sql();
        String mac=json.getString("mac");
        if(devicep.getIsbind()==1&&devicep.getBind_mac()!=null){
            String old_mac=devicep.getBind_mac();
            //mac不变，说明没有变更绑定。不需要操作
            if(old_mac.equals(mac)){
                deviceP_sql.update(devicePMapper,devicep);
              //  return JsonConfig.getJsonObj(CODE_OK,null);
            }
        }
        if((mac.equals("不绑定信标")||mac.equals("UnBind"))&&devicep.getIsbind()==0){
            deviceP_sql.update(devicePMapper,devicep);
           // return JsonConfig.getJsonObj(CODE_OK,null);
        }

            //原有的绑定
            if(devicep.getIsbind()==1){
                System.out.println(devicep.getBind_mac());
               List<Tag> tagList1 = tag_sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),devicep.getBind_mac());
                if(tagList1 ==null|| tagList1.size()!=1){
                    System.out.println(tagList1.size());
                    return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
                }else{
                    Tag tag1 = tagList1.get(0);
                    tag1.setIsbind(0);
                    tag1.setBind_type(0);
                    tag_sql.update(tagMapper, tag1);
                }
            }
            if(!mac.equals("不绑定标签")&&!mac.equals("UnBind")){
                System.out.println("mac="+mac);
                    List<Tag> tagList = tag_sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),mac);
                if(tagList ==null|| tagList.size()!=1){
                    System.out.println("beaconList="+ tagList.size());
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
                    devicep.setIsbind(1);
                    deviceP_sql.update(devicePMapper,devicep);
                }
             }else{
                    devicep.setBind_mac("");
                    devicep.setIsbind(0);
                    deviceP_sql.update(devicePMapper,devicep);
                }
        String idd = "";
            try {
                JSONArray jsonArray = json.getJSONArray("fence_id");
                System.out.println("jsonArray="+jsonArray);
                for (Object ids : jsonArray) {
                    if (ids != null && !ids.toString().isEmpty()) {
                        idd = idd +ids+ "_";
                    }
                }
                if (idd.length() > 0) {
                    devicep.setFence_id(idd);
                    deviceP_sql.update(devicePMapper, devicep);
                }
            }catch (Exception e){
                System.out.println("输出id="+idd);
            }
            System.out.println("围栏id="+idd);
            devicePMap.put(devicep.getSn(),devicep);
            beaconsMap= tag_sql.getAllTag(tagMapper);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);
        return jsonObject;
    }
    @RequestMapping(value = "userApi/unBindTag", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject unBindTag(HttpServletRequest request, @RequestParam("sn") @ParamsNotNull String sn) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Devicep devicep=null;
        List<Devicep> deviceps=  deviceP_sql.getDevicePBySn(devicePMapper,sn);
        if(deviceps==null||deviceps.size()!=1){
            return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
        }else{
            devicep=deviceps.get(0);
            devicep.setOpen_run(0);
            Tag_Sql tag_sql =new Tag_Sql();
            if(devicep.getIsbind()==1){
                List<Tag> tagList1 = tag_sql.getTagByMac(tagMapper,customer.getUserkey(),customer.getProject_key(),devicep.getBind_mac());
                if(tagList1 ==null|| tagList1.size()!=1){
                    return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
                }else{
                    Tag tag1 = tagList1.get(0);
                    tag1.unbind();

                    tag_sql.update(tagMapper, tag1);
                    Tag tag =beaconsMap.get(tag1.getMac());

                }
            }
            devicep.setIsbind(0);
            devicep.setBind_mac("");
            devicep.setIdcard("");
            devicep.setPerson_name("");
            deviceP_sql.update(devicePMapper,devicep);
            devicePMap.put(devicep.getSn(),devicep);
            return getJsonObj(CODE_OK,null,lang);
        }
    }



    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
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
