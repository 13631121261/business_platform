package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.entity.Check_sheet;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.CheckSheetMapper;
import com.kunlun.firmwaresystem.mqtt.MyMqttClient;
import com.kunlun.firmwaresystem.sql.CheckSheet_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;

@RestController
public class SystemControl {
    @Autowired
    private RedisUtils redisUtil;
    @Autowired
    private CheckSheetMapper checkSheetMapper;
    @RequestMapping(value = "/userApi/SystemSet", method = RequestMethod.POST, produces = "text/plain")
    public String SystemSet(HttpServletRequest request, @RequestBody JSONObject json) {
        Customer customer=getCustomer(request);
        myPrintln(json.toString());
        String jsons=json.toString();
        jsons=jsons.replaceAll("true","1");
        jsons=jsons.replaceAll("false","0");
        json=JSONObject.parseObject(jsons);
        Check_sheet check_sheet=new Gson().fromJson(json.toString(),new TypeToken<Check_sheet>(){}.getType());
        myPrintln(check_sheet.toString());
        check_sheet.setProject_key(customer.getProject_key());
        check_sheet.setUserkey(customer.getUserkey());
        String jsonObject= JsonConfig.getJson(JsonConfig.CODE_OK,"",customer.getLang());
        CheckSheet_Sql checkSheet_sql=new CheckSheet_Sql();
        checkSheet_sql.update(checkSheetMapper,check_sheet);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                check_sheetMap.put(customer.getProject_key(),check_sheet);
                MyMqttClient myMqttClient=myMqttClientMap.get(customer.getProject_key());
                if(myMqttClient!=null&&myMqttClient.getStatus()){
                    myPrintln("sdsd");
                    myMqttClient.disConnect();
                    myMqttClient.setHost(check_sheet.getHost());
                    myMqttClient.setPort(check_sheet.getPort());
                    myMqttClient.setPassword(check_sheet.getPassword());
                    myMqttClient.setUser(check_sheet.getUser());
                    myMqttClient.setSub(check_sheet.getSub());
                    myMqttClient.MyMqttClient1(check_sheet.getHost(),check_sheet.getPort());
                    myMqttClient.start();
                }
                else{
                    myPrintln("sss");
                    myMqttClient = new MyMqttClient(check_sheet.getHost(),check_sheet.getPort(),check_sheet.getSub(),check_sheet.getPub(),check_sheet.getQos(),check_sheet.getUser(),check_sheet.getPassword(),check_sheet.getProject_key());
                    myMqttClient.start();
                    myMqttClientMap.put(customer.getProject_key(),myMqttClient);
                }

            }
        }).start();*/

        return jsonObject;

    }
    @RequestMapping(value = "/userApi/SystemGet", method = RequestMethod.GET, produces = "application/json")
    public JSONObject SystemGet(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        myPrintln(customer.getProject_key());
        myPrintln(check_sheetMap.toString());
       Check_sheet check_sheet= check_sheetMap.get(customer.getProject_key());
        JSONObject jsonObject= JsonConfig.getJsonObj(JsonConfig.CODE_OK,check_sheet,customer.getLang());
      /* boolean status=  myMqttClientMap.get(customer.getProject_key()).getStatus();
        jsonObject.put("mqtt_status",status);*/
        return jsonObject;
    }

    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}