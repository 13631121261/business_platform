package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONObject;
import com.kunlun.firmwaresystem.device.PageHistory;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import com.kunlun.firmwaresystem.mappers.CompanyMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.ITextTableGenerator;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.NewSystemApplication.redisUtil;
import static com.kunlun.firmwaresystem.entity.StationStayAnalyzer.analyzeStationStays;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;
import static io.lettuce.core.GeoArgs.Unit.m;

@RestController
public class HistoryControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private AlarmMapper alarmMapper;
    @Autowired
    private CompanyMapper companyMapper;
   /* @RequestMapping(value = "userApi/History/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getHistory(HttpServletRequest request) {
        myPrintln("开始时间="+System.currentTimeMillis());
        String type=request.getParameter("history_type");
        String sn=request.getParameter("quickSearch");
        String start_time=request.getParameter("start_time");
        String stop_time=request.getParameter("stop_time");
        Customer customer=getCustomer(request);
        try {
            History_Sql history_sql = new History_Sql();
            List<History> historyList = history_sql.getHistory(historyMapper, sn, type, Long.parseLong(start_time) * 1000, Long.parseLong(stop_time) * 1000, customer.getProject_key());
            List<MHistory> histories = new ArrayList<>();
            myPrintln("数据长度=" + historyList.size());
            for (int i = 0; i < historyList.size(); i++)
            {


                if (historyList.get(i).getMap_key()==null||historyList.get(i).getMap_key().equals("")) {
                 continue;
                }
                if (i == 0) {
                    MHistory mHistory = new MHistory();
                    mHistory.setStart_time(historyList.get(i).getTime());
                    mHistory.setMap_key(historyList.get(i).getMap_key());
                    mHistory.setSn(historyList.get(i).getSn());
                    mHistory.setName(historyList.get(i).getName());

                    Map m =getMap( historyList.get(i).getMap_key());
                    if(m != null) {
                        mHistory.setMap_name(m.getName());
                    }
                    if (m != null) {
                        mHistory.setMap_data(m.getData());
                    }
                    mHistory.addHistory(historyList.get(i));
                    mHistory.setId(1);
                    histories.add(mHistory);
                    myPrintln("首次=" + i);
                } else {
                    if(i==historyList.size()-1){
                        MHistory mHistory = histories.get(histories.size() - 1);
                        mHistory.setStop_time(historyList.get(i).getTime());
                    }
                    //没有换地图
                    if (i != 0 && historyList.get(i).getMap_key().equals(historyList.get(i - 1).getMap_key())) {
                        //没有离线
                        if (i != 0 && (historyList.get(i).getTime() - historyList.get(i - 1).getTime() <= 20*1000)) {
                            MHistory mHistory = histories.get(histories.size() - 1);
                            mHistory.addHistory(historyList.get(i));

                        }
                        else if (i != 0 && (historyList.get(i).getTime() - historyList.get(i - 1).getTime() > 20*1000)) {
                            MHistory mHistory = histories.get(histories.size() - 1);
                            mHistory.setStop_time(historyList.get(i-1).getTime());
                            MHistory mHistory1 = new MHistory();
                            mHistory1.setId(histories.size()+1);
                            mHistory1.setStart_time(historyList.get(i).getTime());
                            mHistory1.setMap_key(historyList.get(i).getMap_key());
                            mHistory1.setSn(historyList.get(i).getSn());
                            mHistory1.setName(historyList.get(i).getName());
                            Map m =getMap( historyList.get(i).getMap_key());
                            mHistory1.setMap_name(m.getName());
                            mHistory1.setMap_data(m.getData());
                            mHistory1.addHistory(historyList.get(i));
                            histories.add(mHistory1);
                        }
                    }//换了地图
                    else {
                        Map m =getMap( historyList.get(i).getMap_key());
                        if (m == null) {
                         continue;
                        }
                        //myPrintln("换地图=" + i);
                    //    myPrintln("ID+"+historyList.get(i).getId());
                        MHistory mHistory = histories.get(histories.size() - 1);
                        mHistory.setStop_time(historyList.get(i-1).getTime());
                       // myPrintln("数据长度1=" + mHistory.getList().size()+ historyList.get(i).getMap_key());
                        //离线的新建
                        MHistory mHistory1 = new MHistory();
                        mHistory1.setId(histories.size()+1);
                        mHistory1.setStart_time(historyList.get(i).getTime());
                        mHistory1.setMap_key(historyList.get(i).getMap_key());
                        mHistory1.setSn(historyList.get(i).getSn());
                        mHistory1.setName(historyList.get(i).getName());

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
            //  myPrintln(histories.get(0).getList().size());
            myPrintln("结束时间="+System.currentTimeMillis());
            return jsonObject;
        }catch (Exception e){
            myPrintln(e.getMessage());
            return null;
        }
    }

    private Map getMap(String mapKey){
        Map map = (Map) redisUtil.get(redis_id_map +mapKey);
        if (map == null) {
            Map_Sql map_sql = new Map_Sql();
            map= map_sql.getMapByMapkey(mapMapper,mapKey);
        }
        return map;
    }*/

    @RequestMapping(value = "userApi/History/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getHistory(HttpServletRequest request) {
        myPrintln("开始时间="+System.currentTimeMillis());
        String type=request.getParameter("history_type");
        String sn=request.getParameter("quickSearch");
        String start_time=request.getParameter("start_time");
        String stop_time=request.getParameter("stop_time");
        Customer customer=getCustomer(request);
        try {
            History_Sql history_sql = new History_Sql();
            List<History> historyList = history_sql.getHistory(historyMapper, sn, type, Long.parseLong(start_time) * 1000, Long.parseLong(stop_time) * 1000, customer.getProject_key());
            List<MHistory> histories = new ArrayList<>();
            myPrintln("数据长度=" + historyList.size());
            List<Integer> dd=new ArrayList<>();
            for (int i = 0; i < historyList.size(); i++)
            {
                if (historyList.get(i).getMap_key()==null||historyList.get(i).getMap_key().equals("")) {
                    myPrintln("没有地图内容="+histories.get(i).getId());
                    continue;
                }
                if(i==0){
                    continue;
                }



            }
            if (dd.size()==0) {
                dd.add(historyList.size());
            }

            for (int i = 0; i < dd.size(); i++) {
                MHistory history = new MHistory();
                if(i==0){
                    history.addHistoryList(historyList.subList(0,dd.get(0)));
                }else if(i!=dd.size()-1){
                    history.addHistoryList(historyList.subList(dd.get(i-1), dd.get(i)));
                }
                else if(i==dd.size()-1){
                    history.addHistoryList(historyList.subList(dd.get(i-1), historyList.size()));
                }
                history.setId(histories.size()+1);
                Map m=getMap(history.getMap_key());
                if (m != null) {
                    history.setMap_name(m.getName());
                    history.setProportion(m.getProportion());
                    history.setMap_data(m.getData());
                    histories.add(history);
                }

            }
            myPrintln("切换次数="+dd.size());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("data", histories);
            jsonObject.put("count", histories.size());
            //  myPrintln(histories.get(0).getList().size());
            myPrintln("结束时间="+System.currentTimeMillis());
            return jsonObject;
        }catch (Exception e){
            myPrintln(e.getMessage());
            return null;
        }
    }


    HashMap<String,Map> hashMap=new HashMap<>();
    private Map getMap(String mapKey){
     //   myPrintln("读取时间="+System.currentTimeMillis());
      Map map= hashMap.get(mapKey);
    //  map = (Map) redisUtil.get(redis_id_map +mapKey);
        if (map == null) {
            Map_Sql map_sql = new Map_Sql();
            map= map_sql.getMapByMapkey(mapMapper,mapKey);
            if(map==null){
                return null;
            }
            hashMap.put(mapKey,map);
        }
      //  myPrintln("完成读取时间="+System.currentTimeMillis());
        return map;
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
    @RequestMapping(value = "userApi/History/Search_custom_down", method = RequestMethod.GET, produces = "application/json")
    public JSONObject Search_custom_down(HttpServletResponse response,HttpServletRequest request,@RequestParam(value = "stop_time") String stop_time,@ParamsNotNull @RequestParam(value = "start_time") String start_time,@RequestParam(value = "company_id") String company_id) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        try {
            String sn=request.getParameter("sn");
            History_Sql history_sql = new History_Sql();
            List<History>  history = history_sql.getHistory(historyMapper, sn, Long.parseLong(start_time) , Long.parseLong(stop_time) , customer.getProject_key(), company_id);
            List<StationStayAnalyzer.StationStay> stays = analyzeStationStays(history);
            List<StationStayAnalyzer.StationStay> list=new ArrayList<>();
            for (int i= stays.size()-1;i>=0;i--)
            {
                StationStayAnalyzer.StationStay stay=stays.get(i);
                Station station = (Station) redisUtil.get(redis_key_locator + stay.getStationMac());
                if(station!=null){
                    stay.setStation_name(station.getName());
                }
                else{
                    Station station1=new Station_sql().getStationByMac(stationMapper,stay.getStationMac());
                    if (station1 != null) {
                        stay.setStation_name(station1.getName());
                    }

                }
                Devicep devicep=devicePMap.get(stay.getSn());
                if (devicep != null) {
                    stay.setName(devicep.getName());
                    Company company=(Company) redisUtil.get(redis_key_company+devicep.getCompany_id());
                    if (company != null) {
                        stay.setCompany_name(company.getName());
                    }
                }else{
                    Person person=personMap.get(stay.getSn());
                    if (person != null) {
                        stay.setName(person.getName());
                        Company company=(Company) redisUtil.get(redis_key_company+person.getCompany_id());
                        if (company != null) {
                            stay.setCompany_name(company.getName());
                        }
                    }
                }
                list.add(stay);
            }
            myPrintln("创建文件");
           File file= ITextTableGenerator.createPdf(list,Long.parseLong(start_time) , Long.parseLong(stop_time));
            myPrintln("创建文件完成"+file.getAbsolutePath());
            if (file != null && file.exists()) { //判断文件父目录是否存在q
                myPrintln("开始下载");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                // response.setContentType("application/force-download");
                response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null; //文件输入流
                BufferedInputStream bis = null;
                OutputStream os = null; //输出流
                try {
                    os = response.getOutputStream();
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                 //   e.printStackTrace();
                    myPrintln(e.getMessage());
                }
                //   myPrintln("----------file download---" + file.getPath());
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return  getJsonObj(CODE_OK,list,lang);
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
        }
        return null;
    }



////localhost/userApi/History/index?history_type=device&start_time=1749700800&stop_time=1749873600&quickSearch=3693
    @RequestMapping(value = "userApi/History/Search_custom", method = RequestMethod.GET, produces = "application/json")
    public JSONObject Search_custom(HttpServletRequest request,@RequestParam(value = "stop_time") String stop_time,@ParamsNotNull @RequestParam(value = "start_time") String start_time,@RequestParam(value = "company_id") String company_id) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
    try {
    String sn=request.getParameter("sn");
    String page=request.getParameter("page");
    String limit=request.getParameter("limit");
    if (page==null||page.equals("")) {
        page="1";
    }
    if (limit==null||limit.equals("")) {
        limit="10";
    }
    History_Sql history_sql = new History_Sql();
    PageHistory pageHistory = history_sql.getHistory(historyMapper, sn, Long.parseLong(start_time) , Long.parseLong(stop_time) , customer.getProject_key(), company_id,page,limit);
    List<StationStayAnalyzer.StationStay> stays = analyzeStationStays(pageHistory.getHistoryList());

    myPrintln("数据长度=" + pageHistory.getTotal());
    myPrintln("查到时间"+System.currentTimeMillis());

    List<StationStayAnalyzer.StationStay> list=new ArrayList<>();
    for (int i= stays.size()-1;i>=0;i--)
    {
        StationStayAnalyzer.StationStay stay=stays.get(i);
        Station station = (Station) redisUtil.get(redis_key_locator + stay.getStationMac());
        if(station!=null){
            stay.setStation_name(station.getName());
        }
        else{
            Station station1=new Station_sql().getStationByMac(stationMapper,stay.getStationMac());
            if (station1 != null) {
                stay.setStation_name(station1.getName());
            }

        }
        Devicep devicep=devicePMap.get(stay.getSn());
        if (devicep != null) {
            stay.setName(devicep.getName());
            Company company=(Company) redisUtil.get(redis_key_company+devicep.getCompany_id());
            if (company != null) {
                stay.setCompany_name(company.getName());
            }
        }else{
            Person person=personMap.get(stay.getSn());
            if (person != null) {
                stay.setName(person.getName());
                Company company=(Company) redisUtil.get(redis_key_company+person.getCompany_id());
                if (company != null) {
                    stay.setCompany_name(company.getName());
                }
            }
        }


        list.add(stay);
    }
    myPrintln("结束时间"+System.currentTimeMillis());
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("code", 1);
    jsonObject.put("msg", "ok");
    jsonObject.put("count", pageHistory.getTotal());
    jsonObject.put("data",  list);
    return jsonObject;
    }catch (Exception e){
        myPrintln("异常="+e.getMessage());
    }
        return null;
    }

    @RequestMapping(value = "userApi/History/Search_custombyMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject Search_custombyMap(HttpServletRequest request,@RequestParam(value = "stop_time") String stop_time,@ParamsNotNull @RequestParam(value = "start_time") String start_time) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        try {
            String sn=request.getParameter("sn");

            History_Sql history_sql = new History_Sql();
            List<History> history = history_sql.getHistory(historyMapper, sn, Long.parseLong(start_time) , Long.parseLong(stop_time) , customer.getProject_key());
            List<StationStayAnalyzer.StationStay> stays = analyzeStationStays(history);

            myPrintln("数据长度=" + history.size());
            myPrintln("查到时间"+System.currentTimeMillis());

            List<StationStayAnalyzer.StationStay> list=new ArrayList<>();
            for (int i= stays.size()-1;i>=0;i--)
            {
                StationStayAnalyzer.StationStay stay=stays.get(i);
                Station station = (Station) redisUtil.get(redis_key_locator + stay.getStationMac());
                if(station!=null){
                    stay.setStation_name(station.getName());
                }
                else{
                    Station station1=new Station_sql().getStationByMac(stationMapper,stay.getStationMac());
                    if (station1 != null) {
                        stay.setStation_name(station1.getName());
                    }

                }
                Devicep devicep=devicePMap.get(stay.getSn());
                if (devicep != null) {
                    stay.setName(devicep.getName());
                    Company company=(Company) redisUtil.get(redis_key_company+devicep.getCompany_id());
                    if (company != null) {
                        stay.setCompany_name(company.getName());
                    }
                }else{
                    Person person=personMap.get(stay.getSn());
                    if (person != null) {
                        stay.setName(person.getName());
                        Company company=(Company) redisUtil.get(redis_key_company+person.getCompany_id());
                        if (company != null) {
                            stay.setCompany_name(company.getName());
                        }
                    }
                }


                list.add(stay);
            }
            myPrintln("结束时间"+System.currentTimeMillis());

            return JsonConfig.getJsonObj(CODE_OK,list,customer.getLang());
        }catch (Exception e){
            myPrintln("异常="+e.getMessage());
        }
        return null;
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
        private double proportion;

        public void setProportion(double proportion) {
            this.proportion = proportion;
        }

        public double getProportion() {
            return proportion;
        }

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
        public void addHistoryList(List<History> list){
            if(this.list==null){
                this.list=new ArrayList<History>();
            }
            sum=list.size();
            setStart_time(list.get(0).getStart_time());
            setStop_time(list.get(0).getEnd_time());
            setMap_key(list.get(0).getMap_key());
            setSn(list.get(0).getSn());
            setName(list.get(0).getName());
           for(History history:list){
                history.setProject_key(null);
                history.setMap_key(null);
                history.setSn(null);
                history.setId(0);
                history.setType(null);
                history.setName(null);
            }
            this.list.addAll(list);


        }
    }


    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}
