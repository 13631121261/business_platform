package com.kunlun.firmwaresystem.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.device.Group;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.StringUtil;
import org.eclipse.paho.mqttv5.common.MqttMessage;

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
            myPrintln("log=订阅收到消息="+new String(message.getPayload()));
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
                    myPrintln("读取"+pushDevice.getAddress());
                    station = (Station) redisUtil.get(redis_key_locator + pushDevice.getAddress());

                    if (station == null) {
                        //从数据库读取
                        myPrintln("从数据库读取");
                        Station station1= Station_sql.getStationByMac(StationMapper,pushDevice.getAddress());
                        if (station1!=null){
                            station=station1;
                        }
                        else{
                            myPrintln("不存在于系统的蓝牙网关基站，不添加，直接返回");
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
                        //此平台原来是离线，有推送信息

                    }
                    else if (pushDevice.getPush_type().equals("online")){
                        station.setOnline(1);
                      //  if (station.getOnline()==0){
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                        //}
                    }
                    else if (pushDevice.getPush_type().equals("offline")){
                        station.setOnline(0);
                       // if (station.getOnline()==1){
                            Alarm_Sql alarm_sql = new Alarm_Sql();
                            alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_offline, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                       // }
                    }
                    redisUtil.setnoTimeOut(redis_key_locator + pushDevice.getAddress(),station);
                    Station_sql.updateStation(StationMapper, station);

                    break;
                case "beacon":
                    if(pushDevice.getPush_type().equals("location")){
                        tag = tagsMap.get(pushDevice.getAddress());
                        if (tag == null) {
                            return;
                        }else{
                            tag.setX(pushDevice.getX());
                            tag.setY(pushDevice.getY());
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
                                    redisUtil.setnoTimeOut(redis_id_map +tag.getMap_key(),map);
                                }else{
                                    myPrintln("地图是空的"+tag.getMap_key());
                                    return;
                                }
                            }
                            if (tag.getBind_type() == 1) {
                                //myPrintln("开始处理");
                                hande_device(tag, deviceps, map);
                            }
                            else if (tag.getBind_type() == 2) {
                                hander_person(tag, station, tag.getMap_key(), map, deviceps);
                            }
                            if (!deviceps.isEmpty()) {
                                //myPrintln("需要推送的1");
                                sendTagPush(deviceps, tag.getMap_key());
                                sendRelayPush(deviceps, tag.getMap_key());
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
                                    Devicep devicep=devicePMap.get(tag.getMap_key());
                                    if (devicep != null) {
                                        devicep.setOnline(1);
                                        if( tag.getOnline()!=1) {
                                            alarm.setAlarm_object(Alarm_object.device);
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
                                    Person person=personMap.get(tag.getMap_key());
                                    if (person != null) {
                                        person.setOnline(1);
                                        if( tag.getOnline()!=1) {
                                            alarm.setAlarm_object(Alarm_object.person);
                                            alarm.setAlarm_type(Alarm_Type.sos_online);
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
                                Devicep devicep=devicePMap.get(tag.getMap_key());
                                if (devicep != null) {
                                    devicep.setOnline(0);
                                    if ( tag.getOnline()!=0) {
                                        alarm.setAlarm_object(Alarm_object.device);
                                        alarm.setAlarm_type(Alarm_Type.sos_offline);
                                        alarm.setName(devicep.getName());
                                        alarm.setSn(devicep.getSn());
                                        alarm.setMap_key(devicep.getMap_key());
                                        alarm.setProject_key(devicep.getProject_key());
                                        alarm.setCreate_time(pushDevice.getLast_time());
                                        alarm_sql.addAlarm(alarmMapper, alarm);
                                    }

                                }

                            }else if (tag.getIsbind() == 1&&tag.getBind_type() == 2) {
                                Person person=personMap.get(tag.getMap_key());
                                if (person != null) {
                                    person.setOnline(0);
                                    if ( tag.getOnline()!=0) {
                                        alarm.setAlarm_object(Alarm_object.person);
                                        alarm.setAlarm_type(Alarm_Type.sos_offline);
                                        alarm.setName(person.getName());
                                        alarm.setSn(person.getIdcard());
                                        alarm.setMap_key(person.getMap_key());
                                        alarm.setProject_key(person.getProject_key());
                                        alarm.setCreate_time(pushDevice.getLast_time());
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
                                Devicep devicep=devicePMap.get(tag.getMap_key());
                                if (devicep != null) {
                                    devicep.setBt(tag.getBt());
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
              //  myPrintln("计数=" + count );

                String map_key = "";
                String jsonstr = StringUtil.unzip(message.getPayload());
                message.clearPayload();

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
                                return;
                            }
                            JSONObject a = beacons.getJSONObject(key);
                           //     myPrintln("信标" + a);
                            {

                                //  beacon.setX();

                                tag.setX(Double.parseDouble(decimalFormat.format(a.getDouble("x"))));
                                tag.setY(Double.parseDouble(decimalFormat.format(a.getDouble("y"))));
                                //  myPrintln("初始化Y="+beacon.getY());
                                tag.setLastTime(a.getLong("updatedAt") / 1000);
                                tag.setRssi(a.getIntValue("rssi"));
                                tag.setStation_address(a.getString("nearestGateway"));
                                station = (Station) redisUtil.get(redis_key_locator + tag.getStation_address());
                                if (station == null) {
                                    //从数据库读取
                                    Station station1=  Station_sql.getStationByMac(StationMapper,tag.getStation_address());
                                    if (station1!=null){
                                        station=station1;
                                    }
                                    else{
                                     //   myPrintln("不存在于系统的AOA基站"+tag.getStation_address());
                                        return;
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
                                    }

                                }
                            }


                            //这里会有一个过期的缓存
                            Map map = (Map) redisUtil.get(redis_id_map + a.getString("mapId"));
                            if (map != null) {
                                tag.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - Double.parseDouble(decimalFormat.format(a.getDouble("y"))))));
                                tag.setMap_key(map.getMap_key());

                            } else {
                                myPrintln("地图没有缓存");
                                Map_Sql map_sql = new Map_Sql();
                                List<Map>  maps= map_sql.getMapBymapId(mapMapper,station.getUser_key(),station.getProject_key(),a.getString("mapId"));
                                if (maps != null&& !maps.isEmpty()) {
                                    map = maps.get(0);
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
                                    hande_device(tag, deviceps, map);
                                }
                                else if (tag.getBind_type() == 2) {
                                    hander_person(tag, station, map_key, map, deviceps);
                                }
                            }
                        }
                    }
                    if (!deviceps.isEmpty()) {
                        //myPrintln("需要推送的1");
                        sendTagPush(deviceps, map_key);
                        sendRelayPush(deviceps, map_key);
                    } else {
                        //    myPrintln("没有需要推送的"+deviceps);
                    }
                } else if (jsonObject.getString("type").equals("locators")) {
                    JSONObject locators = jsonObject.getJSONObject("data");
                    Set<String> ips = locators.keySet();

                    //  myPrintln("步骤1");
                    for (String ip : ips) {
                        // myPrintln("IP ="+ip);
                        JSONObject a = locators.getJSONObject(ip);
                        if (a != null) {
                            int tag = 0;
                            String address = a.getString("mac").replaceAll(":", "");
                            myPrintln(address);
                            station = (Station) redisUtil.get(redis_key_locator + address);
                           // myPrintln(station.toString());
                            if (station == null) {
                                //从数据库读取
                               Station station1=  Station_sql.getStationByMac(StationMapper,address);
                                if (station1!=null){
                                    myPrintln("shwai");
                                    station=station1;
                                }
                                else{
                                   // myPrintln("不存在于系统的AOA基站，不添加，直接返回");
                                    return;
                                }

                                tag = 1;
                            }
                            JSONObject info = a.getJSONObject("info");

                            station.setX(Double.parseDouble(decimalFormat.format(info.getDouble("x"))));
                            station.setY(Double.parseDouble(decimalFormat.format(info.getDouble("y"))));
                            station.setZ(info.getDouble("z"));
                            myPrintln("mingzi"+station.getName());
                            // myPrintln(check);

                            Map map = (Map) redisUtil.get(redis_id_map + info.getString("mapId"));
                            if (map != null) {
                                station.setMap_key(map.getMap_key());
                                station.setMap_name(map.getName());
                                station.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - station.getY())));
                            } else {
                                myPrintln("地图没有缓存");
                                Map_Sql map_sql = new Map_Sql();
                               List<Map>  maps= map_sql.getMapBymapId(mapMapper,station.getUser_key(),station.getProject_key(),info.getString("mapId"));
                               if (maps != null&& !maps.isEmpty()) {
                                   map = maps.get(0);
                                   station.setMap_key(map.getMap_key());
                                   station.setMap_name(map.getName());
                                   station.setY(Double.parseDouble(decimalFormat.format(map.getHeight() - station.getY())));
                                   redisUtil.setnoTimeOut(redis_id_map + info.getString("mapId"),map);
                               }else{
                                   myPrintln("此基站未有绑定地图,不去修改他的位置Y坐标");
                               }
                               //此基站未有绑定地图,不自动增加

                             //   continue;
                            }
                            if(station.getName()!=null&&station.getName().contains("null")){
                                station.setName(info.getString("name"));
                            }
                            if (station.getOnline() != 1 && a.getBoolean("online")) {
                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                            }
                            if (station.getOnline() == 1 && !a.getBoolean("online")) {
                                Alarm_Sql alarm_sql = new Alarm_Sql();
                                alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_offline, Alarm_object.locator, station.getMap_key(), 0, "", 0, 0, "", station.getName(), station.getAddress(), station.getProject_key(), station.getLast_time()));
                            }
                            station.setOnline(a.getBoolean("online") ? 1 : 0);
                            station.setLast_time(a.getLong("updatedAt") / 1000);
                            if (tag == 1) {
                                Station_sql.updateStation(StationMapper, station);
                            }
                            redisUtil.setnoTimeOut(redis_key_locator + address, station);
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
        // myPrintln("接收时间="+dfs.format(new Date()));
        String data = new String(message.getPayload());
        //   myPrintln("接收消息Qos:" + data);
        time = System.currentTimeMillis() / 1000;// new Date()为获取当前系统时间
        if (data.isEmpty() || !data.contains("pkt_type")) {
            return;
        }

        JSONObject jsonObject = null;
        String pkt_type = null;
        String StationAddress = null;
        Object object = null;

    }

    private void hander_person(Tag tag, Station station, String map_key, Map map, ArrayList<Object> deviceps) {
        Person person = personMap.get(tag.getBind_key());
        // myPrintln("缩放" + map.getProportion());
        person.setX(tag.getX());
        person.setY(tag.getY());
        person.setOnline(1);
        if (map != null) {
            person.setMap_key(map.getMap_key());
            person.setMap_name(map.getName());
        }

        person.setLasttime(tag.getLastTime());
        if (station != null) {
            person.setStation_mac(station.getAddress());
            person.setStation_name(station.getName());
        }
        deviceps.add(person);
        if (person.getMap_key()!=null&&!person.getMap_key().isEmpty()) {
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

        String res = (String) redisUtil.get(person_check_online_status_res + tag.getBind_key());
        // myPrintln("步骤2" + key);
        if (res == null || res.equals("0")) {
            Alarm_Sql alarm_sql = new Alarm_Sql();
            alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(), person.getLasttime()));
        }
        redisUtil.setnoTimeOut(person_check_online_status_res + tag.getBind_key(), "1");
        if (map != null) {
            handleFence(tag, map.getProportion());
        }
        handleSos_AOA(tag);
        handleBt_AOA(tag);
    }

    private void hande_device(Tag tag, ArrayList<Object> deviceps, Map map) {
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
        devicep.setMap_name(map.getName());
        devicep.setX(tag.getX());
        devicep.setY(tag.getY());
        devicep.setLasttime(tag.getLastTime());
        devicep.setNear_s_address(tag.getStation_address());

        station = (Station) redisUtil.get(redis_key_locator + tag.getStation_address());
        // myPrintln(station.toString());
        if (station == null) {
            //从数据库读取
            Station station1=  Station_sql.getStationByMac(StationMapper,tag.getStation_address());
            if (station1!=null){
                myPrintln("shwai");
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

        //    myPrintln("步骤2" + key);
        String res = (String) redisUtil.get(device_check_online_status_res + tag.getBind_key());
        if (res == null || res.equals("0")) {
            Alarm_Sql alarm_sql = new Alarm_Sql();
            alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_online, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
        }
        redisUtil.setnoTimeOut(device_check_online_status_res + tag.getBind_key(), "1");
        handleSos_AOA(tag);
        handleFence(tag, map.getProportion());
        handleBt_AOA(tag);

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
                //    myPrintln("电量记录="+res);
                if((res==null||res.equals("0"))&& tag.getBt()<=2.1){
                    //    myPrintln("保存记录"+res);
                    redisUtil.setnoTimeOut(person_check_bt_status_res + tag.getBind_key(),"1");
                    Alarm_Sql alarm_sql = new Alarm_Sql();
                    Person person=personMap.get(tag.getBind_key());
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_bt, Alarm_object.person, tag.getMap_key(), 0, "", tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
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
                    alarm_sql.addAlarm(alarmMapper, new Alarm(Alarm_Type.sos_run, Alarm_object.device, tag.getMap_key(), 0, "", tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(),devicep.getLasttime()));
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
                            alarm_sql.addAlarm(alarmMapper, new Alarm(fence.getFence_type()==FenceType.OUT?Alarm_Type.fence_on_sos:Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence.getId(), fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
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
                            myPrintln("围栏组异常为空");
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
                                alarm_sql.addAlarm(alarmMapper, new Alarm(fence.getFence_type() == FenceType.OUT ? Alarm_Type.fence_on_sos : Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence.getId(), fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
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
                            alarm_sql.addAlarm(alarmMapper, new Alarm(fence.getFence_type()==FenceType.OUT?Alarm_Type.fence_on_sos:Alarm_Type.fence_out_sos, Alarm_object.person, tag.getMap_key(), person.getFence_id(), fenceMap.get(person.getFence_id()).getName(), tag.getBt(), 0, "", person.getName(), person.getIdcard(), person.getProject_key(),person.getLasttime()));
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
                                  alarm_sql.addAlarm(alarmMapper, new Alarm(fence.getFence_type() == FenceType.OUT ? Alarm_Type.fence_on_sos : Alarm_Type.fence_out_sos, Alarm_object.device, tag.getMap_key(), fence_id, fence.getName(), tag.getBt(), 0, "", devicep.getName(), devicep.getSn(), devicep.getProject_key(), devicep.getLasttime()));
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
                            myPrintln(new Date()+"222-"+ tag.getBind_key() + "触发围栏报警");
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


