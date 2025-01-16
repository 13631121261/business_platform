package com.kunlun.firmwaresystem.mqtt;

import com.google.gson.Gson;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.SpringUtil;
import org.eclipse.paho.client.mqttv3.*;

import java.text.SimpleDateFormat;

public class MessageCallback implements MqttCallback {


    private static int ExpireTime = 60;   // redis中存储的过期时间60s
    Gson gson = new Gson();

    private RedisUtils redisUtil;
    MyMqttClient mqttClient;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    String time;

    public MessageCallback(MyMqttClient mqttClient) {
        this.mqttClient = mqttClient;
        // redisUtil=  (FirmwaresystemApplication)FirmwaresystemApplication.applicationContext.getBean(TestService.class);
        redisUtil = SpringUtil.getBean(RedisUtils.class);

    }

    public void connectionLost(Throwable cause) {

        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连" + cause.getLocalizedMessage());
        mqttClient.start();

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            MqttMessage mqttMessage = token.getMessage();

            // System.out.println("发送状态---------id===" + token.getMessageId());
             /*   if (redisUtil == null) {
                System.out.println("Redis为空");
            } else {

            }*/
        } catch (MqttException e) {
            System.out.println("MQTT发送回调。读取发送状态异常");
        }
    }

    public void messageArrived(String topic, MqttMessage message) {
        mqttClient.executorService.submit(new CallBackHandlers(topic, message));
        // subscribe后得到的消息会执行到这里面
        //   System.out.println("接收消息主题:" + topic);
        //  System.out.println("接收消息Qos:" + message.getQos());
       /* String data=new String(message.getPayload());
         time=df.format(new Date());// new Date()为获取当前系统时间
      //  System.out.println("接收消息内容:" + data);
        if(data.isEmpty()||!data.contains("pkt_type")){
            return;
        }
        Station Station=null;
        JSONObject jsonObject=null;
        String pkt_type=null;
        String StationAddress=null;
        try {
             jsonObject = JSONObject.parseObject(data);
             pkt_type = jsonObject.getString("pkt_type");
             StationAddress = jsonObject.getString("gw_addr");
             Station = (Station) redisUtil.get(redis_key_Station + StationAddress);
            if (Station == null) {
                System.out.println("网关地址有误=" + StationAddress);
                return;
            }
            //上一次离线时间
            if(Station.getOnline()==0){
                redisUtil.set(redis_key_Station_onLine_time+StationAddress,time);
                redisUtil.set(redis_key_Station_onLine_time_count+StationAddress,0);
            }
            else if(  redisUtil.get(redis_key_Station_onLine_time+StationAddress)==null){
                redisUtil.set(redis_key_Station_onLine_time+StationAddress,time);
                redisUtil.set(redis_key_Station_onLine_time_count+StationAddress,0);
            }
             Station.setOnline(1);
            Station.setOnline_txt("在线");
            Station.setLasttime(time);
            redisUtil.set(redis_key_Station + StationAddress,Station);
            Station_sql Station_sql=new Station_sql();
            Station_sql.updateStation(StationMapper,Station);

        }catch (Exception e){
            System.out.println("json格式不对=" + data);
            return;
        }
        Station.setPub_topic(topic);
        if(topic.contains("_")){
            Station.setSub_topic(topic.split("_")[0]+"_sub");
        }else{
            Station.setSub_topic("GwData");
        }
        Object object=null;
      // try {
      //    System.out.println(pkt_type);
           if(!pkt_type.equals(Constant.pkt_type_scan_report)){
               System.out.println("接收消息主题:" + topic);
               System.out.println("接收消息内容:" + data);
           }
            switch (pkt_type) {
                case Constant.pkt_type_response:
                    object= analysisResponse(jsonObject);
                    break;
                case Constant.pkt_type_scan_report:
                    object= analysisScanReport(data);
                    break;
                case Constant.pkt_type_command:

                    break;
                case Constant.pkt_type_state:
                    object=analysisState(jsonObject);
                    break;
            }
      *//*  }catch (Exception e){
            System.out.println("异常="+e.getMessage());
        }*//*
       if(object==null){
           System.out.println("此消息未有解析，请更新服务器");
           return;
       }
        String className=  object.getClass().getSimpleName();

        Station_sql Station_sql;
       // System.out.println("ClassnMame="+className);
        switch (className){
            case Constant.Scan_report:
                String Gaddress = ((Scan_report<Scan_report_data>) object).getGw_addr();
                try {
                    Station = (Station) redisUtil.get(redis_key_Station + Gaddress);
                 //   System.out.println("尝试执行转发");
                    RabbitMessage rabbitMessage=new RabbitMessage(Station.getProject_key(),data);
                    directExchangeProducer.send(rabbitMessage.toString(),transpond);
                    //System.out.println("第一个="+((Scan_report<Scan_report_data>) object).getData().getDev_infos()[0].getAddr());
                    for (Scan_report_data_info scanReportDataInfo : ((Scan_report<Scan_report_data>) object).getData().getDev_infos()) {
                        Station_devices Stations=null;
                        String json=null;
                        try {
                            json = (String) redisUtil.get(redis_key_device_Stations + scanReportDataInfo.getAddr());
                        }catch (Exception e){
                            System.out.println("法国红酒封口的"+e.getMessage());
                        }
                        if (json == null) {
                            Stations=new Station_devices();
                            ArrayList<Station_device> StationDevices=new ArrayList<>();
                            Stations.setStationDevices(StationDevices);
                        } else {
                         try {
                             Stations=new Gson().fromJson(json,Station_devices.class);
                             if (Stations.getStationDevices().size()>= 10) {
                                 Stations.getStationDevices().remove(0);
                             }
                         }catch (Exception e){
                         }
                        }
                        try {
                            if(Station!=null){

                                Stations.getStationDevices().add(new Station_device(Station.getAddress(), scanReportDataInfo.getAddr(), scanReportDataInfo.getRssi(), Station.getSub_topic(), Station.getPub_topic(),Station.getX(),Station.getY(),Station.getName()));
                             //    System.out.println("  X="+Station.getX()+"缓存的设备网关="+Stations.toString());
                                redisUtil.set(redis_key_device_Stations + scanReportDataInfo.getAddr(), Stations.toString());
                            }
                        }catch (Exception e){
                            System.out.println("wewe"+e.getMessage());
                        }
                    }

                    com.kunlun.firmwaresystem.StationJson.type_scan_report.Scan_report scan_report=((Scan_report<Scan_report_data>) object);
                    Scan_report_data scan_report_data=(Scan_report_data)scan_report.getData();
                    String type=scan_report_data.getReport_type();
                    if(type.equals("stuff_card")){
                       Scan_report_data_info[] scanReportDataInfos=scan_report_data.getDev_infos();
                       for(int i=0;i<scanReportDataInfos.length;i++){
                           Ibcn_infos[] ibcn_infos=scanReportDataInfos[i].getIbcn_infos();
                           for(int j=0;j<ibcn_infos.length;j++){

                             *//*  System.out.println("Major="+ibcn_infos[i].getMajor());
                               System.out.println("Minor="+ibcn_infos[i].getMinor());
                               System.out.println("Rssi="+ibcn_infos[i].getRssi());*//*
                           }
                       }
                    }
                    else if(type.equals("adv_only")||type.equals("adv_srp")){
                        Scan_report_data_info[] devices=scan_report_data.getDev_infos();
                        for(Scan_report_data_info device:devices){
                            if(beaconsMap.get(device.getAddr())!=null||wordcard_aMap.get(device.getAddr())!=null) {
                                ArrayList map = (ArrayList<Record>) redisUtil.get(redis_key_tag_map + device.getAddr());
                                if (map == null) {
                                    map = new ArrayList(50);
                                } else {
                                    if (map.size() > 50) {
                                        for (int i = 0; i < map.size() - 49; i++) {
                                            map.remove(0);
                                        }
                                    }
                                }
                                if(device.getIbcn_uuid()!=null&&device.getIbcn_uuid().length()>10){
                                    Beacon beacon=beaconsMap.get(device.getAddr());
                                    if(beacon!=null){
                                        int bt=0;
                                        if(device.getSrp_raw()!=null&&device.getSrp_raw().length()>0){
                                            byte[] btdata= StringUtil.hexToByteArr(device.getSrp_raw());
                                            if(btdata[6]==0x73){
                                                bt=btdata[5];
                                            }
                                            //////需要增加另一个电量识别
                                        }
                                        beacon.setBt(bt);
                                        beacon.setMajor(device.getIbcn_major());
                                        beacon.setUuid(device.getIbcn_uuid());
                                        beacon.setMinor(device.getIbcn_minor());
                                        map.add(new Record(device.getAddr(), beaconsMap.get(device.getAddr()).getName(), StationAddress, Station.getName(), device.getRssi(), beacon));
                                    }
                                }
                                else if(device.getAdv_raw()!=null&device.getAdv_raw().contains("4C4BFFF2")){
                                   // System.out.println("工卡="+device.getAddr());
                                    if(wordcard_aMap==null){
                                        System.out.println("工卡不存在");

                                    }
                                    Wordcard_a wordCard_a=wordcard_aMap.get(device.getAddr());
                                    if(wordCard_a!=null){
                                        byte[] adv=StringUtil.hexToByteArr(device.getAdv_raw());
                                        byte[] ffd= ParseLeAdvData.adv_report_parse(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,adv);
                                        int bt=ffd[8];
                                        int sos=ffd[13];
                                        int run=ffd[15];
                                        wordCard_a.setBt(bt);
                                        wordCard_a.setSos(sos);
                                        wordCard_a.setRun(run);
                                      //  System.out.println(wordCard_a.getMac()+"SOS="+sos+" 运动="+run);
                                        map.add(new Record(device.getAddr(), wordcard_aMap.get(device.getAddr()).getName(), StationAddress, Station.getName(), device.getRssi(), wordCard_a));
                                    }else{
                                        System.out.println("工卡内容为空");
                                    }
                                }
                                redisUtil.set(redis_key_tag_map + device.getAddr(), map);

                            }








                            *//*if(device.getIbcn_uuid()!=null&&device.getIbcn_uuid().length()>10){
                           //     System.out.println("Rssi="+beacon.getIbcn_rssi_at_1m());
                                if(beaconsMap.get(device.getAddr())!=null){
                                   ArrayList map=(ArrayList<Record>) redisUtil.get(redis_key_tag_map+device.getAddr());
                                   if(map==null){
                                       map=new ArrayList(50);
                                   }
                                   else{
                                       if(map.size()>50){
                                        for(int i=0;i<map.size()-49;i++){
                                            map.remove(0);
                                        }
                                       }
                                   }
                                   map.add(new Record(device.getAddr(),beaconsMap.get(device.getAddr()).getName(),StationAddress,Station.getName(),device.getRssi(),new Beacon(device.getAddr(),device.getIbcn_uuid(),device.getIbcn_major(),device.getIbcn_minor(),device.getIbcn_rssi_at_1m()),device.getSrp_raw()));
                                   redisUtil.set(redis_key_tag_map+device.getAddr(),map);
                                }
                            }
                            else if(device.getAdv_raw()!=null&device.getAdv_raw().contains("4C4BFFF2")){
                                    if(wordcard_aMap.get(device.getAddr())!=null){
                                        ArrayList map=(ArrayList<Record>) redisUtil.get(redis_key_tag_map+device.getAddr());
                                        if(map==null){
                                            map=new ArrayList(50);
                                        }
                                        else{
                                            if(map.size()>50){
                                                for(int i=0;i<map.size()-49;i++){
                                                    map.remove(0);
                                                }
                                            }
                                        }
                                    }
                            }*//*
         *//*     if（）{

                            }*//*
                        }
                    }
                }catch (Exception e){
                    System.out.println("异常"+e);
                }

            break;
            case Constant.ConnectExecute:
                redisUtil.set("sendToStation_id="+ ((ConnectExecute<ConnectExecuteDetail>)object).getData().getMsgId(),((ConnectExecute<ConnectExecuteDetail>)object).getData().isResult());
                //  Util.add_user_device_one();
                break;
            case Constant.ConnectState:
               // ((ConnectState<ConnectDetail>)object).getData().getDevice_state();
                redisUtil.set(ConnectState+((ConnectState<ConnectDetail>)object).getData().getDevice_addr(),((ConnectState<ConnectDetail>)object).getData().getDevice_state());
                System.out.println("连接状态="+((ConnectState<ConnectDetail>)object).getData().getDevice_state());
                //准备就绪，推送
                if(((ConnectState<ConnectDetail>)object).getData().getDevice_state().equals(ConnectState_redy)){
                    System.out.println("连接状态已就绪  开始推送至rabittmq=");
                }

                break;
            case Constant.Scan_filter:
               // System.out.println("进入两次");
                //取出网关
                Station = (Station) redisUtil.get(redis_key_Station + ((Scan_filter )object).getGw_addr());
               // System.out.println("缓存");
                Station.Filter_name1(((Scan_filter )object).getData().getFilter_name());
                Station.Filter_ibeacon1(((Scan_filter )object).getData().isFilter_beacon_b());
                Station.Filter_companyId1(((Scan_filter )object).getData().getFilter_comp_ids());
                Station.setFilter_rssi(((Scan_filter )object).getData().getFilter_rssi()+"");
                Station.setFilter_uuid(((Scan_filter )object).getData().getFilter_uuid());
               // System.out.println("过滤的UUID==="+((Scan_filter )object).getData().getFilter_uuid());
                //更新后再次缓存
                redisUtil.set(redis_key_Station + ((Scan_filter )object).getGw_addr(),Station);
                //存到数据库 同步
                 Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
               break ;
            case Constant.Scan_params:
                 Station = (Station) redisUtil.get(redis_key_Station + ((Scan_params )object).getGw_addr());
                Station.Scan_out1(((Scan_params)object).getData().isReport_onoff());
                Station.Report_type1(((Scan_params)object).getData().isRequest_onoff());
                Station.Scan_interval(((Scan_params)object).getData().getReport_interval());
                //更新后再次缓存
                redisUtil.set(redis_key_Station + ((Scan_params )object).getGw_addr(),Station);
                //存到数据库 同步
                 Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
                break;
            case Constant.Adv_params:
                 Station=(Station)redisUtil.get(redis_key_Station+((Adv_params)object).getGw_addr());
                Station.Broadcast1(((Adv_params)object).getData().isAdv_onoff());
                redisUtil.set(redis_key_Station + ((Adv_params )object).getGw_addr(),Station);
                 Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
                break;
            case Constant.WifiVersion:
                Station=(Station)redisUtil.get(redis_key_Station+((WifiVersion)object).getGw_addr());
                Station.setWifi_version(((WifiVersion<WifiVersionDetail>)object).getData().getVersion());
                redisUtil.set(redis_key_Station + ((WifiVersion )object).getGw_addr(),Station);
                 Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
                break;
            case Constant.BleVersion:
                Station=(Station)redisUtil.get(redis_key_Station+((BleVersion)object).getGw_addr());
                Station.setBle_version(((BleVersion<BleVersionDetail>)object).getData().getVersion());
                redisUtil.set(redis_key_Station + ((BleVersion )object).getGw_addr(),Station);
                Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
                break;
            case Constant.App_Server:
                Station=(Station)redisUtil.get(redis_key_Station+((App_Server)object).getGw_addr());
                if(Station==null){
                    return;
                }
                Station.setSub_topic(((App_Server) object).getData().getSub());
                Station.setIp((((App_Server) object).getData().getHost()));
                Station.setPub_topic(((App_Server) object).getData().getPub());
                redisUtil.set(redis_key_Station + ((App_Server )object).getGw_addr(),Station);
                Station_sql=new Station_sql();
                Station_sql.updateStation(StationMapper,Station);
                break;
        }
      //  System.out.println(StationHand.getPkt_type());
    }
//解析全部的获取状态
    private Object analysisResponse(JSONObject jsonRaw){

        Type type =null;
        try {
        JSONObject data=jsonRaw.getJSONObject("data");
        String resp=data.getString("resp");
      //  System.out.println("具体的头="+resp);
        switch (resp){
            case response_sys_get_ver:
                type= new TypeToken<BleVersion<BleVersionDetail>>(){}.getType();
                BleVersion<BleVersionDetail> Ble_version=gson.fromJson(jsonRaw.toString(),type);
                System.out.println("蓝牙版本号=" + Ble_version.getData().getVersion());
                return Ble_version;
                //下发连接的状态
            case response_sys_get_wifi_ver:
                type= new TypeToken<WifiVersion<WifiVersionDetail>>(){}.getType();
                WifiVersion<  WifiVersionDetail>   Wifi_version=gson.fromJson(jsonRaw.toString(),type);
                System.out.println("WIfi版本号=" +   Wifi_version.getData().getVersion());
                return   Wifi_version;


            case response_conn_addr_request:
                type= new TypeToken<ConnectExecute<ConnectExecuteDetail>>(){}.getType();
                ConnectExecute<ConnectExecuteDetail> connectExecute=gson.fromJson(jsonRaw.toString(),type);
                System.out.println("连接执行状态=" + connectExecute.getData().isResult());
                return connectExecute;
            case response_scan_filter_get:
                Scan_filterDetail scanFilterDetail=new Scan_filterDetail(jsonRaw.getJSONObject("data").toString());
                Scan_filter scan_filter=new Scan_filter();
                scan_filter.setData(scanFilterDetail);
                scan_filter.setGw_addr(jsonRaw.getString("gw_addr"));
                scan_filter.setPkt_type(jsonRaw.getString("pkt_type"));
                scan_filter.setTime(jsonRaw.getString("time"));
                return scan_filter;

            case response_scan_params_get:
                try{
                   // System.out.println("log");
                    Scan_paramsDetail scan_paramsDetail=new Scan_paramsDetail(jsonRaw.getJSONObject("data").toString());
                  //  System.out.println("log1222");
                    Scan_params scanParams=new Scan_params();
                  //  System.out.println("333");
                    scanParams.setData(scan_paramsDetail);
                  //  System.out.println("444");
                    scanParams.setGw_addr(jsonRaw.getString("gw_addr"));
                  //  System.out.println("555");
                    scanParams.setPkt_type(jsonRaw.getString("pkt_type"));
                   // System.out.println("666");
                    scanParams.setTime(jsonRaw.getString("time"));
                   // System.out.println("777");
                    return scanParams;
                }
                catch (Exception e){
                    System.out.println("response_scan_params_get异常输出="+e.toString());
                    return null;
                }
            case response_adv_params_get:
                Adv_paramsDetail adv_paramsDetail=new Adv_paramsDetail(jsonRaw.getJSONObject("data").toString());
                Adv_params adv_params=new Adv_params();
                adv_params.setData(adv_paramsDetail);
                adv_params.setGw_addr(jsonRaw.getString("gw_addr"));
              //  System.out.println("555");
                adv_params.setPkt_type(jsonRaw.getString("pkt_type"));
              //  System.out.println("666");
                adv_params.setTime(jsonRaw.getString("time"));
                return adv_params;
            case sys_app_server:
                App_ServerDetail app_serverDetail=new App_ServerDetail(jsonRaw.getJSONObject("data").toString());
                App_Server app_server=new App_Server();
                app_server.setData(app_serverDetail);
                app_server.setGw_addr(jsonRaw.getString("gw_addr"));
                app_server.setPkt_type(jsonRaw.getString("pkt_type"));
                app_server.setTime(jsonRaw.getString("time"));
                return app_server;



        }
        }catch (Exception e){
            System.out.println("解析异常=" + jsonRaw.toString());
        }
        return null;
    }

    private Object analysisState(JSONObject jsonRaw){
        Type type =null;
        JSONObject data=null;
        String state=null;
        try {
             data=jsonRaw.getJSONObject("data");
            state =data.getString("state");
          //  System.out.println("具体的头111="+state);

            switch (state){
                case state_sta_gw_hb :
                    type= new TypeToken<HeartState<HeartDetail>>(){}.getType();
                    HeartState<HeartDetail> heartState=gson.fromJson(jsonRaw.toString(),type);
                    System.out.println(heartState.getGw_addr()+"心跳状态=" + heartState.getData().getTicks_cnt());

                   Station Station=(Station) redisUtil.get(redis_key_Station+heartState.getGw_addr());
                   String synStr=(String)redisUtil.get(redis_key_project_sys+Station.getAddress());
                   System.out.println("当前心跳="+heartState.getData().getTicks_cnt());
                    Integer heart=(Integer) redisUtil.get(redis_key_project_heart+Station.getAddress());
                    if(heart!=null){
                        System.out.println("记录心跳心跳="+heart.intValue());
                    }
                    if(synStr!=null&&synStr.contains("upgrade")){
                        if(heart!=null&&Math.abs(heartState.getData().getTicks_cnt()-heart)<10){
                            System.out.println("刚更新过固件，不再执行");
                            return heartState;
                        }
                    }
                    Station_config project=projectMap.get(Station.getProject_key());
                   if(Station!=null){
                       redisUtil.set(redis_key_Station+heartState.getGw_addr(),Station);
                      // System.out.println("网关配置="+Station.toString());
                      // System.out.println("项目配置="+project.toString());
                       if(project!=null){
                           List<String> cmds=new ArrayList<>();
                       //    System.out.println("项目不为空");
                           String cmd="";
                     //      System.out.println("scan_filter_comp_ids");


                            if(  isChange(project.getFilter_companyId(),Station.getFilter_companyId())){
                                cmd= getParamsJson("scan_filter_comp_ids",project,Station.getAddress(),null,null);
                                cmds.add(cmd);
                            }
                           if(  isChange(project.getFilter_rssi(),Station.getFilter_rssi())){
                               cmd= getParamsJson("scan_filter_comp_ids",project,Station.getAddress(),null,null);
                               cmds.add(cmd);
                           }
                           if(  isChange(project.getFilter_rssi(),Station.getFilter_rssi())){
                               cmd= getParamsJson("scan_filter_rssi",project,Station.getAddress(),null,null);
                               cmds.add(cmd);
                           }
                          *//*  if(!project.getFilter_companyId().equals(Station.getFilter_companyId())){
                                isChange();
                                cmd= getParamsJson("scan_filter_comp_ids",project,Station.getAddress(),null,null);
                            }*//*
         *//*   System.out.println("scan_filter_rssi");
                            if (!project.getFilter_rssi().equals(Station.getFilter_rssi())){

                                cmd= getParamsJson("scan_filter_rssi",project,Station.getAddress(),null,null);
                            }*//*

                           if(  isChange(project.getFilter_uuid(),Station.getFilter_uuid())){
                               cmd= getParamsJson("scan_filter_ibcn_uuid",project,Station.getAddress(),null,null);
                               cmds.add(cmd);
                           }
                        *//*   System.out.println("scan_filter_ibcn_uuid");
                            if(!project.getFilter_uuid().equals(Station.getFilter_uuid())){
                                cmd= getParamsJson("scan_filter_ibcn_uuid",project,Station.getAddress(),null,null);
                            }*//*

                         //  System.out.println("adv_onoff");
                            if(project.getBroadcast()!=Station.getBroadcast()){
                                cmd= getParamsJson("adv_onoff",project,Station.getAddress(),null,null);
                                cmds.add(cmd);
                            }
                         //  System.out.println("scan_filter_ibcn_dev");
                            if(project.getFilter_ibeacon()!=Station.getFilter_ibeacon()){
                                cmd= getParamsJson("scan_filter_ibcn_dev",project,Station.getAddress(),null,null);
                                cmds.add(cmd);
                            }
                         //  System.out.println("scan_request_onoff");
                            if(project.getReport_type()!=Station.getReport_type()){
                                cmd= getParamsJson("scan_request_onoff",project,Station.getAddress(),null,null);
                                cmds.add(cmd);
                            }
                         //  System.out.println("scan_report_onoff");

                           if(  isChange(project.getFilter_name(),Station.getFilter_name())){
                               cmd= getParamsJson("scan_filter_name",project,Station.getAddress(),null,null);
                               cmds.add(cmd);
                           }
                        *//*   System.out.println("scan_filter_name");
                            if(!project.getFilter_name().equals(Station.getFilter_name())){
                                cmd= getParamsJson("scan_filter_name",project,Station.getAddress(),null,null);
                            }*//*
                         //  System.out.println("scan_report_interval"+project.getScan_interval());
                          // System.out.println("scan_report_interval"+Station.getScan_interval());
                            if(project.getScan_interval()!=Station.getScan_interval()){
                                cmd= getParamsJson("scan_report_interval",project,Station.getAddress(),null,null);
                                cmds.add(cmd);
                            }
                          //  System.out.println("setTopic");
                          *//* if(!project.getSub_topic().equals(Station.getSub_topic())){
                               cmd= getParamsJson("setTopic",project,Station.getAddress(),null,null);
                           }*//*
                           System.out.println(project.getSub_topic()+"    " +Station.getSub_topic());
                           if(  isChange(project.getSub_topic(),Station.getSub_topic())){
                                if(!Station.getSub_topic().contains(project.getSub_topic())){
                                    cmd= getParamsJson("setTopic",project,Station.getAddress(),null,null);
                                    cmds.add(cmd);
                                }
                           }else{

                           }
                          //  System.out.println("bleVersion"+project.getBle_version());
                        //   System.out.println("bleVersion"+Station.getBle_version());
                           if(  isChange(project.getBle_version(),Station.getBle_version())){
                               if(Station.getBle_version()!=null&&!Station.getBle_version().equals(""))
                               {
                                   redisUtil.set(redis_key_project_heart + Station.getAddress(), heartState.getData().getTicks_cnt());
                                   Ble ble = new Ble();
                                   // System.out.println("bleVersionaaa");
                                   Ble_firmware ble_firmware = ble.getVersionByKey(bleMapper, project.getCustomer_key(), project.getBle_version());
                                   cmd = getParamsJson("bleVersion", project, Station.getAddress(), ble_firmware, null);
                                   cmds.add(cmd);
                               }
                           }
                          *//*
                            if((project.getBle_version()!=null&&Station.getBle_version()==null)||(project.getBle_version()!=null&&!Station.getBle_version().equals(project.getBle_version()))){
                                Ble ble=new Ble();
                                System.out.println("bleVersionaaa");
                                Ble_firmware ble_firmware= ble.getVersionByKey(bleMapper,project.getCustomer_key(),project.getBle_version());
                                System.out.println("bleVersionbbbb");
                                cmd= getParamsJson("bleVersion",project,Station.getAddress(),ble_firmware,null);
                                System.out.println("bleVersioncccc");
                            }*//*
                        //   System.out.println("WifiVersion");
                           if(  isChange(project.getWifi_version(),Station.getWifi_version())){
                               if(Station.getWifi_version()!=null&&!Station.getWifi_version().equals("")){
                                   redisUtil.set(redis_key_project_heart+Station.getAddress(),heartState.getData().getTicks_cnt());
                                   Wifi wifi=new Wifi();
                                   Wifi_firmware wifi_firmware= wifi.getVersionByKey(wifiMapper,project.getCustomer_key(),project.getWifi_version());
                                   cmd= getParamsJson("wifiVersion",project,Station.getAddress(),null,wifi_firmware);
                                   cmds.add(cmd);
                               }
                           }
                           if(project.getScan_out()!=Station.getScan_out()){
                               cmd= getParamsJson("scan_report_onoff",project,Station.getAddress(),null,null);
                               cmds.add(cmd);
                           }
                       *//*    System.out.println("wifiVersion");
                           if((project.getWifi_version()!=null&&Station.getWifi_version()==null)||(project.getWifi_version()!=null&&!Station.getWifi_version().equals(project.getWifi_version()))){
                               Wifi wifi=new Wifi();
                               Wifi_firmware wifi_firmware= wifi.getVersionByKey(wifiMapper,project.getCustomer_key(),project.getWifi_version());
                               cmd= getParamsJson("wifiVersion",project,Station.getAddress(),null,wifi_firmware);
                           }*//*
                            //当心跳包来的时候，检查网关与项目的配置，不一致的话就更新网关
                            if( cmds.size()>0){
                                for(String cmddata:cmds){
                                    String topic=Station.getSub_topic();
                                    if(!topic.equals("SrvData")&&!topic.contains(Station.getAddress())){
                                        topic=topic+"/"+Station.getAddress();
                                    }
                                    redisUtil.set(redis_key_project_sys+Station.getAddress(),cmddata);
                                    System.out.println("发送的指令="+cmddata+"==="+topic);
                                    RabbitMessage rabbitMessage=new RabbitMessage(topic,cmddata);
                                    directExchangeProducer.send(rabbitMessage.toString(),go_to_connect);
                                }

                            }
                            else{
                                redisUtil.set(redis_key_project_sys+Station.getAddress(),"ok");
                                System.out.println("网关:"+Station.getName()+"同步完成了");
                            }
                       }
                       else{
                           System.out.println("项目是空的");
                           /////////后续需要做log，把log文件管理器
                       }
                   }
                    return heartState;
                case state_sta_device_state :
                    type= new TypeToken<ConnectState<ConnectDetail>>(){}.getType();
                    ConnectState<ConnectDetail> connectState=gson.fromJson(jsonRaw.toString(),type);
                    System.out.println("连接状态=" + connectState.getData().getDevice_state());
                    return connectState;
            }
    }catch (Exception e){
        System.out.println("222解析异常=" +jsonRaw.toString());
    }
        return null;
    }
private boolean isChange(String p,String g){
        if(p!=null&&p.equals("null")){
            return false;
        }
        if(p!=null&&p.contains("und")&&p.contains("Un")){
            return false;
        }
        if(p==null){
            return false;
        }else if(g==null&&p.length()>0){
            return true;
        }else if(p.length()>0&&!p.equals(g)){
            return true;
        }


        return false;
}
    //解析全部的主动扫描上报
private Scan_report analysisScanReport(String jsonRaw){
        try {
            Type type = new TypeToken<Scan_report<Scan_report_data>>(){}.getType();
            Scan_report<Scan_report_data> scan=gson.fromJson(jsonRaw,type);
         //   System.out.println(scan.getData().getDev_infos()[0].getAddr());
            return scan;
        }
        catch (Exception e)
        {
            System.out.println("解析扫描上报数据异常");
            return null;
        }
    }


    private String getParamsJson(String type, Station_config project, String address, Ble_firmware ble_firmware, Wifi_firmware wifi_firmware){
        String cmd="";
        switch (type){
            case "scan_report_interval":
                 cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_report_interval\", \"value\": "+project.getScan_interval()+"}}";
                break;
            case "scan_report_onoff":
                if(project.getScan_out()==1){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_report_onoff\", \"enable\": true}}";
                }else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_report_onoff\", \"enable\": false}}";
                }
                break;
            case "scan_request_onoff":
                if(project.getReport_type()==2){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_request_onoff\", \"enable\": true}}";
                }
                else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_request_onoff\", \"enable\": false}}";
                }
                break;
            case "scan_filter_ibcn_dev":
                if(project.getFilter_ibeacon()==1){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address +"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_ibcn_dev\", \"enable\": true}}";
                }
            else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address +"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_ibcn_dev\", \"enable\": false}}";
                }
                break;
            case "adv_onoff":
                if(project.getBroadcast()==1){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"adv_onoff\", \"enable\": true}}";
                }
                else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"adv_onoff\", \"enable\": false}}";
                }
                break;
            case"scan_filter_ibcn_uuid":
                if(project.getFilter_uuid()!=null&&project.getFilter_uuid().length()>0){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_ibcn_uuid\", \"enable\": true, \"value\": \""+project.getFilter_uuid()+"\"}}";
                }
                else{
                    cmd=  "{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_ibcn_uuid\", \"enable\": false}}";
                }
                break;
            case "scan_filter_name":
                String[] names=project.getFilter_name().split("-");

                if(names!=null&&names.length>=2){
                     cmd="{\"pkt_type\": \"command\",\"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4,\"cmd\": \"scan_filter_name\",\"enable\": true,\"num\":"+(names.length-1)+",\"value\":[";
                    String ss="";
                    for(int i=1;i<names.length;i++){
                        if(i==1){
                            if(i==names.length-1){
                                ss=ss+"{\"name\":\""+names[i]+"\"}]}}";
                            }
                            else{
                                ss="{\"name\":\""+names[i]+"\"}";
                            }
                        }else{
                            if(i==names.length-1){
                                ss=ss+",{\"name\":\""+names[i]+"\"}]}}";
                            }else{
                                ss=ss+",{\"name\":\""+names[i]+"\"}";
                            }
                        }

                    }
                    cmd=cmd+ss;
                }
                else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_name\", \"enable\": false, \"num\": 0}}";
                }
                break;

            case "scan_filter_comp_ids":
                String[] companyids=project.getFilter_companyId().split("-");

                if(companyids!=null&&companyids.length>=2){
                    cmd="{\"pkt_type\": \"command\",\"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4,\"cmd\": \"scan_filter_comp_ids\",\"enable\": true,\"num\":"+(companyids.length-1)+",\"value\":[";
                    String ss="";
                    for(int i=1;i<companyids.length;i++){
                        if(i==1){
                            if(i==companyids.length-1){
                                ss=ss+"{\"id\":\""+companyids[i]+"\"}]}}";
                            }
                            else{
                                ss="{\"id\":\""+companyids[i]+"\"}";
                            }
                        }else{
                            if(i==companyids.length-1){

                                ss=ss+",{\"id\":\""+companyids[i]+"\"}]}}";
                            }else{

                                ss=ss+",{\"id\":\""+companyids[i]+"\"}";
                            }
                        }

                    }
                    cmd=cmd+ss;
                }
                else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_comp_ids\", \"enable\": false, \"num\": 0}}";
                }
                break;

            case "scan_filter_rssi":
                if(project.getFilter_rssi()!=null&&project.getFilter_rssi().length()>0&&!project.getFilter_rssi().equals("无过滤")){
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_rssi\", \"enable\": true, \"value\": "+project.getFilter_rssi()+"}}";
                }
                else{
                    cmd="{\"pkt_type\": \"command\", \"gw_addr\": \""+address+"\", \"data\": {\"msgId\": 4, \"cmd\": \"scan_filter_rssi\", \"enable\": false}}";
                }
                break;
            case "bleVersion":
                cmd="{\"pkt_type\":\"command\",\n" +
                        "\"gw_addr\":\""+address+"\",\n" +
                        "\"data\":{\"msgId\":1234,\n" +
                        "\"cmd\":\"sys_upgrade_ble\",\n" +
                        "\"url\":\""+  ble_firmware.getUrl()+"?userKey="+ble_firmware.getCustomer_key()+"&version="+ble_firmware.getVersion()+"&type=ble"+"\",\n" +
                        "\"reload_default\":false\n" +
                        "}}";
                break;
            case "wifiVersion":
                cmd="{\"pkt_type\":\"command\",\n" +
                        "\"gw_addr\":\""+address+"\",\n" +
                        "\"data\":{\"msgId\":1234,\n" +
                        "\"cmd\":\"sys_upgrade_wifi\",\n" +
                        "\"url\":\""+  wifi_firmware.getUrl()+"?userKey="+wifi_firmware.getCustomer_key()+"&version="+wifi_firmware.getVersion()+"&type=wifi"+"\",\n" +
                        "\"reload_default\":false\n" +
                        "}}";
                break;
            case "setTopic":
                cmd="{\"pkt_type\":\"command\",\n" +
                        "\"gw_addr\":\""+address+"\",\n" +
                        "\"data\":{\"msgId\":1234,\n" +
                        "\"cmd\":\"sys_app_server\",\n" +
                        "\"op\":\"set\",\n" +
                        "\"type\":\"MQTT\",\n" +
                        "\"port\":1883,\n" +
                        "\"host\":\""+host+"\",\n" +
                        "\"mqtt\":{\n" +
                        "\"pub\":\""+project.getPub_topic()+"\",\n" +
                        "\"sub\":\""+project.getSub_topic()+"/"+address+"\",\n" +
                        "\"usr\":\"\",\n" +
                        "\"pw\":\"\",\n" +
                        "\"clientId\":\""+address+"\",\n" +
                        "\"qos\":1\n" +
                        "}\n" +
                        "}\n" +
                        "}";

        }
        project=null;
        return cmd;


    }
*/


    }


}