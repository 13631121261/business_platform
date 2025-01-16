package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONObject;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.entity.Map;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.RedisUtils;
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
public class HistoryControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private AlarmMapper alarmMapper;
    @RequestMapping(value = "userApi/History/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getHistory(HttpServletRequest request) {
        String type=request.getParameter("history_type");
        String sn=request.getParameter("quickSearch");
        String start_time=request.getParameter("start_time");
        String stop_time=request.getParameter("stop_time");
        Customer customer=getCustomer(request);
        try {
            History_Sql history_sql = new History_Sql();
            List<History> historyList = history_sql.getHistory(historyMapper, sn, type, Long.parseLong(start_time)*1000, Long.parseLong(stop_time)*1000, customer.getProject_key());
            List<MHistory> histories = new ArrayList<>();
            for (int i = 0; i < historyList.size(); i++) {
                System.out.println(historyList.get(i));
                if (i == 0) {
                    MHistory mHistory = new MHistory();
                    mHistory.setStart_time(historyList.get(i).getTime());
                    mHistory.setMap_key(historyList.get(i).getMap_key());
                    mHistory.setSn(historyList.get(i).getSn());
                    mHistory.setName(historyList.get(i).getName());
                    Map_Sql map_sql = new Map_Sql();
                    Map m = map_sql.getMapByMapkey(mapMapper, historyList.get(i).getMap_key());
                    mHistory.setMap_name(m.getName());
                    mHistory.setMap_data(m.getData());
                    mHistory.addHistory(historyList.get(i));
                    mHistory.setId(1);
                    histories.add(mHistory);
                    System.out.println("首次=" + i);

                } else {
                    if(i==historyList.size()-1){
                        MHistory mHistory = histories.get(histories.size() - 1);
                        mHistory.setStop_time(historyList.get(i).getTime());
                    }


                    //没有换地图
                    if (i != 0 && historyList.get(i).getMap_key().equals(historyList.get(i - 1).getMap_key())) {
                        //没有离线
                        if (i != 0 && (historyList.get(i).getTime() - historyList.get(i - 1).getTime() <= 10*1000)) {
                            MHistory mHistory = histories.get(histories.size() - 1);
                            mHistory.addHistory(historyList.get(i));
                          //  System.out.println("正常=" + i);
                        }//连续10秒没有接收到信号，判断为重新上线
                        else if (i != 0 && (historyList.get(i).getTime() - historyList.get(i - 1).getTime() > 10*1000)) {
                           // System.out.println("新建=" + histories.size());
                      // System.out.println("时间="+historyList.get(i).getTime());
                       // System.out.println("时间="+historyList.get(i-1).getTime());
                            MHistory mHistory = histories.get(histories.size() - 1);

                            mHistory.setStop_time(historyList.get(i-1).getTime());
                           // System.out.println("数据长度2=" + mHistory.getList().size());
                            //离线的新建
                            MHistory mHistory1 = new MHistory();
                            mHistory1.setId(histories.size()+1);
                            mHistory1.setStart_time(historyList.get(i).getTime());
                            mHistory1.setMap_key(historyList.get(i).getMap_key());
                            mHistory1.setSn(historyList.get(i).getSn());
                            mHistory1.setName(historyList.get(i).getName());
                            Map_Sql map_sql = new Map_Sql();
                            Map m = map_sql.getMapByMapkey(mapMapper, historyList.get(i).getMap_key());
                            mHistory1.setMap_name(m.getName());
                            mHistory1.setMap_data(m.getData());
                            mHistory1.addHistory(historyList.get(i));
                            histories.add(mHistory1);
                        }
                    }//换了地图
                    else {
                        System.out.println("换地图=" + i);
                        MHistory mHistory = histories.get(histories.size() - 1);
                        mHistory.setStop_time(historyList.get(i-1).getTime());
                        System.out.println("数据长度1=" + mHistory.getList().size());
                        //离线的新建
                        MHistory mHistory1 = new MHistory();
                        mHistory1.setId(histories.size()+1);
                        mHistory1.setStart_time(historyList.get(i).getTime());
                        mHistory1.setMap_key(historyList.get(i).getMap_key());
                        mHistory1.setSn(historyList.get(i).getSn());
                        mHistory1.setName(historyList.get(i).getName());
                        Map_Sql map_sql = new Map_Sql();
                        Map m = map_sql.getMapByMapkey(mapMapper, historyList.get(i).getMap_key());
                        mHistory1.setMap_name(m.getName());
                        mHistory1.setMap_data(m.getData());
                        mHistory1.addHistory(historyList.get(i));
                        histories.add(mHistory1);
                    }

                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("data", histories);
              System.out.println(histories.get(0).getList().size());
            return jsonObject;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "userApi/History/Search", method = RequestMethod.GET, produces = "application/json")
    public JSONObject Search(HttpServletRequest request) {
        String type = request.getParameter("history_type");
        String name = request.getParameter("name");
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(type!=null&&type.equals("person")){
            Person_Sql person_sql=new Person_Sql();
           List<Person> personList= person_sql.getAllPersonLike(personMapper,name,name,customer.getProject_key());
            for(Person person:personList){
                person.setName(person.getName()+"/"+person.getIdcard());
            }
           JSONObject jsonObject=getJsonObj(CODE_OK,personList,lang);
            return jsonObject;
        }else{
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            List<Devicep> deviceps= deviceP_sql.getDevicePByLike(devicePMapper,name,name,customer.getProject_key());
            for(Devicep devicep:deviceps){
                devicep.setName(devicep.getName()+"/"+devicep.getSn());
            }
            JSONObject jsonObject=getJsonObj(CODE_OK,deviceps,lang);
            return jsonObject;
        }
    }
    class MHistory{
        int id;
       private String name;
        private String map_key;
        private String map_name;
        private String map_data;
        private long start_time;
        private long stop_time;
        private String sn;
        private long sum;
        private  List<History> list;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getMap_name() {
            return map_name;
        }

        public void setMap_name(String map_name) {
            this.map_name = map_name;
        }

        public void setSum(long sum) {
            this.sum = sum;
        }

        public long getSum() {
            return sum;
        }

        public String getMap_data() {
            return map_data;
        }

        public void setMap_data(String map_data) {
            this.map_data = map_data;
        }

        public String getName() {
            return name;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getSn() {
            return sn;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMap_key() {
            return map_key;
        }

        public void setMap_key(String map_key) {
            this.map_key = map_key;
        }

        public long getStart_time() {
            return start_time;
        }

        public void setStart_time(long start_time) {
            this.start_time = start_time;
        }

        public long getStop_time() {
            return stop_time;
        }

        public void setStop_time(long stop_time) {
            this.stop_time = stop_time;
        }

        public List<History> getList() {
            return list;
        }

        public void setList(List<History> list) {
            this.list = list;
        }
        public void addHistory(History history){
            if(list==null){
                list=new ArrayList<>();
            }
            list.add(history);
            sum=list.size();
        }
    }


    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }
}
