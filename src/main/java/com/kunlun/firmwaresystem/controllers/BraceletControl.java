package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageBracelet;
import com.kunlun.firmwaresystem.entity.Bracelet;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.sql.Bracelet_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.RequestBody;
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
public class BraceletControl {
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private StationMapper StationMapper;

    @RequestMapping(value = "userApi/Bracelet/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllBracelet(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        Bracelet_Sql braceletSql=new Bracelet_Sql();
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
        PageBracelet pageBracelet=braceletSql.selectPageBracelet(braceletMapper,page,limit,quickSearch,customer.getUserkey(),customer.getProject_key());
        if(pageBracelet.getBraceletList().size()>0){
            for(Bracelet bracelet:pageBracelet.getBraceletList()){
                Bracelet bracelet1=braceletsMap.get(bracelet.getMac());
                bracelet.setMap_key(bracelet1.getMap_key());
                if(bracelet1.getOnline()==0){
                    bracelet.setSos(-1);
                    bracelet.setBt(0);
                }else{
                    bracelet.setSos(bracelet1.getSos());

                    bracelet.setBt(bracelet1.getBt());
                }
                bracelet.setLast_time(bracelet1.getLast_time());
                bracelet.setOnline(bracelet1.getOnline());

            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageBracelet.getTotal());
        jsonObject.put("data", pageBracelet.getBraceletList());
         return jsonObject;
    }

    @RequestMapping(value = "userApi/Bracelet/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllBracelet1(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        String type=request.getParameter("type");
        Bracelet_Sql braceletSql=new Bracelet_Sql();
        myPrintln("类型="+type);
        List<Bracelet> braceletList=braceletSql.getunAllBracelet(braceletMapper,customer.getUserkey(),customer.getProject_key(),type);
        JSONObject jsonObject = new JSONObject();
        if(lang!=null&&lang.equals("en")){
            braceletList.add(0, new Bracelet("Unbound"));
        }else {
            braceletList.add(0, new Bracelet("不绑定标签"));
        }
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", braceletList.size());
        jsonObject.put("data",braceletList);
        return jsonObject;
    }

    /*@RequestMapping(value = "userApi/Bracelet/export", method = RequestMethod.GET, produces = "application/json")
    public void getAllBeaconExport(HttpServletRequest request, HttpServletResponse response) {
        Customer customer = getCustomer(request);
        String type="5";
        Beacon_Sql beacon_sql=new Beacon_Sql();
        myPrintln("类型="+type);
        List<Beacon> beaconList=beacon_sql.getAllBeacon(beaconMapper,customer.getUserkey(),customer.getProject_key(),type);
        for(Beacon beacon:beaconList){
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
        JSONObject jsonObject = new JSONObject();
        String[] titles = {"MAC", "在线状态", "绑定状态", "资产编码/身份证","资产/人员", "电压", "创建时间", "在线时间"};
        //在这里进行添加excel
        File outfile = new File("AOA" + ".xlsx");
        SystemUtil.getUtil().createExcelBeacon(outfile.getPath(), titles, beaconList);
        if (outfile.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=result.xls" );
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(outfile);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myPrintln("----------file download---" + outfile.getPath());
            try {
                bis.close();
                fis.close();
                outfile.delete();
            } catch (IOException e) {
                e.printStackTrace();
                myPrintln("删除文件异常");
            }
        }
    }
*/
    @RequestMapping(value = "userApi/Bracelet/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deleteBracelet(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Bracelet_Sql braceletSql=new Bracelet_Sql();
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
                for(String key:braceletsMap.keySet()){
                    Bracelet bracelet=braceletsMap.get(key);
                    if(bracelet!=null&&bracelet.getId()==Integer.parseInt(ids.toString())&&bracelet.getIs_bind()==1){
                        return JsonConfig.getJsonObj(CODE_10,null,lang);
                    }
                }
            }
        }
        if(id.size()>0){
            List<Bracelet> braceletList=braceletMapper.selectBatchIds(id);
            int status = braceletSql.deletes(braceletMapper, id);
            //beaconsMap=beacon_sql.getAllBeacon(beaconMapper);

            if(status!=-1){
                for(Bracelet bracelet:braceletList){
                    braceletsMap.remove(bracelet.getMac());
                }
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Bracelet/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addAOA(HttpServletRequest request, @RequestBody JSONObject json) {
        myPrintln(json.toString());
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Bracelet_Sql braceletSql=new Bracelet_Sql();
        Bracelet bracelet=new Gson().fromJson(json.toString(),new TypeToken<Bracelet>(){}.getType());
        bracelet.setUser_key(customer.getUserkey());
        bracelet.setProject_key(customer.getProject_key());
        bracelet.setCreate_time(System.currentTimeMillis()/1000);
        bracelet.setCustomer_key(customer.getCustomerkey());
        if(bracelet.getMac()!=null){
            bracelet.setMac(bracelet.getMac().replaceAll(" ","").toLowerCase());
        }
        boolean status=braceletSql.addBracelet(braceletMapper,bracelet);
        if(status){
            braceletsMap.put(bracelet.getMac(),bracelet);
            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }
      else{
        return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
        }
    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }


}
