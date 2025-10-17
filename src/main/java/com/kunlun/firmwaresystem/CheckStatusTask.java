package com.kunlun.firmwaresystem;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.check.Check_alarm;
import com.kunlun.firmwaresystem.entity.check.DevStatus;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.CheckAlarmMapper;
import com.kunlun.firmwaresystem.mappers.LogsMapper;
import com.kunlun.firmwaresystem.mappers.ProjectMapper;
import com.kunlun.firmwaresystem.mqtt.MyMqttClient;
import com.kunlun.firmwaresystem.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
    @Component
    public class CheckStatusTask {
        @Resource
        private ProjectMapper projectMapper;
        @Resource
        private LogsMapper logsMapper;
        @Resource
        private CheckAlarmMapper checkAlarmMapper;

        @Resource
        private PersonService personService;

        @Resource
        private StationService stationService;

        @Resource
        private DevicepService devicepService;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");//设置日期格式1

        long time=0;
        //30秒执行一次
        @Scheduled(cron = "*/30 * * * * ?")
        public void execute() throws Exception {
            time++;
            checkStation();
         //   checkBeacon();
            checkPerson();
            checkDevice();
         //   System.out.println("30秒清空一次数据"+time);
            deleteCache();
            if(time%3==0){
                //for(String key:myMqttClientMap.keySet()){
                  // MyMqttClient client= myMqttClientMap.get(key);
                   //if (client != null) {
              //  System.out.println("66666"+mqttClient.getStatus());
                       if (!mqttClient.getStatus()){
                           myPrintln("真实重连 MQTT  定时检测");
                           new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   myPrintln("真实重连 MQTT  定时检测");
                                   mqttClient.start();
                               }
                           }).start();

                       }
                  // }
                //}
            }

        }
        @Scheduled(cron = "0 58 15 * * ?")
        public void execute1() throws Exception {
            System.out.println("定时在运行");
            checkSOS();
        }
        //检测AOA基站
        private void checkStation(){
            Map<String, DevStatus> devStatusMap=new HashMap<>();
            Station_sql stationSql=new Station_sql();
            List<Station> stations=new ArrayList<>();
            for(String address:station_maps.keySet()){
                Station station=(Station) redisUtil.get(redis_key_locator+address);
                if(station!=null){
                    stations.add(station);
                //    stationSql.updateStation(stationMapper,station);
                    String project_key=station.getProject_key();
                    DevStatus locatorStatus= devStatusMap.get(project_key);
                    if(locatorStatus==null){
                        locatorStatus=new DevStatus();
                    }
                    if(station.getOnline()==1){
                        locatorStatus.addOnLine(station);
                    }else{
                        locatorStatus.addOffLine(station);
                    }
                    devStatusMap.put(project_key,locatorStatus);
                }
            }
            time=System.currentTimeMillis();
            stationService.saveOrUpdateBatch(stations);
            myPrintln("基站结束时间-"+(System.currentTimeMillis()-time));
            for(String project_key:devStatusMap.keySet()){
             //   myPrintln("循环="+devStatusMap.get(project_key));
                redisUtil.set(redis_key_locator_project+project_key,devStatusMap.get(project_key));
            }
        }

        //检测信标
       /* private void checkBeacon(){
            Map<String, DevStatus> devStatusMap=new HashMap<>();
            for(String address:beaconsMap.keySet()){
                Beacon  beacon =beaconsMap.get(address);
                if(beacon!=null){
                    String project_key=beacon.getProject_key();
                    DevStatus devStatus=devStatusMap.get(project_key);
                    if(devStatus==null){
                        devStatus=new DevStatus();
                    }
                    if(beacon.getOnline()==1){
                        devStatus.addOnLine(beacon);
                    }else{
                        devStatus.addOffLine(beacon);
                    }
                    devStatusMap.put(project_key,devStatus);
                }
            }
            for(String project_key:devStatusMap.keySet()){
                redisUtil.set(redis_key_beacon_project+project_key,devStatusMap.get(project_key));
            }
        }*/
        //检测人员
        @Transactional(rollbackFor = Exception.class)
        public void checkPerson(){
            Map<String, DevStatus> devStatusMap=new HashMap<>();

            for(String idcard:personMap.keySet()){
                Person  person =personMap.get(idcard);
                if(person!=null){
                    String project_key=person.getProject_key();
                    DevStatus devStatus=devStatusMap.get(project_key);
                    if(devStatus==null){
                        devStatus=new DevStatus();
                    }
                    if(person.getOnline()==1){
                        devStatus.addOnLine(person);
                    }else{
                        devStatus.addOffLine(person);
                    }
                    devStatusMap.put(project_key,devStatus);
                }
            }


            for(String project_key:devStatusMap.keySet()){
                redisUtil.set(redis_key_person_project+project_key,devStatusMap.get(project_key));
            }
        }
        //检测资产
        private void checkDevice(){
            Map<String, DevStatus> devStatusMap=new HashMap<>();
            List< Devicep> deviceps=new ArrayList<>();
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            for(String sn:devicePMap.keySet()){
                Devicep devicep =devicePMap.get(sn);
                if(devicep!=null){
                   // deviceP_sql.update(devicePMapper,devicep);
                    deviceps.add(devicep);
                    String project_key=devicep.getProject_key();
                    DevStatus devStatus=devStatusMap.get(project_key);
                    if(devStatus==null){
                        devStatus=new DevStatus();
                    }
                    if(devicep.getOnline()==1){
                        devStatus.addOnLine(devicep);
                    }else{
                        devStatus.addOffLine(devicep);
                    }
                    devStatusMap.put(project_key,devStatus);
                }
            }
            devicepService.saveOrUpdateBatch(deviceps);
            for(String project_key:devStatusMap.keySet()){
                redisUtil.set(redis_key_device_project+project_key,devStatusMap.get(project_key));
            }
        }

        //每天人员报警保存
        private void checkSOS(){
            long time=System.currentTimeMillis()/1000;
            long one_time=time-3600;
            Alarm_Sql alarm_sql=new Alarm_Sql();
            Project_Sql project_sql=new Project_Sql();
            List<Project>  projectList= project_sql.getAllProject(projectMapper);
            for(Project project:projectList){
                List<Alarm> alarms=  alarm_sql.selectByOneHour(alarmMapper,project.getProject_key(),one_time);
                int all=alarms.size();
                int key_sum=0;
                int run_sum=0;
                int fence_on_sum=0;
                int fence_out_sum=0;
                int offline_sum=0;
                int online_sum=0;
                if(all>0){
                    for(Alarm alarm:alarms){
                        switch (alarm.getAlarm_type()){
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
                        }
                    }
                }
                CheckAlarm_Sql checkAlarm_sql=new CheckAlarm_Sql();
                Check_alarm check_alarm=new Check_alarm();
                check_alarm.setCreate_time(System.currentTimeMillis()/1000);
                check_alarm.setSum(key_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.all);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
                check_alarm.setSum(run_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.sos_run);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
                check_alarm.setSum(fence_out_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.fence_out_sos);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
                check_alarm.setSum(fence_on_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.fence_on_sos);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
                check_alarm.setSum(offline_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.sos_offline);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
                check_alarm.setSum(online_sum);
                check_alarm.setProject_key(project.getProject_key());
                check_alarm.setAlarm_type(Alarm_Type.sos_online);
                checkAlarm_sql.addCheckAlarm(checkAlarmMapper,check_alarm);
            }
        }
        //每天人员报警保存
        private void deleteCache(){
            long time=System.currentTimeMillis()/1000;
            long one_time=time-2592000;
            History_Sql history_sql=new History_Sql();
            history_sql.deleteBy15Day(historyMapper,one_time*1000);
            Logs_Sql logs_sql=new Logs_Sql();
            logs_sql.deleteBy15Day(logsMapper,one_time);
            Alarm_Sql alarm_sql=new Alarm_Sql();
            alarm_sql.deleteBy15Day(alarmMapper,one_time);
        }






}
