package com.kunlun.firmwaresystem.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.StringUtil;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.DataFormatException;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;

import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
import static com.kunlun.firmwaresystem.util.StringUtil.*;

public class CallBackHandlers implements Runnable {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    long times=System.currentTimeMillis()/1000;
    long time;
    String topic;
    MqttMessage message;

    Gson gson = new Gson();
    History_Sql history_sql=new History_Sql();
    Station Station = null;
    static int  count =0;
    public CallBackHandlers(String topic, MqttMessage message) {
        this.message = message;
        this.topic = topic;
    }

    @Override
    public void run() {
        // subscribe后得到的消息会执行到这里面

        if(topic.equals("location_engine")){
           // System.out.println("log=订阅收到消息="+new String(message.getPayload()));
            String data = new String(message.getPayload());

            //   println("接收消息Qos:" + data);
            time = System.currentTimeMillis()/1000;// new Date()为获取当前系统时间
            if (!data.contains("pkt_type")){
                return;
            }
            JSONObject jsonObject = null;
            String pkt_type = null;
            String StationAddress = null;
            try {
                Station station = new Gson().fromJson(jsonObject.toString(), new TypeToken<Station>() {
                }.getType());


                //  println("吃吃吃"+data);

                if (redisUtil == null) {
                    System.out.println("redis 空引用");
                    return;
                }
                if (StationMapper == null) {
                    System.out.println("StationMapper 空引用");
                    return;
                }
                if (StationMap == null) {
                    System.out.println("StationMap 空引用");
                    return;
                }
                if (StationMap.get(StationAddress) != null) {

                    Station = (Station) redisUtil.get(redis_key_Station + StationAddress);

                    if (Station == null) {
                        Station_sql Station_sql = new Station_sql();
                        Station = Station_sql.getStationByMac(StationMapper, StationAddress);
                        redisUtil.set(redis_key_Station + StationAddress, Station);
                    }
                }


            }
            catch (Exception e ){

            }
            redisUtil.get(redis_key_Station+Station.getAddress());
        }
        if (topic.equals("/cle/mqtt666——————目前不被使用")) {
            try {
                count++;
                System.out.println("计数=" + count + "        " + message.getPayload());
                String map_key = "";
                String jsonstr = StringUtil.unzip(message.getPayload());
                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
                System.out.println(jsonObject);

                if (jsonObject.getString("type").equals("sensors")) {
                    //标签定位
                    JSONObject beacons = jsonObject.getJSONObject("data");
                    Set<String> macs = beacons.keySet();
                    ArrayList<Object> deviceps = new ArrayList<>();
                    ArrayList<Object> tags = new ArrayList<>();
                    // System.out.println("步骤1");
                    for (String key : macs) {
                        // System.out.println(key);
                        if (beaconsMap.get(key) != null) {
                            Locator locator = null;
                            Tag tag = beaconsMap.get(key);
                            if (tag == null) {
                                return;
                            }
                            //    System.out.println("信标" + beacon);
                            if (tag != null) {
                                JSONObject a = beacons.getJSONObject(key);
                                //  beacon.setX();

                                tag.setX(Double.parseDouble(decimalFormat.format(a.getDouble("x"))));
                                tag.setY(Double.parseDouble(decimalFormat.format(a.getDouble("y"))));
                                //  System.out.println("初始化Y="+beacon.getY());
                                tag.setLastTime(a.getLong("updatedAt") / 1000);
                                tag.setRssi(a.getIntValue("rssi"));
                                tag.setStation_address(a.getString("nearestStation"));
                                locator = (Locator) redisUtil.get(redis_key_locator + tag.getStation_address());
                                //System.out.println("基站="+check+beacon.getStation_address());
                                tag.setOnline(1);
                                tag.setBt(a.getIntValue("rssi"));
                                if (a.getJSONObject("userData") != null) {
                                    JSONObject userData = a.getJSONObject("userData");
                                    if (userData.getJSONArray("9") != null) {
                                        JSONArray s9 = userData.getJSONArray("9");
                                        String str = Integer.toBinaryString((((int) s9.get(1)) & 0xFF) + 0x100).substring(1);
                                        char[] bit = str.toCharArray();
                                        if (bit[2] == '1') {
                                            tag.setSos(1);
                                            //   System.out.println("信标SOS1");
                                        } else {
                                            //  System.out.println("信标SOS/"+str);
                                            tag.setSos(0);
                                        }
                                        if (bit[4] == '1') {
                                            tag.setRun(1);
                                        } else {
                                            // System.out.println("信标SOS/"+str);
                                            tag.setRun(0);
                                        }

                                        double bt = ((double) ((int) s9.get(3)) * 1.0) / 255 * 6.6;
                                        DecimalFormat decimalFormat = new DecimalFormat("#.00");
                                        String bts = decimalFormat.format(bt);
                                        tag.setBt(Double.valueOf(bts));

                                        // System.out.println(beacon.toString());
                                    }
                                }
                            }
                            if (tag.getIsbind() == 1 && tag.getBind_key() != null) {
                                JSONObject a = beacons.getJSONObject(key);

                                //这里会有一个过期的缓存
                                Map map = (Map) redisUtil.get(redis_id_map + a.getString("mapId"));


                                if (map != null) {
                                    //  System.out.println(beacon.getY());
                                    //    System.out.println(map.getHeight());
                                    tag.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - Double.parseDouble(decimalFormat.format(a.getDouble("y"))))));
                                    //  System.out.println("PP"+beacon.getY());

                                    //  redisUtil.set(redis_id_map + a.getString("mapId"), map,0);
                                    tag.setMap_key(map.getMap_key());
                                    map_key = map.getMap_key();
                                    // System.out.println("地图=" + map_key);
                                } else {
                                    System.out.println("地图是空的");
                                    return;
                                }
                                //   System.out.println("步骤2");
                                if (tag.getBind_type() == 1) {
                                    Devicep devicep = devicePMap.get(tag.getBind_key());
                                    devicep.setX(tag.getX());
                                    devicep.setY(tag.getY());
                                    devicep.setLasttime(tag.getLastTime());
                                    devicep.setStation_mac(tag.getStation_address());
                                    devicep.setType("device");

                                    devicep.setSos(tag.getSos());
                                    devicep.setOnline(1);
                                    devicep.setRun(tag.getRun());
                                    devicep.setBt(tag.getBt());
                                    //   System.out.println("资产="+devicep);
                                    if (locator != null) {
                                        //   System.out.println("有信息"+devicep);
                                        devicep.setB_area_name(locator.getArea_name());
                                        devicep.setB_area_id(locator.getArea_id());
                                        devicep.setMap_name(locator.getMap_name());
                                        devicep.setStation_name(locator.getName());
                                        devicep.setMap_key(map_key);
                                        //  System.out.println(devicep.getB_area_name());
                                        if (map != null) {
                                            devicep.setMap_name(map.getName());
                                        }
                                    }
                                   /* if(devicep.getSn().equals("f0c892010002")){
                                        System.out.println("f0c892010002   Y="+devicep.getY());
                                        System.out.println("f0c892010002   X="+devicep.getX());
                                        System.out.println("----------------------------");
                                    }*/

                                    deviceps.add(devicep);
                                    History history = new History();
                                    history.setMap_key(devicep.getMap_key());
                                    history.setSn(devicep.getSn());
                                    history.setTime(System.currentTimeMillis());
                                    history.setType("device");
                                    history.setX(devicep.getX());
                                    history.setY(devicep.getY());
                                    history.setName(devicep.getName());
                                    history.setProject_key(devicep.getProject_key());
                                    history_sql.addHistory(historyMapper, history);
                                    //    System.out.println("步骤2" + key);
                                    String res = (String) redisUtil.get(device_check_online_status_res + tag.getBind_key());
                                    if (res == null || res.equals("0")) {
                                        Alarm_Sql alarm_sql = new Alarm_Sql();
                                        alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
                                    }
                                    redisUtil.setnoTimeOut(device_check_online_status_res + tag.getBind_key(), "1");
                                    handleSos_AOA(tag);
                                    handleFence(tag, map.getProportion());
                                    handleBt_AOA(tag);
                                    if (devicep.getOpen_run() == 1) {
                                        handleRun_AOA(tag);
                                    }

                                }
                                //System.out.println(beacons.getJSONObject(key));
                                else if (tag.getBind_type() == 2) {
                                    Person person = personMap.get(tag.getBind_key());
                                    // System.out.println("缩放" + map.getProportion());
                                    person.setX(tag.getX());
                                    person.setY(tag.getY());
                                    person.setOnline(1);
                                    //   person.setType("person");
                                    person.setLasttime(tag.getLastTime());
                                    if (locator != null) {
                                        // System.out.println(check);
                                        person.setB_area_name(locator.getArea_name());
                                        person.setB_area_id(locator.getArea_id());
                                        person.setMap_name(locator.getMap_name());
                                        person.setStation_mac(locator.getAddress());
                                        person.setStation_name(locator.getName());

                                        person.setMap_key(map_key);
                                        if (map != null) {
                                            person.setMap_name(map.getName());
                                        }

                                        // System.out.println(person);
                                    }
                                    deviceps.add(person);
                                    History history = new History();
                                    history.setMap_key(person.getMap_key());
                                    history.setSn(person.getIdcard());
                                    history.setTime(System.currentTimeMillis());
                                    history.setType("person");
                                    history.setX(person.getX());
                                    history.setY(person.getY());
                                    history.setProject_key(person.getProject_key());
                                    history.setName(person.getName());
                                    history_sql.addHistory(historyMapper, history);
                                    // System.out.println("数量"+deviceps.size()+deviceps);
                                    String res = (String) redisUtil.get(person_check_online_status_res + tag.getBind_key());
                                    // System.out.println("步骤2" + key);
                                    if (res == null || res.equals("0")) {
                                        Alarm_Sql alarm_sql = new Alarm_Sql();
                                        alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(), person.getLasttime()));
                                    }
                                    redisUtil.setnoTimeOut(person_check_online_status_res + tag.getBind_key(), "1");
                                    handleFence(tag, map.getProportion());
                                    handleSos_AOA(tag);
                                    handleBt_AOA(tag);

                                    //   System.out.println("步骤2");
                                }
                            }
                        }
                    }
                    if (deviceps.size() > 0) {

                        //System.out.println("需要推送的1");
                        sendTagPush(deviceps, map_key);
                        sendRelayPush(deviceps, map_key);
                    } else {
                        //    System.out.println("没有需要推送的"+deviceps);
                    }
                } else if (jsonObject.getString("type").equals("locators")) {
                    JSONObject locators = jsonObject.getJSONObject("data");
                    Set<String> ips = locators.keySet();

                    //  System.out.println("步骤1");
                    for (String ip : ips) {
                        // System.out.println("IP ="+ip);
                        JSONObject a = locators.getJSONObject(ip);
                        if (a != null) {
                            int tag = 0;
                            String address = a.getString("mac").replaceAll(":", "");
                            Locator locator = (Locator) redisUtil.get(redis_key_locator + address);
                            if (locator == null) {
                                System.out.println("新的AOA");
                                locator = new Locator();
                                locator.setAddress(address);
                                locator.setCreate_time(System.currentTimeMillis() / 1000);
                                tag = 1;
                            } else {
                                //  System.out.println("旧的AOA");
                            }
                            JSONObject info = a.getJSONObject("info");

                            locator.setX(Double.parseDouble(decimalFormat.format(info.getDouble("x"))));
                            locator.setY(Double.parseDouble(decimalFormat.format(info.getDouble("y"))));
                            locator.setZ(info.getDouble("z"));
                            // System.out.println(check);
                            locator.setMap_id(info.getString("mapId"));
                            Map map = (Map) redisUtil.get(redis_id_map + info.getString("mapId"));
                            if (map != null) {
                                locator.setMap_key(map.getMap_key());
                                locator.setUser_key(map.getUser_key());
                                locator.setProject_key(map.getProject_key());
                                locator.setMap_name(map.getName());
                                locator.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - locator.getY())));
                                locator.setProportion(map.getProportion());
                            } else {
                                //此基站未有绑定地图,不自动增加
                                continue;
                            }
                            locator.setName(info.getString("name"));
                            locator.setModel_name(info.getString("modelName"));
                            locator.setVersion(info.getString("version"));
                            locator.setIp(ip);
                            if (locator.getOnline() != 1 && a.getBoolean("online")) {
                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.locator, locator.getMap_key(), 0, "", 0, 0, "", locator.getName(), locator.getAddress(), locator.getProject_key(), locator.getLast_time()));
                            }
                            if (locator.getOnline() == 1 && !a.getBoolean("online")) {
                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_offline, Alarm_object.locator, locator.getMap_key(), 0, "", 0, 0, "", locator.getName(), locator.getAddress(), locator.getProject_key(), locator.getLast_time()));
                            }
                            locator.setOnline(a.getBoolean("online") ? 1 : 0);

                            locator.setLast_time(a.getLong("updatedAt") / 1000);
                            if (tag == 1) {
                                Locators_Sql locators_sql = new Locators_Sql();
                                locators_sql.addLocator(locatorMapper, locator);
                            }
                            redisUtil.setnoTimeOut(redis_key_locator + address, locator);
                        }
                    }
                }
            } catch (DataFormatException e) {
                System.out.println("AOA解析异常1" + e);
            } catch (IOException e) {
                System.out.println("AOA解析异常2" + e);
            }
            return;
        }
        // System.out.println("接收时间="+dfs.format(new Date()));
        String data = new String(message.getPayload());
        //   System.out.println("接收消息Qos:" + data);
        time = System.currentTimeMillis() / 1000;// new Date()为获取当前系统时间
        if (data.isEmpty() || !data.contains("pkt_type")) {
            return;
        }

        JSONObject jsonObject = null;
        String pkt_type = null;
        String StationAddress = null;
        Object object = null;

    }
    private void BeaconHandle(Tag tag) {

/*
                        Alarm_Sql alarm_sql = new Alarm_Sql();


                        alarm_sql.addAlarm(alarmMapper,new Alarm(Alarm_Type.sos_key,Alarm_object.device,beacon.getMap_key(),-1,"",beacon.getBt(),0,"",deviceP.getName(),deviceP.getSn(),deviceP.getProject_key(),deviceP.getLasttime()));


                    if(beacon.getOnline()!=1&&beacon.getIsbind()==1){
                        System.out.println("接收到设备 1");
                        if(beacon.getBind_type()==1){
                            System.out.println("接收到设备 2");

                            alarm_sql.addAlarm(alarmMapper,new Alarm(Alarm_Type.sos_online,Alarm_object.device,beacon.getMap_key(),-1,"",beacon.getBt(),0,"",deviceP.getName(),deviceP.getSn(),deviceP.getProject_key(),deviceP.getLasttime()));
                            System.out.println("接收到设备 3");
                        }
                        else{
                            Person  person=personMap.get(beacon.getBind_key());

                            alarm_sql.addAlarm(alarmMapper,new Alarm(Alarm_Type.sos_online,Alarm_object.person,beacon.getMap_key(),person.getFence_id(),"",beacon.getBt(),0,"",person.getName(),person.getIdcard(),person.getProject_key(),person.getLasttime()));

                        }
                    }


                    // deviceP.setSos(beacon.getSos());
                */


    }







    private void handleSos_AOA(Tag tag){
        if(tag.getIsbind()==1){
            if(tag.getBind_type()==1){
                String res=(String) redisUtil.get(device_check_sos_status_res + tag.getBind_key());
                if((res==null||res.equals("0"))&& tag.getSos()==1){
                    redisUtil.setnoTimeOut(device_check_sos_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Devicep devicep=devicePMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_key, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));

                }else if(tag.getSos()==0){
                    redisUtil.setnoTimeOut(device_check_sos_status_res + tag.getBind_key(),"0");
                }
            }
            else  if(tag.getBind_type()==2){
                String res=(String) redisUtil.get(person_check_sos_status_res + tag.getBind_key());
                if((res==null||res.equals("0"))&& tag.getSos()==1){
                    Person person=personMap.get(tag.getBind_key());
                    redisUtil.setnoTimeOut(person_check_sos_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_key, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));

                }else if(tag.getSos()==0){
                    redisUtil.setnoTimeOut(person_check_sos_status_res + tag.getBind_key(),"0");
                }
            }
        }
    }
    private void handleBt_AOA(Tag tag){
        if(tag.getIsbind()==1){
            if(tag.getBind_type()==1){
                String res=(String) redisUtil.get(device_check_bt_status_res + tag.getBind_key());
                if((res==null||res.equals("0"))&& tag.getBt()<=2.1){
                    redisUtil.setnoTimeOut(device_check_bt_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Devicep devicep=devicePMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));
                }else if(tag.getBt()>2.5){
                    redisUtil.setnoTimeOut(device_check_bt_status_res + tag.getBind_key(),"0");
                }
            }
            else  if(tag.getBind_type()==2){
                String res=(String) redisUtil.get(person_check_bt_status_res + tag.getBind_key());
                //    System.out.println("电量记录="+res);
                if((res==null||res.equals("0"))&& tag.getBt()<=2.1){
                    //    System.out.println("保存记录"+res);
                    redisUtil.setnoTimeOut(person_check_bt_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Person person=personMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
                    //  alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.device, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                }else if(tag.getBt()>2.5){
                    // System.out.println("保存记录66"+res);
                    redisUtil.setnoTimeOut(person_check_bt_status_res + tag.getBind_key(),"0");
                }
            }
        }
    }
    private void handleRun_AOA(Tag tag){
        if(tag.getIsbind()==1){
            if(tag.getBind_type()==1){
                String res=(String) redisUtil.get(device_check_run_status_res + tag.getBind_key());
                //   System.out.println("运动检测="+res);
                if((res==null||res.equals("0"))&& tag.getRun()==1){
                    redisUtil.setnoTimeOut(device_check_run_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Devicep devicep=devicePMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_run, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));
                }else if(tag.getRun()==0){
                    redisUtil.setnoTimeOut(device_check_run_status_res + tag.getBind_key(),"0");
                }
            }
            //人员基本用不到移动警告/暂时屏蔽
            /*else  if(beacon.getBind_type()==2){
                String res=(String) redisUtil.get(person_check_run_status_res + beacon.getBind_key());
                //    System.out.println("电量记录="+res);
                if((res==null||res.equals("0"))&&beacon.getRun()==1){
                    //    System.out.println("保存记录"+res);
                    redisUtil.setnoTimeOut(person_check_run_status_res + beacon.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Person person=personMap.get(beacon.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.person, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                    //  alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.device, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                }else{
                    // System.out.println("保存记录66"+res);
                    redisUtil.setnoTimeOut(person_check_run_status_res + beacon.getBind_key(),"0");
                }
            }*/
        }
    }
    private void handleFence(Tag tag, double pos){
        // System.out.println("围栏");
        if(tag.getIsbind()==1&& tag.getBind_key()!=null){
            int type= tag.getBind_type();
            if(type==1){
                Devicep devicep=devicePMap.get(tag.getBind_key());
                if(devicep==null){

                    System.out.println("handleFence()资产缓存异常");
                    return;
                }else if(devicep.getFence_id().isEmpty()){
                     System.out.println("没有绑定围栏");
                    return;
                }else{
                 String id=   devicep.getFence_id();
                 if(id.isEmpty()){
                     return;
                 }
                 String[] ids=id.split("_");
                for(String i:ids){
                    try {
                        int idd = Integer.parseInt(i);
                        device_a(tag,devicep,pos,fenceMap.get(idd));
                    }catch (Exception e){

                        System.out.println("应该是围栏绑定的ID为空异常***"+e);
                    }
                }

                }
            }else if(type==2){
                Person person=personMap.get(tag.getBind_key());
                if(person==null){
                    //handleFence()人员缓存异常
                    System.out.println("handleFence()人员缓存异常");
                    return;
                }else if(person.getFence_id()==0||person.getFence_id()==-1){
                    //System.out.println("没有绑定围栏");
                    return;
                }else{
                    //
                    //  System.out.println("人员有绑定围栏");
                    Fence fence=fenceMap.get(person.getFence_id());
                    if(fence!=null){

                        //围栏关闭，不执行
                        if(fence.getOpen_status()==false){
                            return;
                        }
                        //围栏类型是时间段
                        if(fence.getTime_type()==2){
                            //时间段为空，不执行，异常
                            if(fence.getStart_times()==null||fence.getStart_times()==null){
                                return;
                            }
                            String starts[]=fence.getStart_times().split(":");
                            String stops[]=fence.getStop_times().split(":");
                            Calendar calendar = Calendar.getInstance();
                            //  System.out.println("当前时间: " + calendar.getTime());
                            int hour=calendar.get(Calendar.HOUR_OF_DAY);
                            int min=calendar.get(Calendar.MINUTE);
                            int now=hour*60+min;
                            int start=Integer.parseInt(starts[0])*60+Integer.parseInt(starts[1]);
                            int stop=Integer.parseInt(stops[0])*60+Integer.parseInt(stops[1]);
                            if(start>stop){
                                stop=stop+1440;
                            }
                            if(start>=now||now>=stop){
                                return;
                            }else{
                                //  System.out.println("符合时间范围，执行判断");
                            }
                        }
                        Area area=area_Map.get(fence.getArea_id());
                        if(area!=null){
                            String points=area.getPoint();
                            if(points!=null){
                                String[] pointss=points.split(" ");
                                if(pointss.length>0){
                                    double xx;
                                    double yy;
                                    List<Point2D.Double> polygons=new ArrayList<>();

                                    for(String xy:pointss){
                                        String x=xy.split(",")[0];
                                        String y=xy.split(",")[1];
                                        if(x!=null&&!x.equals("")){
                                            xx=Double.parseDouble(x);
                                            yy=Double.parseDouble(y);
                                            polygons.add(new Point2D.Double(xx,yy));
                                        }
                                    }
                                    boolean status= isPointInPolygon(new Point2D.Double(tag.getX()*pos, tag.getY()*pos),polygons);
                                    FenceType fenceType=fence.getFence_type();
                                    //  System.out.println("人员真实状态="+fenceType+status);
                                    try {
                                        Integer count = (Integer) redisUtil.get(fence_check_person + tag.getBind_key());
                                        // System.out.println(count);
                                        if(count==null){
                                            count=0;
                                        }
                                        if ((status && fenceType == FenceType.OUT) || (!status && fenceType == FenceType.ON)) {

                                            String res = (String) redisUtil.get(fence_check_person_res + tag.getBind_key());
                                            //if(count!=0){
                                            // count=0;
                                            //}
                                            if (count < fence.getTimeout()) {
                                                count++;
                                            }
                                            redisUtil.set(fence_check_person + tag.getBind_key(), count);
                                            if (count >= fence.getTimeout() && (res == null || !res.equals("1"))) {
                                                redisUtil.setTimeOut_10s(fence_check_person_res + tag.getBind_key(), "1");
                                                StringUtil.sendFenceSosPerson(person);
                                                System.out.println("222-"+ tag.getMac() + "触发围栏报警");
                                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                                alarm_sql.addAlarm(alarmMapper, new Alarm(status ? Alarm_Type.fence_on_sos : Alarm_Type.fence_out_sos, Alarm_object.person, tag.getMap_key(), person.getFence_id(), fenceMap.get(person.getFence_id()).getName(), tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
                                            }
                                        } else {
                                            try {
                                                if (count > -fence.getTimeout()) {
                                                    count--;
                                                    redisUtil.set(fence_check_person + tag.getBind_key(), count);
                                                } else {
                                                    //  System.out.println(beacon.getMac() + "正常");
                                                    // redisUtil.set(fence_check_person+beacon.getBind_key(),0);
                                                    redisUtil.set(fence_check_person_res + tag.getBind_key(), "");
                                                }
                                                // System.out.println(beacon.getMac()+"-----------");
                                            } catch (Exception e) {
                                                System.out.println("异常" + e);
                                            }
                                        }
                                    }catch (Exception e){
                                        System.out.println("异常="+e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static boolean isPointInPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
        int count = 0;
        int size = polygon.size();
        for (int i = 0; i < size; i++) {
            Point2D.Double p1 = polygon.get(i);
            Point2D.Double p2 = polygon.get((i + 1) % size);
            if (p1.y == p2.y) {
                continue;
            }
            if (point.y < Math.min(p1.y, p2.y)) {
                continue;
            }
            if (point.y >= Math.max(p1.y, p2.y)) {
                continue;
            }
            double x = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
            if (x > point.x) {
                count++;
            } else if (x == point.x) {
                return true;
            }
        }
        return count % 2 == 1;
    }
 private void device_a(Tag tag, Devicep devicep, double pos, Fence fence)
 {
     if(fence!=null){
         //
         if(!fence.getOpen_status()){
             System.out.println("围栏关闭，不执行");
             return;
         }
         //围栏类型是时间段
         if(fence.getTime_type()==2){
             //
             if(fence.getStart_times()==null||fence.getStart_times()==null){
                 System.out.println("时间段为空，不执行，异常");
                 return;
             }
             String starts[]=fence.getStart_times().split(":");
             String stops[]=fence.getStop_times().split(":");
             Calendar calendar = Calendar.getInstance();
             //  System.out.println("当前时间: " + calendar.getTime());
             int hour=calendar.get(Calendar.HOUR_OF_DAY);
             int min=calendar.get(Calendar.MINUTE);
             int now=hour*60+min;
             int start=Integer.parseInt(starts[0])*60+Integer.parseInt(starts[1]);
             int stop=Integer.parseInt(stops[0])*60+Integer.parseInt(stops[1]);
             if(start>stop){
                 stop=stop+1440;
             }
             if(start>=now||now>=stop){
                 return;
             }else{
                 //  System.out.println("符合时间范围，执行判断");
             }
         }
         Area area=area_Map.get(fence.getArea_id());
         if(area!=null){
             String points=area.getPoint();
             if(points!=null){
                 String[] pointss=points.split(" ");
                 if(pointss.length>0){
                     double xx;
                     double yy;
                     List<Point2D.Double> polygons=new ArrayList<>();

                     for(String xy:pointss){
                         String x=xy.split(",")[0];
                         String y=xy.split(",")[1];
                         if(x!=null&&!x.equals("")){
                             xx=Double.parseDouble(x);
                             yy=Double.parseDouble(y);
                             polygons.add(new Point2D.Double(xx,yy));
                         }
                     }
                     boolean status= isPointInPolygon(new Point2D.Double(tag.getX()*pos, tag.getY()*pos),polygons);
                     FenceType fenceType=fence.getFence_type();
                     // System.out.println("真实状态="+fenceType+status+devicep.getBind_mac());
                     // int count= (int) redisUtil.get(fence_check_de+beacon.getBind_key());
                     Integer count = (Integer) redisUtil.get(fence_check_device + tag.getBind_key());
                     //System.out.println(count);
                     if(count==null){
                         count=0;
                     }
                     if((status&&fenceType==FenceType.OUT)||(!status&&fenceType==FenceType.ON)){

                         String res= (String) redisUtil.get(fence_check_device_res+ tag.getBind_key());
                         //if(count!=0){
                         // count=0;
                         //}

                         if(count<fence.getTimeout()){
                             count++;
                         }
                         redisUtil.set(fence_check_device+ tag.getBind_key(),count);
                         if(count>=fence.getTimeout()&&(res==null||!res.equals("1"))){
                             redisUtil.setTimeOut_10s(fence_check_device_res+ tag.getBind_key(),"1");
                             StringUtil.sendFenceSosDevice(devicep);
                             System.out.println("111-"+ tag.getMac()+"触发围栏报警");
                             Alarm_Sql alarm_sql=new Alarm_Sql();
                             alarm_sql.addAlarm(alarmMapper,new Alarm(status?Alarm_Type.fence_on_sos:Alarm_Type.fence_out_sos,Alarm_object.device, tag.getMap_key(),fence.getId(),fence.getName(), tag.getBt(),0,"",devicep.getName(),devicep.getSn(),devicep.getProject_key(),devicep.getLasttime()));
                         }
                     }else{
                         if(count>-fence.getTimeout()){
                             count--;
                             redisUtil.set(fence_check_device+ tag.getBind_key(),count);
                         }else{
                             redisUtil.set(fence_check_device_res+ tag.getBind_key(),"");
                         }
                     }
                 }
             }
         }
     }
 }
}


