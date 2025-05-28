package com.kunlun.firmwaresystem.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.MyWebSocket;
import com.kunlun.firmwaresystem.WebSocket_Registration;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.mappers.Real_PointMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.StringUtil;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
    Station station = null;
    Station_sql Station_sql=new Station_sql();
    static int  count =0;
    Tag tag =null;
    public CallBackHandlers(String topic, MqttMessage message) {
        this.message = message;
        this.topic = topic;

    }

    @Override
    public void run() {
        // subscribe后得到的消息会执行到这里面

        if(topic.equals("location_engine")){
            //myPrintln("log=订阅收到消息="+new String(message.getPayload()));
            if (message.getPayload()==null || message.getPayload().length==0){
                return;
            }

            String json_str=new String(message.getPayload());
            message.clearPayload();
            if (!json_str.contains("push_type")) {
                return;
            }
            Push_Device pushDevice= new Gson().fromJson(json_str, new TypeToken<Push_Device>() {}.getType());
          //  myPrintln(pushDevice.toString());
            time = System.currentTimeMillis()/1000;// new Date()为获取当前系统时间
            String pkt_type = null;
//{address='f0c890021001', gateway_address='null', bt='null', project_key='null', x=-0.23, y=7.33, map_key='bWFwXzE3MjY2NDYwNzg1MTY=', 0='location', device_type='beacon', last_time=1741589077}

            switch (pushDevice.getDevice_type()){
                case "gateway":
                 //   myPrintln("读取"+pushDevice.getAddress());
                    station = (Station) redisUtil.get(redis_key_locator + pushDevice.getAddress());

                    if (station == null) {
                       // 从数据库读取
                      //  myPrintln("从数据库读取");
                        Station station1= Station_sql.getStationByMac(StationMapper,pushDevice.getAddress());
                        if (station1!=null){
                            station=station1;
                        }
                        else{
                         //   myPrintln("不存在于系统的蓝牙网关基站，不添加，直接返回");
                            return;
                        }
                    }
                    if(pushDevice.getPush_type().equals("HeartBeat")){

                        station.setMap_key(pushDevice.getMap_key());
                        //这里会有一个过期的缓存
                        Map map = (Map) redisUtil.get(redis_id_map +station.getMap_key());
                        if (map == null) {
                            Map_Sql map_sql = new Map_Sql();
                            map= map_sql.getMapByMapkey(mapMapper,station.getMap_key());
                            if (map != null) {
                                station.setMap_name(map.getName());
                                map.setData("");
                                redisUtil.setnoTimeOut(redis_id_map +station.getMap_key(),map);
                            }else{
                                myPrintln("地图是空的"+station.getMap_key());
                                return;
                            }
                        }
                        else {
                            station.setMap_name(map.getName());
                        }
                        station.setLast_time(pushDevice.getLast_time());
                        station.setX(pushDevice.getX());
                        station.setY(pushDevice.getY());
                        station.setOnline(1);
                      //  myPrintln("设置基站坐标"+station.getAddress()+"   X="+station.getX()+"   Y="+station.getY());
                        //此平台原来是离线，有推送信息

                    }
                    else if (pushDevice.getPush_type().equals("online")){
                        station.setLast_time(pushDevice.getLast_time());
                        if (station.getOnline()==0){
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(station.getAddress(), station.getName(), Alarm_Type.sos_online, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                        }
                        station.setOnline(1);
                    }
                    else if (pushDevice.getPush_type().equals("offline")){
                        station.setLast_time(pushDevice.getLast_time());
                       // myPrintln("收到离线"+json_str);
                       if (station.getOnline()==1){
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(station.getAddress(), station.getName(), Alarm_Type.sos_offline, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), System.currentTimeMillis()/1000));
                        }
                       station.setOnline(0);
                    }
                    redisUtil.setnoTimeOut(redis_key_locator + pushDevice.getAddress(),station);
                   // Station_sql.updateStation(StationMapper, station);

                    break;
                case "beacon":
                case "kunlun_card":
                case "bracelet":
                case "OFcat1":
                  //  myPrintln(pushDevice.toString());
                    if(pushDevice.getPush_type().equals("location")){
                      //  myPrintln("dddddddd"+pushDevice.toString());
                        tag = tagsMap.get(pushDevice.getAddress());
                        if (tag == null) {
                            break;
                        }else{
                            if(pushDevice.getDevice_type().equals("kunlun_card")||pushDevice.getDevice_type().equals("OFcat1")){
                                tag.setRun(pushDevice.getRun());
                                tag.setSos(pushDevice.getSos());
                                //工卡运动时更新位置坐标
                                if (pushDevice.getRun()==1){
                                    tag.setX(pushDevice.getX());
                                    tag.setY(pushDevice.getY());

                                  //  myPrintln("更新位置"+tag.getX()+"   Y="+tag.getY());
                                }else{
                                   // myPrintln("不更新位置");
                                }
                            }else{
                                tag.setX(pushDevice.getX());
                                tag.setY(pushDevice.getY());
                            }

                            tag.setMap_key(pushDevice.getMap_key());
                            tag.setStation_address(pushDevice.getGateway_address());
                            tag.setOnline(1);
                            tag.setLastTime(pushDevice.getLast_time());
                        }
                        if (tag.getIsbind() == 1 && tag.getBind_key() != null) {
                            ArrayList<Object> deviceps = new ArrayList<>();
                            Map map = (Map) redisUtil.get(redis_id_map +tag.getMap_key());
                            if (map == null) {
                                Map_Sql map_sql = new Map_Sql();
                                map= map_sql.getMapByMapkey(mapMapper,tag.getMap_key());
                                if (map != null) {
                                    map.setData("");
                                    redisUtil.setnoTimeOut(redis_id_map +tag.getMap_key(),map);
                                }else{
                                    myPrintln("地图是空的"+tag.getMap_key());
                                    return;
                                }
                            }
                            if (tag.getBind_type() == 1) {
                                //myPrintln("开始处理");
                                hande_device(tag, deviceps, map,0);
                            }
                            else if (tag.getBind_type() == 2) {
                                hander_person(tag,null, map, deviceps,0,1);
                            }
                            if (!deviceps.isEmpty()) {
                                sendTagPush(deviceps, tag.getMap_key());
                                sendRelayPush(deviceps, tag.getMap_key());
                            }
                        }
                    }
                    else if(pushDevice.getPush_type().equals("status")){
                        {
                            // myPrintln("dddddddd"+pushDevice.toString());
                            tag = tagsMap.get(pushDevice.getAddress());
                            if (tag == null) {
                                break;
                            }else{
                                tag.setRun(pushDevice.getRun());
                                tag.setSos(pushDevice.getSos());
                                tag.setMap_key(pushDevice.getMap_key());
                                tag.setStation_address(pushDevice.getGateway_address());
                                tag.setOnline(1);
                                tag.setLastTime(pushDevice.getLast_time());
                            }
                            if (tag.getIsbind() == 1 && tag.getBind_key() != null) {
                                ArrayList<Object> deviceps = new ArrayList<>();
                                Map map = (Map) redisUtil.get(redis_id_map +tag.getMap_key());
                                if (map == null) {
                                    Map_Sql map_sql = new Map_Sql();
                                    map= map_sql.getMapByMapkey(mapMapper,tag.getMap_key());
                                    if (map != null) {
                                        map.setData("");
                                        redisUtil.setnoTimeOut(redis_id_map +tag.getMap_key(),map);
                                    }else{
                                        myPrintln(pushDevice.toString());
                                        myPrintln("status+地图是空的"+tag.getMap_key());
                                        //return;
                                    }
                                }
                                if (tag.getBind_type() == 1) {
                                    //myPrintln("开始处理");
                                    hande_device(tag, deviceps, map,0);
                                }
                                else if (tag.getBind_type() == 2) {
                                    hander_person(tag,null, map, deviceps,0,2);

                                }

                            }
                        }

                }
                    else if(pushDevice.getPush_type().equals("online")){
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            Alarm  alarm=new Alarm();
                            tag = tagsMap.get(pushDevice.getAddress());
                            if (tag == null) {
                                return;
                            }else {
                                if (tag.getIsbind() == 1 && tag.getBind_type()==1) {
                                    Devicep devicep=devicePMap.get(tag.getBind_key());

                                    if (devicep != null) {
                                        if (!pushDevice.getGateway_address().isEmpty())
                                        {
                                            devicep.setNear_s_address(pushDevice.getGateway_address());
                                            station = (Station) redisUtil.get(redis_key_locator + pushDevice.getGateway_address());

                                            if (station == null) {
                                                // 从数据库读取
                                           //     myPrintln("从数据库读取");
                                                Station station1= Station_sql.getStationByMac(StationMapper,pushDevice.getGateway_address());
                                                if (station1!=null){
                                                    station=station1;
                                                }
                                                else{
                                                   // myPrintln("不存在于系统的蓝牙网关基站，不添加，直接返回");
                                                    return;
                                                }
                                            }
                                            devicep.setNear_s_name(station.getName());
                                        }
                                        devicep.setOnline(1);
                                        if( tag.getOnline()!=1) {
                                            myPrintln("进入重复"+tag.getMac());
                                            alarm.setAlarm_object(Alarm_object.device);
                                            alarm.setStation_name(devicep.getNear_s_name());
                                            alarm.setStation_address(devicep.getNear_s_address());
                                            alarm.setAlarm_type(Alarm_Type.sos_online);
                                            alarm.setName(devicep.getName());
                                            alarm.setSn(devicep.getSn());
                                            alarm.setMap_key(devicep.getMap_key());
                                            alarm.setProject_key(devicep.getProject_key());
                                            alarm.setCreate_time(pushDevice.getLast_time());
                                            alarm_sql.addAlarm(alarmMapper, alarm);
                                        }

                                    }
                                }else if (tag.getIsbind() == 1&&tag.getBind_type() == 2) {
                                    Person person=personMap.get(tag.getBind_key());
                                    if (person != null) {
                                        person.setOnline(1);
                                        if( tag.getOnline()!=1) {
                                            alarm.setAlarm_object(Alarm_object.person);
                                            alarm.setAlarm_type(Alarm_Type.sos_online);
                                            alarm.setStation_address(person.getStation_mac());
                                            alarm.setStation_name(person.getStation_name());
                                            alarm.setName(person.getName());
                                            alarm.setSn(person.getIdcard());
                                            alarm.setMap_key(person.getMap_key());
                                            alarm.setProject_key(person.getProject_key());
                                            alarm.setCreate_time(pushDevice.getLast_time());
                                            alarm_sql.addAlarm(alarmMapper, alarm);
                                        }
                                    }
                                }
                                tag.setOnline(1);

                            }

                    }
                    else if(pushDevice.getPush_type().equals("offline")){
                        Alarm_Sql alarm_sql = new Alarm_Sql();
                        Alarm  alarm=new Alarm();
                        tag = tagsMap.get(pushDevice.getAddress());
                        if (tag == null) {
                            return;
                        }else {
                            if (tag.getIsbind() == 1 && tag.getBind_type()==1) {
                                myPrintln("进入这里 111"+tag.toString());
                                Devicep devicep=devicePMap.get(tag.getBind_key());
                                if (devicep != null) {
                                    devicep.setOnline(0);
                                    devicep.setSos(-1);
                                    devicep.setRun(-1);
                                    if ( tag.getOnline()!=0) {
                                        alarm.setAlarm_object(Alarm_object.device);
                                        alarm.setAlarm_type(Alarm_Type.sos_offline);
                                        alarm.setName(devicep.getName());
                                        alarm.setStation_address(devicep.getNear_s_address());
                                        alarm.setStation_name(devicep.getNear_s_name());
                                        alarm.setSn(devicep.getSn());
                                        alarm.setMap_key(devicep.getMap_key());
                                        alarm.setProject_key(devicep.getProject_key());
                                        alarm.setCreate_time(System.currentTimeMillis()/1000);
                                        alarm_sql.addAlarm(alarmMapper, alarm);
                                    }else{
                                        myPrintln("实际不可能的，在线"+tag.getOnline());
                                    }
                                }
                            }else if (tag.getIsbind() == 1&&tag.getBind_type() == 2) {
                                Person person=personMap.get(tag.getBind_key());
                                if (person != null) {
                                    person.setOnline(0);

                                    if ( tag.getOnline()!=0) {
                                        alarm.setAlarm_object(Alarm_object.person);
                                        alarm.setAlarm_type(Alarm_Type.sos_offline);
                                        alarm.setName(person.getName());
                                        alarm.setSn(person.getIdcard());
                                        alarm.setStation_address(person.getStation_mac());
                                        alarm.setStation_name(person.getStation_name());
                                        alarm.setMap_key(person.getMap_key());
                                        alarm.setProject_key(person.getProject_key());
                                        alarm.setCreate_time(System.currentTimeMillis()/1000);
                                        alarm_sql.addAlarm(alarmMapper, alarm);
                                    }
                                }
                            }
                            tag.setOnline(0);
                        }

                    }
                    else if(pushDevice.getPush_type().equals("bt")){
                    myPrintln("来自定位引擎数据"+pushDevice.getAddress());
                        tag = tagsMap.get(pushDevice.getAddress());
                        if (tag == null) {
                            return;
                        }else {
                            if (pushDevice.getBt()!=null){
                                String bt= pushDevice.getBt().replaceAll("m","");
                                bt=bt.replaceAll("V","");
                                bt=bt.replaceAll("%","");
                                bt=bt.replaceAll(" ","");
                                tag.setBt(Double.parseDouble(bt));
                            }

                            if (tag.getIsbind() == 1 && tag.getBind_type()==1) {
                                Devicep devicep=devicePMap.get(tag.getBind_key());
                                if (devicep != null) {
                                    devicep.setBt(tag.getBt());
                                    //devicep.setOnline(1);
                                }

                            }
                            //暂时不给人员设定电量，人员直接看
                            /*else if (tag.getIsbind() == 1&&tag.getBind_type() == 2) {
                                Person person=personMap.get(tag.getMap_key());
                                if (person != null) {

                                }
                            }*/

                        }

                    }
                        break;
            }
            try {



            }
            catch (Exception e ){

            }

        }
        if (topic.equals("/cle/mqtt")) {

            try {
               // count++;


                String map_key = "";
                String jsonstr = StringUtil.unzip(message.getPayload());
                message.clearPayload();
              //  myPrintln("计数=" + jsonstr );
               // myPrintln(jsonstr);
                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
              //  myPrintln(Thread.currentThread().getName());


                if (jsonObject.getString("type").equals("sensors")) {
                    //标签定位
                    JSONObject beacons = jsonObject.getJSONObject("data");
                    Set<String> macs = beacons.keySet();
                    ArrayList<Object> deviceps = new ArrayList<>();
                    ArrayList<Object> tags = new ArrayList<>();
                    // myPrintln("步骤1");
                    for (String key : macs) {

                        // myPrintln(key);
                        if (tagsMap.get(key) != null) {
                            Station station = null;
                             tag = tagsMap.get(key);
                            if (tag == null) {
                                continue;
                            }
                            JSONObject a = beacons.getJSONObject(key);

                           //
                            {

                                //  beacon.setX();

                                tag.setX(Double.parseDouble(decimalFormat.format(a.getDouble("x"))));
                                tag.setY(Double.parseDouble(decimalFormat.format(a.getDouble("y"))));
                                //  myPrintln("初始化Y="+beacon.getY());
                                tag.setLastTime(a.getLong("updatedAt") / 1000);
                                tag.setRssi(a.getIntValue("rssi"));
                                tag.setStation_address(a.getString("nearestGateway"));
                              //  myPrintln("基站 MAC="+tag.getStation_address());
                                station = (Station) redisUtil.get(redis_key_locator + tag.getStation_address());
                                if (station == null) {
                                    //从数据库读取
                                    Station station1=  Station_sql.getStationByMac(StationMapper,tag.getStation_address());
                                    if (station1!=null){
                                        station=station1;
                                    }
                                    else{
                                        myPrintln("不存在于系统的AOA基站"+tag.getStation_address());
                                        continue;
                                    }
                                }
                                //myPrintln("基站="+check+beacon.getStation_address());
                                tag.setOnline(1);
                                //tag.setBt(a.getIntValue("rssi"));
                                if (a.getJSONObject("userData") != null) {
                                    JSONObject userData = a.getJSONObject("userData");
                                    if (userData.getJSONArray("9") != null) {
                                        JSONArray s9 = userData.getJSONArray("9");
                                        String str = Integer.toBinaryString((((int) s9.get(1)) & 0xFF) + 0x100).substring(1);
                                        char[] bit = str.toCharArray();
                                        if (bit[2] == '1') {
                                            tag.setSos(1);
                                            //   myPrintln("信标SOS1");
                                        } else {
                                            //  myPrintln("信标SOS/"+str);
                                            tag.setSos(0);
                                        }
                                        if (bit[4] == '1') {
                                            tag.setRun(1);
                                        } else {
                                            // myPrintln("信标SOS/"+str);
                                            tag.setRun(0);
                                        }
                                        double bt = ((double) ((int) s9.get(3))) / 255 * 6.6;
                                        DecimalFormat decimalFormat = new DecimalFormat("#.00");
                                        String bts = decimalFormat.format(bt);
                                        tag.setBt(Double.parseDouble(bts));
                                      //  myPrintln("AOA 的电量="+tag.getMac()+"==="+bt );
                                    }

                                }
                            }


                            //这里会有一个过期的缓存
                            Map map = (Map) redisUtil.get(redis_id_map + a.getString("mapId"));
                            if (map != null) {
                                tag.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - Double.parseDouble(decimalFormat.format(a.getDouble("y"))))));
                                tag.setMap_key(map.getMap_key());
                                map_key=map.getMap_key();

                            } else {
                                myPrintln("地图没有缓存");
                                Map_Sql map_sql = new Map_Sql();
                                List<Map>  maps= map_sql.getMapBymapId(mapMapper,station.getUser_key(),station.getProject_key(),a.getString("mapId"));
                                if (maps != null&& !maps.isEmpty()) {
                                    map = maps.get(0);
                                    map.setData("");
                                    redisUtil.setnoTimeOut(redis_id_map + a.getString("mapId"),map);
                                }else{
                                    tag.setMap_key("");
                                    myPrintln("地图是空的");
                                }

                              //  continue;
                                if (map != null) {
                                    map_key=map.getMap_key();
                                }

                            }
                            if (tag.getIsbind() == 1 && tag.getBind_key() != null) {
                                if (tag.getBind_type() == 1) {
                                    //myPrintln("开始处理");
                                    hande_device(tag, deviceps, map,1);
                                }
                                else if (tag.getBind_type() == 2) {
                                 //   myPrintln("开始处理"+station);
                                    hander_person(tag, station, map, deviceps,1,1);
                                }
                            }
                        }
                    }
                    if (!deviceps.isEmpty()) {
                      //  myPrintln("AOA需要推送的1");
                        sendTagPush(deviceps, map_key);
                        sendRelayPush(deviceps, map_key);
                    } else {
                        //    myPrintln("没有需要推送的"+deviceps);
                    }
                } else if (jsonObject.getString("type").equals("locators")) {
                   // myPrintln("66666"+jsonObject);
                    JSONObject locators = jsonObject.getJSONObject("data");
                    Set<String> ips = locators.keySet();

                    //  myPrintln("步骤1");
                    for (String ip : ips) {
                     //    myPrintln("IP ="+ip);
                         try {
                             JSONObject a = locators.getJSONObject(ip);
                             if (a != null) {
                                 int tag = 0;
                                 String address = a.getString("mac").replaceAll(":", "");
                               //  myPrintln(address);
                                 try {
                                     station = (Station) redisUtil.get(redis_key_locator + address);

                                 } catch (Exception e) {
                                    // myPrintln("777" + e.getMessage());
                                 }
                                 if (station == null) {
                                     //从数据库读取
                                     Station station1 = Station_sql.getStationByMac(StationMapper, address);
                                     if (station1 != null) {
                                         myPrintln("shwai");
                                         station = station1;
                                     } else {
                                         // myPrintln("不存在于系统的AOA基站，不添加，直接返回");
                                         continue;
                                     }

                                     tag = 1;
                                 }
                                 JSONObject info = a.getJSONObject("info");

                                 station.setX(Double.parseDouble(decimalFormat.format(info.getDouble("x"))));
                                 station.setY(Double.parseDouble(decimalFormat.format(info.getDouble("y"))));
                                 station.setZ(info.getDouble("z"));
                                 myPrintln("mingzi" + station.getName());
                                 // myPrintln(check);

                                 Map map = (Map) redisUtil.get(redis_id_map + info.getString("mapId"));
                                 if (map != null) {
                                     station.setMap_key(map.getMap_key());
                                     station.setMap_name(map.getName());
                                     station.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - station.getY())));
                                 } else {
                                     myPrintln("地图没有缓存");
                                     Map_Sql map_sql = new Map_Sql();
                                     List<Map> maps = map_sql.getMapBymapId(mapMapper, station.getUser_key(), station.getProject_key(), info.getString("mapId"));
                                     if (maps != null && !maps.isEmpty()) {
                                         map = maps.get(0);
                                         station.setMap_key(map.getMap_key());
                                         station.setMap_name(map.getName());
                                         station.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - station.getY())));
                                         map.setData("");
                                         redisUtil.setnoTimeOut(redis_id_map + info.getString("mapId"), map);
                                     } else {
                                         myPrintln("此基站未有绑定地图,不去修改他的位置Y坐标");
                                     }
                                     //此基站未有绑定地图,不自动增加

                                     //   continue;
                                 }
                                 if (station.getName() != null && station.getName().contains("null")) {
                                     station.setName(info.getString("name"));
                                 }
                                 if (station.getOnline() != 1 && a.getBoolean("online")) {
                                     Alarm_Sql alarm_sql = new Alarm_Sql();
                                     alarm_sql.addAlarm(alarmMapper, new Alarm(station.getAddress(),station.getName(), Alarm_Type.sos_online, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                                 }
                                 if (station.getOnline() == 1 && !a.getBoolean("online")) {
                                     Alarm_Sql alarm_sql = new Alarm_Sql();
                                     alarm_sql.addAlarm(alarmMapper, new Alarm(station.getAddress(), station.getName(), Alarm_Type.sos_offline, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                                 }
                                 station.setOnline(a.getBoolean("online") ? 1 : 0);
                                 station.setLast_time(a.getLong("updatedAt") / 1000);
                                 if (tag == 1) {
                                     Station_sql.updateStation(StationMapper, station);
                                 }
                                 redisUtil.setnoTimeOut(redis_key_locator + address, station);
                             }
                         }catch (Exception e) {
                             myPrintln("888"+e.getMessage());
                         }
                    }
                }
            } catch (DataFormatException e) {
                myPrintln("AOA解析异常1" + e);
            } catch (IOException e) {
                myPrintln("AOA解析异常2" + e);
            }
            return;
        }




    }

    private void hander_person(Tag tag,Station station, Map map, ArrayList<Object> deviceps,int type,int keep_out) {

        Person person = personMap.get(tag.getBind_key());
        if (keep_out == 1) {
            person.setX(tag.getX());
            person.setY(tag.getY());
        }
        person.setSos(tag.getSos());
        person.setOnline(1);
        if (map != null) {
            person.setMap_key(map.getMap_key());
            person.setMap_name(map.getName());
        }
        person.setRun(tag.getRun());

        person.setStation_mac(tag.getStation_address());
        person.setOnline(1);
        person.setLasttime(tag.getLastTime());
    //   myPrintln("最后时间="+person.getLasttime());
        if (station==null) {
            station = (Station) redisUtil.get(redis_key_locator + tag.getStation_address());
        }
        // myPrintln(station.toString());
        if (station == null) {
            //从数据库读取
            Station station1=  Station_sql.getStationByMac(StationMapper,tag.getStation_address());
            if (station1!=null){
                station=station1;
                person.setStation_name(station.getName());
            }
        }else{
            person.setStation_name(station.getName());
        }
        deviceps.add(person);
        if (person.getMap_key() != null && !person.getMap_key().isEmpty()) {
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
        }
        if (map != null) {
            handleFence(tag, map.getProportion());
        }
        String patrol_id = person.getPatrol_list_id();
        //初始化，需要获取数据库信息，有绑定路线但是没有路线详情
        if (patrol_id != null && person.getPatrol_lists() == null) {
            Patrol_List_Sql patrol_sql = new Patrol_List_Sql();
            Area_Sql areaSql = new Area_Sql();
            person.setPatrol_list_ids(patrol_id);
            //一个人会有多个路线
            if (person.getPatrol_list_ids() != null && person.getPatrol_list_ids().length > 0) {
                ArrayList<Patrol_list> patrol_lists = new ArrayList<>();
                for (int i = 0; i < person.getPatrol_list_ids().length; i++) {
                    //获得每一条路线。
                    myPrintln("获得每一条路线。"+person.getPatrol_list_ids()[i]);
                    if (!person.getPatrol_list_ids()[i].isEmpty()) {
                        //获得每一条路线。
                        Patrol_list patrol_list = patrol_sql.getPatrolById(patrolListMapper, Integer.parseInt(person.getPatrol_list_ids()[i]));
                        if (patrol_list != null) {
                            //每个路线对应的每个点位
                            if (patrol_list.getPatrol_list() != null) {
                                String[] lists = patrol_list.getPatrol_list().split("-");
                                ArrayList<Patrol> patrolArrayList = new ArrayList<>();
                                Patrol_Sql patrolSql = new Patrol_Sql();
                                for (int j = 0; j < lists.length; j++) {
                                    try {
                                        if (!lists[j].isEmpty()) {
                                            int id = Integer.parseInt(lists[j]);
                                            //每个路线对应的每个点位
                                            Patrol patrol = patrolSql.getPatrolById(patrolMapper, id);
                                            if (patrol != null) {
                                                if (patrol.getEnable_day() != null) {
                                                    patrol.setEnable_days(patrol.getEnable_day());
                                                }
                                                patrol.setTime_range(patrol.getStartTime(), patrol.getEndTime());
                                                if (patrol_list.getMust_list().contains(lists[j])) {
                                                    patrol.setRequired(true);
                                                } else {
                                                    patrol.setRequired(false);
                                                }
                                                int area_id = patrol.getArea_id();
                                                if (area_id != 0) {
                                                    Area area = areaMapper.selectById(area_id);
                                                    if (area != null) {
                                                        myPrintln("区域点位="+area.getName());
                                                        patrol.setPoints(area.getPoint());
                                                    } else {
                                                        myPrintln("总有异常发生，不应该存在巡更点位获取不到对应区域点位的情况");
                                                        return;

                                                    }
                                                }
                                                patrolArrayList.add(patrol);
                                            }
                                        }
                                    } catch (Exception e) {
                                        myPrintln(e.toString());
                                    }
                                }
                                patrol_list.setPatrol_list_detail(patrolArrayList);
                            }
                            patrol_lists.add(patrol_list);
                        }
                    }
                }
                person.setPatrol_lists(patrol_lists);
            }
        } else if (person.getPatrol_lists() != null) {
            for (Patrol_list patrol_list : person.getPatrol_lists()) {
               // myPrintln("每条路线="+patrol_list.getName());
                for (Patrol patrol : patrol_list.getPatrol_list_detail()) {
                  //  myPrintln("每条点位="+patrol.getName());
                    String points = patrol.getPoints();
                    if (points != null && !points.isEmpty()) {
                      //  myPrintln("细分="+points);
                        String[] xys = points.split(" ");
                        String[][] xy = new String[xys.length][2];
                        int i = 0;
                        for (String point : xys) {

                            xy[i] = point.split(",");
                            i++;
                        }
                        try {
                            int result = checkPointLocation(person.getX()*patrol.getProportion(), person.getY()*patrol.getProportion(), xy, 3.0);
                           // myPrintln("结果="+result);
                            if(result<=1){
                              //  myPrintln("尝试插入数据");
                                try {
                                    Real_Point real_point = new Real_Point();
                                    real_point.setIdcard(person.getIdcard());
                                    real_point.setCreate_time(System.currentTimeMillis() / 1000);
                                    real_point.setPartol_id(patrol.getId());
                                    realPointMapper.insert(real_point);
                                    myPrintln(patrol.getName()+"插入数据完成");
                                }catch (Exception e){
                                    myPrintln(e.toString());
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("输入错误: " + e.getMessage());
                        }
                    }
                }
            }
        }
     //   myPrintln("person="+person);

        if(registration_map.get(person.getProject_key()) != null&&registration_map.get(person.getProject_key()).isRun()) {
            registration_map.get(person.getProject_key()).addPerson(person);
            try {
                WebSocket_Registration webSocketRegistration = WebSocket_Registration.getWebSocket();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data",  person);
                myPrintln("person" + person);
                myPrintln("h获取 准备发送" + jsonObject);
                webSocketRegistration.sendData(person.getProject_key(), jsonObject.toJSONString());
            }catch (Exception e){
                myPrintln("异常="+e.toString());
            }
        }else{
           // myPrintln("排除在外的人员"+person.getIdcard());
        }

        Check_sheet checkSheet = check_sheetMap.get(person.getProject_key());
        //有需要做人员静止或者离开检测的需求
        if (checkSheet != null&&checkSheet.isTime_out_set_status())
        {

            List<String[]> time_list=new ArrayList<>();
            if (checkSheet.getTime_set() != null) {
                time_list.addAll(Arrays.asList(checkSheet.getTime_set()));
            }
            for (int i = 0; i < time_list.size(); i++) {
             //   myPrintln("时间段="+time_list.get(i)[0]+"-"+time_list.get(i)[1]);
            }

            //在不在检测的时间范围内
            boolean status=isTimeInRanges(person.getLasttime(),time_list);
          //  myPrintln("人员在线时间="+person.getLasttime());
         //   myPrintln("是否在时间段范围="+status);
            if (status) {
            //    myPrintln("是否静止或者运动="+person.getRun());
                //在时间范围。并且为静止，触发判断
                if(person.getRun()==0){
                   String  keep=(String) redisUtil.get(redis_key_person_run_result+person.getIdcard());
                   if (keep != null && !keep.isEmpty()) {
                       if (keep.equals("1")){
                           //
                           myPrintln("静止结果未到超时时间");
                           return;
                       }
                   }
                    String time1= (String) redisUtil.get(redis_key_person_run+person.getIdcard());
                    long time=0;
                    if (time1 != null&& !time1.isEmpty()) {

                        time= Long.parseLong(time1);
                    }
                    //第一次静止，前面没有保存
                    myPrintln(person.getIdcard()    + "上次保存时间="+time);
                    if (time==0){
                        //静止的时候，把超出范围的判断清0
                        redisUtil.setnoTimeOut(redis_key_person_out+person.getIdcard(), 0+"");
                        //设置第一次保存
                        myPrintln("设置第一次保存="+person.getRun());
                        redisUtil.setnoTimeOut(redis_key_person_run+person.getIdcard(), ""+person.getLasttime());
                    }
                    //否则，拿出时间，与当前时间对比
                    else{
                        //计算时间差，判断是否超过系统设置的时间阈值
                      long time_c=  person.getLasttime()-time;
                        myPrintln("阈值时间="+checkSheet.getTime_static_out());
                        myPrintln("人员在线时间="+person.getLasttime());
                        myPrintln("时间差值="+time_c);
                      if (time_c>=checkSheet.getTime_static_out()){
                          //超过时间，需要触发静止预警
                          Alarm_Sql alarm_sql=new Alarm_Sql();
                          Alarm alarm=new Alarm();
                          alarm.setAlarm_object(Alarm_object.person);
                          alarm.setAlarm_type(Alarm_Type.keep_static);
                          alarm.setName(person.getName());
                          alarm.setSn(person.getIdcard());
                          alarm.setStation_address(person.getStation_mac());
                          alarm.setStation_name(person.getStation_name());
                          alarm.setMap_key(person.getMap_key());
                          alarm.setProject_key(person.getProject_key());
                          alarm.setCreate_time(System.currentTimeMillis()/1000);
                          alarm_sql.addAlarm(alarmMapper, alarm);
                          redisUtil.setnoTimeOut(redis_key_person_run+person.getIdcard(), 0+"");
                          redisUtil.set(redis_key_person_run_result+person.getIdcard(), "1",checkSheet.getTime_keep());
                          JSONObject jsonObject = new JSONObject();
                          jsonObject.put("data",  person);
                          jsonObject.put("type","keep_static");
                          MyWebSocket.getWebSocket().sendData(person.getProject_key(),jsonObject.toJSONString());
                      }
                    }
                }else{
                    //把静止的判断时间清理
                    redisUtil.setnoTimeOut(redis_key_person_run+person.getIdcard(), 0+"");
                    //说明是没有计算位置传送
                    //2 表示是由定位引擎推送过来的只限于 OFCAT1工卡的数据，没有定位 xy
                          if(keep_out==2){
                              String  keep=(String) redisUtil.get(redis_key_person_out_result+person.getIdcard());
                              if (keep != null && !keep.isEmpty()) {
                                  if (keep.equals("1")){
                                      //
                                      myPrintln("越界的---结果未到超时时间");
                                      return;
                                  }
                              }

                              String time1= (String) redisUtil.get(redis_key_person_out+person.getIdcard());
                              long time=0;
                              if (time1 != null&& !time1.isEmpty()) {
                                  time= Long.parseLong(time1);
                              }
                              if (time==0){
                                  //静止的时候，把超出范围的判断清0
                                  //redisUtil.setnoTimeOut(redis_key_person_run+person.getIdcard(), 0+"");
                                  //设置第一次保存
                                  myPrintln("越界的设置第一次保存="+person.getRun());
                                  redisUtil.setnoTimeOut(redis_key_person_out+person.getIdcard(), ""+person.getLasttime());
                              }
                              //否则，拿出时间，与当前时间对比
                              else{
                                  //计算时间差，判断是否超过系统设置的时间阈值
                                  long time_c=  person.getLasttime()-time;
                                  myPrintln("越界的阈值时间="+checkSheet.getTime_keep_out());
                                  myPrintln("越界的人员在线时间="+person.getLasttime());
                                  myPrintln("越界的时间差值="+time_c);
                                  if (time_c>=checkSheet.getTime_keep_out()){
                                      //超过时间，需要触发静止预警
                                      Alarm_Sql alarm_sql=new Alarm_Sql();
                                      Alarm alarm=new Alarm();
                                      alarm.setAlarm_object(Alarm_object.person);
                                      alarm.setAlarm_type(Alarm_Type.keep_out);
                                      alarm.setName(person.getName());
                                      alarm.setSn(person.getIdcard());
                                      alarm.setStation_address(person.getStation_mac());
                                      alarm.setStation_name(person.getStation_name());
                                      alarm.setMap_key(person.getMap_key());
                                      alarm.setProject_key(person.getProject_key());
                                      alarm.setCreate_time(System.currentTimeMillis()/1000);
                                      alarm_sql.addAlarm(alarmMapper, alarm);
                                      redisUtil.setnoTimeOut(redis_key_person_out+person.getIdcard(), 0+"");
                                      redisUtil.set(redis_key_person_out_result+person.getIdcard(), "1",checkSheet.getTime_keep());
                                      JSONObject jsonObject = new JSONObject();
                                      jsonObject.put("data",  person);
                                      jsonObject.put("type","keep_out");
                                      MyWebSocket.getWebSocket().sendData(person.getProject_key(),jsonObject.toJSONString());
                                  }
                              }
                          }
                          else{
                              redisUtil.setnoTimeOut(redis_key_person_out+person.getIdcard(), 0+"");
                          }
                    //出于运动状态
                    //需要判断是否运动有包含基站定位
                    //如果没有，则有需要触发超出定位地图的报警

                }
            }
            //如果不在检测时间范围，那就把时间清掉，避免冲突
            else {
                redisUtil.setnoTimeOut(redis_key_person_run+person.getIdcard(), 0+"");
                redisUtil.setnoTimeOut(redis_key_person_out+person.getIdcard(), 0+"");
            }


        }

//0表示该标签是非 AOA 标签，只有 AOA 标签才会继续执行
        if (type == 1) {
            handleSos_AOA(tag);
            handleBt_AOA(tag);
        }


    }
    public static boolean isTimeInRanges(long timestamp, List<String[]> timeRanges) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date(timestamp*1000)); // 格式化为 "HH:mm"
      //  myPrintln("转换后的时间="+currentTime);
        for (String[] range : timeRanges) {
            String startTime = range[0].substring(0, 5); // 取 "HH:mm" 部分
            String endTime = range[1].substring(0, 5);   // 取 "HH:mm" 部分

            if (isTimeBetween(currentTime, startTime, endTime)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isTimeBetween(String time, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date current = sdf.parse(time);
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);

            // 处理跨天的情况（如 23:00 - 01:00）
            if (end.before(start)) {
                return !current.before(start) || !current.after(end);
            } else {
                return !current.before(start) && !current.after(end);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("时间格式无效，必须是 HH:mm", e);
        }
    }
    public static int checkPointLocation(double x, double y, String[][] polygonVertices, double maxDistance) {
        // 1. 转换输入数据
        //myPrintln("开始计算 x="+x+" y="+y);
        List<Point> polygon = parsePolygon(polygonVertices);
       // myPrintln("开始计算 22");
        // 2. 验证多边形有效性
        if (polygon.size() < 3) {
            myPrintln("多边形需要至少3个顶点");
        }
        //myPrintln("开始计算 33");
        // 3. 创建检测点
        Point p = new Point(x, y);

        // 4. 判断位置状态
        if (isPointInPolygon(p, polygon)) {
            return 0;
        }
       // myPrintln("开始计算 44");
        double minDist = calculateMinDistanceToEdges(p, polygon);
        ///myPrintln("开始计算 55="+minDist);
        return (minDist <= maxDistance) ? 1: 2;
    }

    private static List<Point> parsePolygon(String[][] vertices) {
        List<Point> polygon = new ArrayList<>();
      //  myPrintln(vertices.length + "");
        int i = 0;
        for (String[] vertex : vertices) {
       //     myPrintln( "I="+i++);
            if (vertex.length != 2) {
           //    myPrintln("顶点数据格式应为[经度,纬度]");

            }
            else {
               // myPrintln( "进行"+vertex[0]+","+vertex[1]);
                try {
                    double x = Double.parseDouble(vertex[0]);
                    double y = Double.parseDouble(vertex[1]);
                    polygon.add(new Point(x, y));
               //     myPrintln("polygon=" + polygon.size());
                } catch (NumberFormatException e) {
                    myPrintln("无效的坐标格式: [" + vertex[0] + "," + vertex[1] + "]");

                }
            }
        }
       // myPrintln( "444444");
        return polygon;
    }

    private static boolean isPointInPolygon(Point p, List<Point> polygon) {
        double x = p.x;
        double y = p.y;
        boolean inside = false;
        int n = polygon.size();

        for (int i = 0; i < n; i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % n);
            double xi = a.x, yi = a.y;
            double xj = b.x, yj = b.y;

            // 检查点是否在边的纵向范围内
            if ((yi < y && yj >= y) || (yj < y && yi >= y)) {
                // 计算射线与边的交点x坐标
                double t = (y - yi) / (yj - yi + 1e-20); // 避免除零
                double xIntersect = xi + t * (xj - xi);

                if (xIntersect <= x) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }


    private static double calculateMinDistanceToEdges(Point p, List<Point> polygon) {
        double minDist = Double.MAX_VALUE;
        int n = polygon.size();

        for (int i = 0; i < n; i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % n);
            double dist = pointToSegmentDistance(p, a, b);
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }


    private static double pointToSegmentDistance(Point p, Point a, Point b) {
        double px = p.x, py = p.y;
        double ax = a.x, ay = a.y;
        double bx = b.x, by = b.y;

        // 向量AB和AP
        double abx = bx - ax;
        double aby = by - ay;
        double apx = px - ax;
        double apy = py - ay;

        // 计算投影参数t
        double t = (apx * abx + apy * aby) / (abx * abx + aby * aby + 1e-20);
        t = Math.max(0, Math.min(1, t)); // 限制在线段范围内

        // 垂足坐标
        double nearestX = ax + t * abx;
        double nearestY = ay + t * aby;

        // 欧氏距离
        double dx = px - nearestX;
        double dy = py - nearestY;
        return Math.sqrt(dx * dx + dy * dy);
    }


    private static class Point {
        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }





    private void hande_device(Tag tag, ArrayList<Object> deviceps, Map map,int type) {
        if (devicePMap==null|| devicePMap.isEmpty()) {
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
        }
        Devicep devicep = devicePMap.get(tag.getBind_key());
        if(devicep==null){
            myPrintln("设备是空的");
            return;
        }
        devicep.setMap_key(tag.getMap_key());
        if (map!=null) {
            devicep.setMap_name(map.getName());
        }
        devicep.setX(tag.getX());
        devicep.setY(tag.getY());
        devicep.setLasttime(tag.getLastTime());
        devicep.setNear_s_address(tag.getStation_address());
        devicep.setOnline(1);
        station = (Station) redisUtil.get(redis_key_locator + tag.getStation_address());
        // myPrintln(station.toString());
        if (station == null) {
            //从数据库读取
            Station station1=  Station_sql.getStationByMac(StationMapper,tag.getStation_address());
            if (station1!=null){
           //     myPrintln("shwai");
                station=station1;
                devicep.setNear_s_name(station.getName());
            }

        }else{
            devicep.setNear_s_name(station.getName());
        }

        devicep.setType("device");
        devicep.setSos(tag.getSos());
        devicep.setOnline(1);
        devicep.setRun(tag.getRun());
        devicep.setBt(tag.getBt());
        deviceps.add(devicep);
        if (devicep.getMap_key()!=null&&!devicep.getMap_key().isEmpty()) {
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
        }


        if (map!=null) {
            handleFence(tag, map.getProportion());
        }

        if (type==1){
            handleSos_AOA(tag);
            handleBt_AOA(tag);
        }
    }

    private void BeaconHandle(Tag tag) {

/*
                        Alarm_Sql alarm_sql = new Alarm_Sql();


                        alarm_sql.addAlarm(alarmMapper,new Alarm(Alarm_Type.sos_key,Alarm_object.device,beacon.getMap_key(),-1,"",beacon.getBt(),0,"",deviceP.getName(),deviceP.getSn(),deviceP.getProject_key(),deviceP.getLasttime()));


                    if(beacon.getOnline()!=1&&beacon.getIsbind()==1){
                        myPrintln("接收到设备 1");
                        if(beacon.getBind_type()==1){
                            myPrintln("接收到设备 2");

                            alarm_sql.addAlarm(alarmMapper,new Alarm(Alarm_Type.sos_online,Alarm_object.device,beacon.getMap_key(),-1,"",beacon.getBt(),0,"",deviceP.getName(),deviceP.getSn(),deviceP.getProject_key(),deviceP.getLasttime()));
                            myPrintln("接收到设备 3");
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
                    alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(), Alarm_Type.sos_key, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));

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
                    alarm_sql.addAlarm(alarmMapper, new Alarm(person.getStation_mac(), person.getStation_name(), Alarm_Type.sos_key, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));

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
                    alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(), Alarm_Type.sos_bt, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));
                }else if(tag.getBt()>2.5){
                    redisUtil.setnoTimeOut(device_check_bt_status_res + tag.getBind_key(),"0");
                }
            }
            else  if(tag.getBind_type()==2){
                String res=(String) redisUtil.get(person_check_bt_status_res + tag.getBind_key());
                //    myPrintln("电量记录="+res);
                if((res==null||res.equals("0"))&& tag.getBt()<=2.1){
                    //    myPrintln("保存记录"+res);
                    redisUtil.setnoTimeOut(person_check_bt_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Person person=personMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(person.getStation_mac(), person.getStation_name(), Alarm_Type.sos_bt, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
                    //  alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.device, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                }else if(tag.getBt()>2.5){
                    // myPrintln("保存记录66"+res);
                    redisUtil.setnoTimeOut(person_check_bt_status_res + tag.getBind_key(),"0");
                }
            }
        }
    }
    private void handleRun_AOA(Tag tag){
        if(tag.getIsbind()==1){
            if(tag.getBind_type()==1){
                String res=(String) redisUtil.get(device_check_run_status_res + tag.getBind_key());
                //   myPrintln("运动检测="+res);
                if((res==null||res.equals("0"))&& tag.getRun()==1){
                    redisUtil.setnoTimeOut(device_check_run_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Devicep devicep=devicePMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(),  Alarm_Type.sos_run, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));
                }else if(tag.getRun()==0){
                    redisUtil.setnoTimeOut(device_check_run_status_res + tag.getBind_key(),"0");
                }
            }
            //人员基本用不到移动警告/暂时屏蔽
            /*else  if(beacon.getBind_type()==2){
                String res=(String) redisUtil.get(person_check_run_status_res + beacon.getBind_key());
                //    myPrintln("电量记录="+res);
                if((res==null||res.equals("0"))&&beacon.getRun()==1){
                    //    myPrintln("保存记录"+res);
                    redisUtil.setnoTimeOut(person_check_run_status_res + beacon.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Person person=personMap.get(beacon.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.person, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                    //  alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.device, beacon.getMap_key(), 0, "", beacon.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key()));
                }else{
                    // myPrintln("保存记录66"+res);
                    redisUtil.setnoTimeOut(person_check_run_status_res + beacon.getBind_key(),"0");
                }
            }*/
        }
    }
    private void handleFence(Tag tag, double pos){

        if(tag.getIsbind()==1&& tag.getBind_key()!=null){
            int type= tag.getBind_type();

            if(type==1){
                Devicep devicep=devicePMap.get(tag.getBind_key());
              //  myPrintln("处理围栏失败6"+devicep);
                if(devicep==null){

                    myPrintln("资产缓存异常");
                }else if(devicep.getFence_id()!=0&&devicep.getFence_id() !=-1) {
                    //单个设备单个围栏
                    Fence fence = fenceMap.get(devicep.getFence_id());
                   // myPrintln("处理围栏");
                    if (fence != null) {
                        boolean status = hander_fence_detail(tag, pos, fence);
                        //true  表示处罚了围栏
                       // myPrintln("处理围栏"+status);
                        if (status) {
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(), fence.getFence_type()==FenceType.OUT?Alarm_Type.fence_on_sos:Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence.getId(), fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
                            StringUtil.sendFenceSosDevice(devicep);
                        }
                    }
                }else if(devicep.getFence_group_id()!=0&&devicep.getFence_group_id() !=-1) {
                   // myPrintln("处理围栏组");
                    //单个设备单个围栏组
                    Fence_group fenceGroup = (Fence_group) redisUtil.get(fence_group+devicep.getFence_group_id());
                    if (fenceGroup == null) {
                        fenceGroup =  fenceGroupMapper.selectById(devicep.getFence_group_id());
                        redisUtil.setnoTimeOut(fence_group+ devicep.getFence_group_id(), fenceGroup);
                    }
                    handle_fence_group(tag, pos, fenceGroup, devicep);
                }
                if (devicep != null && devicep.getGroup_id() != 0 && devicep.getGroup_id() != -1) {
                    Group group = (Group) redisUtil.get(device_person_group + devicep.getGroup_id());
                    if (group == null) {
                        group = groupMapper.selectById(devicep.getGroup_id());
                        if (group != null) {
                            redisUtil.setnoTimeOut(device_person_group + devicep.getGroup_id(), group);
                        } else {
                            myPrintln("围栏组异常为空"+devicep.getGroup_id());
                            return;
                        }
                    }
                    //设备组绑定单个围栏
                    if (group.getF_id() != 0 && group.getF_id() != -1) {
                        Fence fence = fenceMap.get(group.getF_id());
                        if (fence != null) {
                            boolean status = hander_fence_detail(tag, pos, fence);
                            if (status) {
                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(),fence.getFence_type() == FenceType.OUT ? Alarm_Type.fence_on_sos : Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence.getId(), fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
                                StringUtil.sendFenceSosDevice(devicep);
                            }
                        }
                    } else if (group.getF_g_id() != 0 && group.getF_g_id() != -1) {
                        //设备组绑定围围栏组
                        Fence_group fenceGroup = (Fence_group) redisUtil.get(fence_group + group.getF_g_id());
                        if (fenceGroup == null) {
                            fenceGroup = fenceGroupMapper.selectById(group.getF_g_id());
                            redisUtil.setnoTimeOut(fence_group + group.getF_g_id(), fenceGroup);
                        }
                        handle_fence_group(tag, pos, fenceGroup, devicep);
                    }
                }


            }else if(type==2){
                Person person=personMap.get(tag.getBind_key());
                if(person==null){
                    myPrintln("人员缓存异常");
                }else if(person.getFence_id()!=0&&person.getFence_id()!=-1){
                    //myPrintln("没有绑定围栏");
                    Fence fence=fenceMap.get(person.getFence_id());
                    if(fence!=null) {
                        boolean status= hander_fence_detail(tag, pos, fence);
                        //true  表示处罚了围栏
                        if (status) {
                            Alarm_Sql   alarm_sql=new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(person.getStation_mac(), person.getStation_name(),fence.getFence_type()==FenceType.OUT?Alarm_Type.fence_on_sos:Alarm_Type.fence_out_sos, Alarm_object.person, tag.getMap_key(), person.getFence_id(), fenceMap.get(person.getFence_id()).getName(), tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
                            StringUtil.sendFenceSosPerson(person);
                        }
                    }
                }
            }
        }
    }

    private static void handle_fence_group(Tag tag, double pos, Fence_group fenceGroup, Devicep devicep) {

        if (fenceGroup != null) {
         //   myPrintln("处理围栏组1");
          String f_ids=  fenceGroup.getF_id();
          if (f_ids!=null&& !f_ids.isEmpty()) {
              String[] ids = f_ids.split("-9635241-");
              for (String id : ids) {
                // myPrintln("处理围***="+id);
                  if(id.isEmpty()){
                      continue;
                  }
                  int fence_id= Integer.parseInt(id);
                //  myPrintln("处理围*///"+fence_id);
                  if (fence_id>0) {
                      Fence fence = fenceMap.get(fence_id);
                      //myPrintln("围栏名称="+fence.getName());
                      if (fence != null) {
                          boolean status = hander_fence_detail(tag, pos, fence);
                          if (status) {
                              try {
                                  Alarm_Sql alarm_sql = new Alarm_Sql();
                                  alarm_sql.addAlarm(alarmMapper, new Alarm(devicep.getNear_s_address(), devicep.getNear_s_name(), fence.getFence_type() == FenceType.OUT ? Alarm_Type.fence_on_sos : Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence_id, fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
                                  StringUtil.sendFenceSosDevice(devicep);
                              }catch (Exception e){
                                  myPrintln("异常="+e.getMessage());
                              }
                          }
                      }
                  }
              }
          }
        }
    }

    private static boolean hander_fence_detail(Tag tag, double pos, Fence fence) {
        //围栏关闭，不执行
        if(!fence.getOpen_status()){
            return false;
        }
        //围栏类型是时间段
        if(fence.getTime_type()==2){
            //时间段为空，不执行，异常
            if(fence.getStart_times()==null|| fence.getStart_times()==null){
                return false;
            }
            String[] starts = fence.getStart_times().split(":");
            String[] stops = fence.getStop_times().split(":");
            Calendar calendar = Calendar.getInstance();
            //  myPrintln("当前时间: " + calendar.getTime());
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int min=calendar.get(Calendar.MINUTE);
            int now=hour*60+min;
            int start=Integer.parseInt(starts[0])*60+Integer.parseInt(starts[1]);
            int stop=Integer.parseInt(stops[0])*60+Integer.parseInt(stops[1]);
            if(start>stop){
                stop=stop+1440;
            }
            if(start>=now||now>=stop){
                return false;
            }else{
                //  myPrintln("符合时间范围，执行判断");
            }
        }
        String points= fence.getPoints();
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
                boolean status= isPointInPolygon(new Point2D.Double(tag.getX()* pos, tag.getY()* pos),polygons);
                FenceType fenceType= fence.getFence_type();
                //  myPrintln("人员真实状态="+fenceType+status);
                try {
                    Integer count = (Integer) redisUtil.get(fence_check_object+fence.getId() + tag.getBind_key());
                    // myPrintln(count);
                    if(count==null){
                        count=0;
                    }
                    if ((status && fenceType == FenceType.OUT) || (!status && fenceType == FenceType.ON)) {

                        String res = (String) redisUtil.get(fence_check_object_res+fence.getId() + tag.getBind_key());
                        //if(count!=0){
                        // count=0;
                        //}
                        if (count < fence.getTrigger1()) {
                            count++;
                        }
                        redisUtil.set(fence_check_object+fence.getId() + tag.getBind_key(), count);
                        if (count >= fence.getTrigger1() && (res == null || !res.equals("1"))) {
                            redisUtil.setTimeOut(fence_check_object_res+fence.getId() + tag.getBind_key(), "1",fence.getTime_out());
                          //  myPrintln(new Date()+"222-"+ tag.getBind_key() + "触发围栏报警");
                            return true;
                        }
                    } else {
                        try {
                            if (count > -fence.getTrigger1()) {
                                count--;
                                redisUtil.set(fence_check_object+fence.getId() + tag.getBind_key(), count);
                            } else {
                                //  myPrintln(beacon.getMac() + "正常");
                                // redisUtil.set(fence_check_person+beacon.getBind_key(),0);
                                redisUtil.set(fence_check_object_res+fence.getId() + tag.getBind_key(), "");
                            }
                            // myPrintln(beacon.getMac()+"-----------");
                        } catch (Exception e) {
                            myPrintln("异常" + e);
                        }
                    }
                }catch (Exception e){
                    myPrintln("异常="+e);
                }
            }
        }
        return false;
    }

    private static boolean isPointInPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
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

}


