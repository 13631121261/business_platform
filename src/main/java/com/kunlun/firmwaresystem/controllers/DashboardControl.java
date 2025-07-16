package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONObject;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.check.DevStatus;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.CompanyMapper;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.mappers.StationTypeMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired
    private StationTypeMapper stationTypeMapper;
    @Autowired
    private CompanyMapper companyMapper;

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
        for (Alarm alarm : alarms) {
            Station station=(Station) redisUtil.get(redis_key_locator+alarm.getStation_address());
            if (station!=null){
                alarm.setStation_type(station.getType_name());
            }
            Person person=personMap.get(alarm.getSn());
            if (person!=null){
                Company company=(Company) redisUtil.get(redis_key_company+person.getCompany_id());
                if (company!=null){
                    alarm.setCompany_name(company.getName());
                }
            }else {
                Devicep devicep=devicePMap.get(alarm.getSn());
                if (devicep!=null){
                    Company company=(Company) redisUtil.get(redis_key_company+devicep.getCompany_id());
                    if (company!=null){
                        alarm.setCompany_name(company.getName());
                    }

                }
            }

        }
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

    @RequestMapping(value = "userApi/getAllStatus", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getStatus(HttpServletRequest request) {
        Customer customer=getCustomer(request);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");

        StationType_Sql stationTypeSql=new StationType_Sql();
        List<StationType> stationTypeList= stationTypeSql.getAll(stationTypeMapper,customer.getProject_key());
        Station_sql station_sql=new Station_sql();
        HashMap<String,Station> stationMap=  station_sql.getAllStation(stationMapper,customer.getProject_key());
        List<Object> list_data=new ArrayList<>();

        Company_Sql companySql=new Company_Sql();
        List<Company> companyList=companySql.getAll(companyMapper,customer.getProject_key());
        for (Company company : companyList) {
            List<Object> list=new ArrayList<>();
            T1 t1=new T1();
            int person_online_count=0;
            int device_online_count=0;
            try {
                for (StationType stationType : stationTypeList) {
                    int person_count=0;
                    int device_count=0;
                    String  name="";
                    name = stationType.getName();
                    for (Person person : personMap.values()) {
                      //  myPrintln("人员的公司 ID" + person.getCompany_id());
                        if (company.getId() == person.getCompany_id()&&person.getOnline()==1) {
                           // myPrintln("公司相同 ID");
                            if (person.getStation_mac() != null && !person.getStation_mac().isEmpty()) {
                               // myPrintln("人员的基站 MAC=" + person.getStation_mac());
                                if (person.getStation_mac() == null || person.getStation_mac().isEmpty()) {
                                   // myPrintln("人员的基站 空值，继续循环");
                                    continue;
                                }
                                Station station = stationMap.get(person.getStation_mac());
                                myPrintln("人员的基站 名称=" + station.getName());
                                if (station.getType_id() == stationType.getId()) {
                                    myPrintln(" 符合条件=" + person.getName());
                                    person_count++;
                                    person_online_count++;
                                }
                            }
                        }
                    }
                    for (Devicep device : devicePMap.values()) {
                        if (company.getId() == device.getCompany_id()&&device.getOnline()==1) {
                            if (device.getNear_s_address() != null && !device.getNear_s_address().isEmpty()) {
                                Station station = stationMap.get(device.getNear_s_address());
                                if (station != null && station.getType_id() == stationType.getId()) {
                                    device_count++;
                                    device_online_count++;
                                }
                            }
                        }
                    }
                    T1 t = new T1();
                    t.setPerson_count(person_count);
                    t.setDevice_count(device_count);
                    t.setName(name);
                    list.add(t);

                }
                t1.setDetail(list);
            }catch (Exception e){
                myPrintln("异常="+e.getMessage());
            }
            t1.setName(company.getName());
            t1.setDevice_count(device_online_count);
            t1.setPerson_count(person_online_count);

            list_data.add(t1);
        }
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("data", list_data);
        //    myPrintln(jsonObject.toString());
        return  jsonObject;
    }
class T1{
    private int person_count=0,device_count=0;
    private String name="";
    private List<Object> detail=null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDevice_count() {
        return device_count;
    }

    public void setDevice_count(int device_count) {
        this.device_count = device_count;
    }

    public int getPerson_count() {
        return person_count;
    }

    public void setPerson_count(int person_count) {
        this.person_count = person_count;
    }

    public void setDetail(List<Object> detail) {
        this.detail = detail;
    }

    public List<Object> getDetail() {
        return detail;
    }
}

    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }


}
