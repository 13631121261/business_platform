package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageFence;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.FenceMapper;
import com.kunlun.firmwaresystem.mappers.MapMapper;
import com.kunlun.firmwaresystem.mappers.PatrolListMapper;
import com.kunlun.firmwaresystem.mappers.PatrolMapper;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class PatrolControl {
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private PatrolMapper patrolMapper;
    @Resource
    private PatrolListMapper patrolListMapper;


    @RequestMapping(value = "userApi/Patrol/edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateArea(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
            Customer customer = getCustomer(request);
            String lang=customer.getLang();
            myPrintln("patrol="+jsonObject.toString());
            Patrol patrol=null;
            Patrol_Sql patrolSql=new Patrol_Sql();
            patrol = new Gson().fromJson(jsonObject.toString(), new TypeToken<Patrol>() {
            }.getType());
            patrol.setUpdate_time(System.currentTimeMillis()/1000);
            patrol.setEnable_day(patrol.getEnable_days());
            patrol.setStartEndTime(patrol.getTime_range());
            patrol.setProject_key(customer.getProject_key());
            patrol.setUser_key(customer.getUserkey());

            myPrintln(patrol.toString());
            if(patrol.getArea_id()!=0){
                if (patrolSql.update(patrolMapper, patrol)>0) {

                    response = JsonConfig.getJsonObj(CODE_OK, null,lang);
                } else {
                    response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
                }
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
            return response;
        }catch (Exception e){
            myPrintln(e.toString());
            return null;
        }
    }
    @RequestMapping(value = "userApi/Patrol/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addFence(HttpServletRequest request,  @RequestBody JSONObject jsonObject) {
        try {  JSONObject response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln("patrol="+jsonObject.toString());
        Patrol patrol=null;

            patrol= new Gson().fromJson(jsonObject.toString(), new TypeToken<Patrol>() {
            }.getType());

            patrol.setEnable_day(patrol.getEnable_days());
            patrol.setStartEndTime(patrol.getTime_range());
            patrol.setProject_key(customer.getProject_key());
            patrol.setUser_key(customer.getUserkey());
            patrol.setCreate_time(System.currentTimeMillis()/1000);
            myPrintln(patrol.toString());
            Patrol_Sql patrolSql=new Patrol_Sql();
            if (patrolSql.add(patrolMapper, patrol)) {

                response = JsonConfig.getJsonObj(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJsonObj(CODE_REPEAT, null,lang);
            }
        return response;
        }catch (Exception e){
        myPrintln(e.toString());
        return null;
    }
    }

    @RequestMapping(value = "userApi/Patrol/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllPatrol(HttpServletRequest request) {
        try {
            String search=request.getParameter("search");
            JSONObject response = null;
            Customer customer = getCustomer(request);
            Patrol_Sql patrolSql = new Patrol_Sql();
            List<Patrol> patrolList = patrolSql.getAll(patrolMapper, customer.getUserkey(), customer.getProject_key(),search);
            for (Patrol patrol : patrolList) {
                myPrintln("aaa  "+patrol.toString());
                if (patrol.getEnable_day() != null) {
                    patrol.setEnable_days(patrol.getEnable_day());
                }
                patrol.setTime_range(patrol.getStartTime(),patrol.getEndTime());
                patrol.setRequired(true);
                myPrintln("bbbb"+patrol.toString());
            }
            Patrol patrol = new Patrol();
            patrol.setEnable_days(new String[]{"1", "2", "3"});
            patrol.setName("");
            patrol.setTime_range("08:00:00", "12:00:00");
            patrolList.add(0, patrol);
            return getJsonObj(CODE_OK, patrolList, customer.getLang());
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return null;
        }

    }
    @RequestMapping(value = "userApi/Patrol/Line_add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addPatrolLine(HttpServletRequest request, @RequestBody JSONObject   jsonObject) {
        Customer customer = getCustomer(request);
        try {
            Patrol_list patrol_list= new Gson().fromJson(jsonObject.toString(), new TypeToken<Patrol_list>() {
            }.getType());
            if (patrol_list!=null) {
                String must_list="";
                String patrol_list_ids="";
                for(int i=0;i<patrol_list.getPatrol_list_detail().size();i++){
                    Patrol patrol = patrol_list.getPatrol_list_detail().get(i);

                    if (patrol.isRequired()){
                        must_list=must_list+"-"+patrol.getId();
                    }
                    patrol_list_ids=patrol_list_ids+"-"+patrol.getId();
                }
                patrol_list.setMust_list(must_list);
                patrol_list.setPatrol_list(patrol_list_ids);
                patrol_list.setCreate_time(System.currentTimeMillis()/1000);
                patrol_list.setProject_key(customer.getProject_key());
                Patrol_List_Sql patrolListSql=new Patrol_List_Sql();
                patrolListSql.add(patrolListMapper, patrol_list);
                return getJsonObj(CODE_OK, "", customer.getLang());
            }


        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",customer.getLang());
        }
    return null;
    }

    @RequestMapping(value = "userApi/Patrol/Line_edit", method = RequestMethod.POST, produces = "application/json")
    public JSONObject editPatrolLine(HttpServletRequest request, @RequestBody JSONObject   jsonObject) {
        Customer customer = getCustomer(request);
        try {
            Patrol_list patrol_list= new Gson().fromJson(jsonObject.toString(), new TypeToken<Patrol_list>() {
            }.getType());
            if (patrol_list!=null) {
                String must_list="";
                String patrol_list_ids="";
                for(int i=0;i<patrol_list.getPatrol_list_detail().size();i++){
                    Patrol patrol = patrol_list.getPatrol_list_detail().get(i);

                    if (patrol.isRequired()){
                        must_list=must_list+"-"+patrol.getId();
                    }
                    patrol_list_ids=patrol_list_ids+"-"+patrol.getId();
                }
                patrol_list.setMust_list(must_list);
                patrol_list.setPatrol_list(patrol_list_ids);
                patrol_list.setCreate_time(System.currentTimeMillis() / 1000);
                patrol_list.setProject_key(customer.getProject_key());
                Patrol_List_Sql patrolListSql=new Patrol_List_Sql();
                if(patrol_list.getId()>0){
                    patrolListSql.update(patrolListMapper, patrol_list);
                    return getJsonObj(CODE_OK, "", customer.getLang());
                }
                 else{
                    return getJsonObj(CODE_SQL_ERROR, "", customer.getLang());
                }

            }
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",customer.getLang());
        }
            return null;
    }
    @RequestMapping(value = "userApi/Patrol/Line_index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllPatrolLine(HttpServletRequest request) {
        try {
            String search=request.getParameter("search");
            JSONObject response = null;
            Customer customer = getCustomer(request);
            Patrol_List_Sql patrolListSql = new Patrol_List_Sql();
            List<Patrol_list> patrolList = patrolListSql.getAll(patrolListMapper,customer.getProject_key(),search);

            for (Patrol_list patrol_list : patrolList) {
                    int person_count =0;
                for (String idcard:personMap.keySet()){
                    Person person = personMap.get(idcard);
                    if (person.getPatrol_list_id()!=null&&person.getPatrol_list_id().contains(patrol_list.getId()+"")){
                        person_count++;
                    }
                    patrol_list.setPerson_count(person_count);
                }
                if (patrol_list.getPatrol_list() != null) {
                   String[] lists= patrol_list.getPatrol_list().split("-");
                   ArrayList<Patrol> patrolArrayList=new ArrayList<>();
                   Patrol_Sql patrolSql = new Patrol_Sql();
                   for (int i = 0; i < lists.length; i++) {
                       try{
                           if (!lists[i].isEmpty()) {
                               int id= Integer.parseInt(lists[i]);
                               Patrol patrol=patrolSql.getPatrolById(patrolMapper, id);
                               if (patrol!=null) {
                                   if (patrol.getEnable_day() != null) {
                                       patrol.setEnable_days(patrol.getEnable_day());
                                   }
                                   patrol.setTime_range(patrol.getStartTime(),patrol.getEndTime());
                                   if (patrol_list.getMust_list().contains(lists[i])) {
                                       patrol.setRequired(true);
                                   }else{
                                       patrol.setRequired(false);
                                   }
                                   patrolArrayList.add(patrol);
                               }
                           }
                       }catch (Exception e){
                           myPrintln(e.toString());
                       }
                   }
                   patrol_list.setPatrol_list_detail(patrolArrayList);
                }
            }
            Patrol_list patrol_list = new Patrol_list();
            patrol_list.setName("");
            patrolList.add(0,patrol_list);
            return getJsonObj(CODE_OK, patrolList, customer.getLang());
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return null;
        }

    }
    @RequestMapping(value = "userApi/Patrol/Line_index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllPatrolLine1(HttpServletRequest request) {
        try {
            String search=request.getParameter("search");
            JSONObject response = null;
            Customer customer = getCustomer(request);
            Patrol_List_Sql patrolListSql = new Patrol_List_Sql();
            List<Patrol_list> patrolList = patrolListSql.getAll(patrolListMapper,customer.getProject_key(),search);

            return getJsonObj(CODE_OK, patrolList, customer.getLang());
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return null;
        }

    }
    @RequestMapping(value = "userApi/Patrol/line_delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject deletePatrolList(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") String id) {
        try {
            Customer customer = getCustomer(request);
            Patrol_List_Sql patrolSql = new Patrol_List_Sql();
            patrolSql.delete(patrolListMapper, Integer.parseInt(id));
            return getJsonObj(CODE_OK, "", customer.getLang());
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return null;
        }

    }
    @RequestMapping(value = "userApi/Patrol/delete", method = RequestMethod.GET, produces = "application/json")
    public JSONObject deletePatrol(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") String id) {
        try {
            Customer customer = getCustomer(request);
            Patrol_Sql patrolSql = new Patrol_Sql();
            patrolSql.delete(patrolMapper, Integer.parseInt(id));
            return getJsonObj(CODE_OK, "", customer.getLang());
        }catch (Exception e){
            myPrintln("异常===   "+e.toString());
            return null;
        }

    }

    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   myPrintln("customer="+customer);
        return customer;
    }
}
