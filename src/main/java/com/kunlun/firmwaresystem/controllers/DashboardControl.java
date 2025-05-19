package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONObject;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.check.DevStatus;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.sql.Alarm_Sql;
import com.kunlun.firmwaresystem.sql.Map_Sql;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class DashboardControl {
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private StationMapper StationMapper;
/*
    @RequestMapping(value = "userApi/AOA/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllBeacon(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        Beacon_Sql beacon_sql=new Beacon_Sql();
        String quickSearch=request.getParameter("quickSearch");
        String pages=request.getParameter("page");
        String limits=request.getParameter("limit");
        int page=1;
        int limit=10;
        if (!StringUtils.isBlank(pages)) {
            page=Integer.parseInt(pages);
        }
        if (!StringUtils.isBlank(limits)) {
            limit=Integer.parseInt(limits);
        }
        if (StringUtils.isBlank(quickSearch)) {
            quickSearch="";
        }
        PageBeacon pageBeacon=beacon_sql.selectPageBeacon_AOA(beaconMapper,page,limit,quickSearch,customer.getUserkey(),customer.getProject_key());
        if(pageBeacon.getBeaconList().size()>0){
            for(Beacon beacon:pageBeacon.getBeaconList()){
                Beacon beacon1=beaconsMap.get(beacon.getMac());
                beacon.setMap_key(beacon1.getMap_key());
                if(beacon1.getOnline()==0){
                    beacon.setSos(-1);
                    beacon.setRun(-1);
                    beacon.setBt(0);
                }else{
                    beacon.setSos(beacon1.getSos());
                    beacon.setRun(beacon1.getRun());
                    beacon.setBt(beacon1.getBt());
                }
                beacon.setLastTime(beacon1.getLastTime());
                beacon.setOnline(beacon1.getOnline());
                if(beacon.getIsbind()==1&&beacon.getBind_type()==1){
                    if(beacon.getDevice_sn()!=null){
                        myPrintln(beacon.getDevice_sn());
                        Devicep devicep=devicePMap.get(beacon.getDevice_sn());
                        if(devicep!=null){
                            beacon.setDevice_name(devicep.getName());
                        }
                    }
                }
                if(beacon.getIsbind()==1&&beacon.getBind_type()==2){
                    if(beacon.getDevice_sn()!=null){
                        myPrintln(beacon.getDevice_sn());
                        Person person=personMap.get(beacon.getDevice_sn());
                        if(person!=null){
                            beacon.setDevice_name(person.getName());
                        }

                    }
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageBeacon.getTotal());
        jsonObject.put("data", pageBeacon.getBeaconList());
         return jsonObject;
    }*/
@RequestMapping(value = "userApi/getAssetState", method = RequestMethod.GET, produces = "application/json")
public JSONObject getAssetState(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DevStatus devStatus=(DevStatus) redisUtil.get(redis_key_device_project+customer.getProject_key());
        if(devStatus!=null){
            myPrintln(devStatus.toString());
        }
        return   getJsonObj(CODE_OK,devStatus,lang);
    }

    @RequestMapping(value = "userApi/getBeaconState", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getBeaconState(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DevStatus devStatus=(DevStatus) redisUtil.get(redis_key_beacon_project+customer.getProject_key());
        if(devStatus!=null){
            myPrintln(devStatus.toString());
        }
        return   getJsonObj(CODE_OK,devStatus,lang);
    }
    @RequestMapping(value = "userApi/getPersonState", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getPersonState(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DevStatus devStatus=(DevStatus) redisUtil.get(redis_key_person_project+customer.getProject_key());
        if(devStatus!=null){
         //   myPrintln(devStatus.toString());
        }
        return   getJsonObj(CODE_OK,devStatus,lang);
    }
    @RequestMapping(value = "userApi/getLocatorState", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getLocatorState(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DevStatus devStatus=(DevStatus) redisUtil.get(redis_key_locator_project+customer.getProject_key());
        if(devStatus!=null){
          //  myPrintln(devStatus.toString());
        }
        return   getJsonObj(CODE_OK,devStatus,lang);
    }

    //获取一天的SOS状态
    @RequestMapping(value = "userApi/getSosOnDay", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getSosOnDay(HttpServletRequest request) {
        Customer customer = getCustomer(request);

        long time = System.currentTimeMillis() / 1000;
        long one_time = time - 86340;
        Alarm_Sql alarm_sql = new Alarm_Sql();
            List<Alarm> alarms = alarm_sql.selectByOneDay(alarmMapper, customer.getProject_key(), one_time);
            int all = alarms.size();
            int key_sum = 0;
            int run_sum = 0;
            int fence_on_sum = 0;
            int fence_out_sum = 0;
            int offline_sum = 0;
            int online_sum = 0;
            int bt_sum = 0;
            if (all > 0) {
                for (Alarm alarm : alarms) {
                    switch (alarm.getAlarm_type()) {
                        case sos_key:
                            key_sum++;
                            break;
                        case sos_run:
                            run_sum++;
                            break;
                        case sos_offline:
                            offline_sum++;
                            break;
                        case sos_online:
                            online_sum++;
                            break;
                        case fence_on_sos:
                            fence_on_sum++;
                            break;
                        case fence_out_sos:
                            fence_out_sum++;
                            break;
                        case sos_bt:
                            bt_sum++;
                            break;
                    }
                }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        JSONObject jsonObject_data = new JSONObject();
        jsonObject_data.put("key_sum",key_sum);
        jsonObject_data.put("run_sum",run_sum);
        jsonObject_data.put("offline_sum",offline_sum);
        jsonObject_data.put("fence_on_sum",fence_on_sum);
        jsonObject_data.put("fence_out_sum",fence_out_sum);
        jsonObject_data.put("bt_sum",bt_sum);
        jsonObject.put("data", jsonObject_data);
        return jsonObject;

    }
    //获取一天的SOS状态
    @RequestMapping(value = "userApi/getSosDatail", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getSosDatail(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        long time = System.currentTimeMillis() / 1000;
        long one_time = time - 86340;
        Alarm_Sql alarm_sql = new Alarm_Sql();
        List<Alarm> alarms = alarm_sql.selectByOneDay(alarmMapper, customer.getProject_key(), one_time);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");

            jsonObject.put("data",alarms);
            return jsonObject;

    }

    class T{
       private int on;
        private int off;
        private String name;

        public int getOn() {
            return on;
        }

        public void setOn(int on) {
            this.on = on;
        }

        public int getOff() {
            return off;
        }

        public void setOff(int off) {
            this.off = off;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "T{" +
                    "on=" + on +
                    ", off=" + off +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    @RequestMapping(value = "userApi/getAssetByAllMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAssetByAllMap(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        Map_Sql map_sql=new Map_Sql();
        List<Map> mapList=map_sql.getAllMap(mapMapper,customer.getUserkey(),customer.getProject_key());
        List<T> map_device=new ArrayList();
        for(Map map:mapList){
            String map_key=map.getMap_key();
            int[] state=new int[2];
            for(String sn:devicePMap.keySet()){
                Devicep devicep=devicePMap.get(sn);
                if(devicep!=null&&devicep.getMap_key()!=null&&devicep.getMap_key().equals(map_key)){
                   if(devicep.getOnline()==1){
                      // myPrintln(devicep);
                       state[0]++;
                   }

                }
            }
            for(String idcard:personMap.keySet()){
                Person person=personMap.get(idcard);
                if(person!=null&&person.getMap_key()!=null&&person.getMap_key().equals(map_key)){
                    if(person.getOnline()==1){
                        // myPrintln(devicep);
                        state[1]++;
                    }
                }
            }
            T t=new T();
            t.setName(map.getName());
            t.setOff(state[1]);
            t.setOn(state[0]);
         //   myPrintln(t.toString());
            map_device.add(t);

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("data", map_device);
    //    myPrintln(jsonObject.toString());
        return  jsonObject;
    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }


}
