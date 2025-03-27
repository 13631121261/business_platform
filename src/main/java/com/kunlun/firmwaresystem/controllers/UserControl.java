package com.kunlun.firmwaresystem.controllers;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.NewSystemApplication;
import com.kunlun.firmwaresystem.device.*;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.entity.web_Structure.StationTree;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.*;
import com.kunlun.firmwaresystem.sql.*;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.SystemUtil;
import com.kunlun.firmwaresystem.util.constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class UserControl {
    @Autowired
    private WifiMapper wifiMapper;
    @Autowired
    private BleMapper bleMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private RulesMapper rulesMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private StationMapper StationMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private DeviceOfflineMapper deviceOfflineMapper;

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private MenuEn_Mapper menuEnMapper;
    @Autowired
    private RolesMapper rolesMapper;
    @Autowired
    private LogsMapper logsMapper;
    public final static int ExpireTime = 600;   // redis中存储的过期时间60s
    @Resource
    private MapMapper mapMapper;
    @Autowired
    private RedisUtils redisUtil;
   //登录
    @RequestMapping(value = "userApi/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject login(HttpServletRequest request, HttpServletResponse httpServletResponse, @RequestBody JSONObject json) {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        String response = null;
        JSONObject jsonObject=null;
        //账号密码固定超过5个字符，后续在优化
        String userName=json.getString("username");
        String passWord=json.getString("password");
        Customer customer = new Customer(userName, passWord);
        String lang=customer.getLang();
        Customer_sql customer_sql = new Customer_sql();
        List<Customer> customerList = customer_sql.getCustomer(customerMapper, customer);
        if (customerList == null || customerList.size() == 0) {
            jsonObject = JsonConfig.getJsonObj(JsonConfig.CODE_RESPONSE_NULL, null,lang);
        } else if (customerList.size() > 1) {
            jsonObject = JsonConfig.getJsonObj(JsonConfig.CODE_RESPONSE_MORE, null,lang);
        } else {
            customer=customerList.get(0);
            myPrintln("账号="+customer);
           String toketn="";
           if(customer.getType()==1){
               toketn = Base64.getEncoder().encodeToString((customer.getUserkey() + "_" + System.currentTimeMillis()).getBytes()).replaceAll("\\+", "");
           }else{
               toketn = Base64.getEncoder().encodeToString((customer.getCustomerkey() + "_" + System.currentTimeMillis()).getBytes()).replaceAll("\\+", "");
           }
            myPrintln( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            customer.setToken(toketn);
            customer.setLast_login_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            customer.setRefresh_token("");
            customer_sql.updateCustomer(customerMapper,customer);
//把原来的token清空
            redisUtil.set("tokenId:"+customer.getCustomerkey() , "", ExpireTime);
            //设置新的token
            redisUtil.set("tokenId:"+customer.getCustomerkey() , toketn, ExpireTime);
            //设置token对应内容
            redisUtil.set(toketn, customer, ExpireTime);

            jsonObject = JsonConfig.getJsonToken(CODE_OK, customer,toketn,lang);
            myPrintln("登录信息=" + customer);
        }

        return jsonObject;

    }




    @RequestMapping(value = "userApi/route", method = RequestMethod.GET,produces = "application/json")
    public JSONObject getRoute(HttpServletRequest httpRequest){
       Customer customer= getCustomer(httpRequest);
       String lang= customer.getLang();
       String project_key=httpRequest.getParameter("project_key");
       String time=( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        myPrintln("获取参数"+customer.toString());
       if(customer!=null){
            JSONObject data = new JSONObject();
            JSONObject json = new JSONObject();
            json.put("code",1);
            json.put("msg","ok");
            JSONObject adminInfo = new JSONObject();
            adminInfo.put("super",false);
            adminInfo.put("last_login_time",time);
            adminInfo.put("nickname",customer.getNickname());
            adminInfo.put("id",customer.getId());
            adminInfo.put("avatar","https://bpic.51yuansu.com/pic2/cover/00/31/39/5810b3a30ead9_610.jpg");
            adminInfo.put("username",customer.getUsername());
            adminInfo.put("project_key",project_key);
            data.put("adminInfo",adminInfo);
           String api="{\"siteName\": \"业务管理平台\",\"version\": \"v1.0.0\",\"cdnUrl\": \"\",\"apiUrl\": \"https://www.baidu.com/\",\"upload\": {\"maxsize\": 10485760,\"savename\":\"\\/storage\\/{topic}\\/{year}{mon}{day}\\/{filename}{filesha1}{.suffix}\",\"mimetype\": \"jpg,png,bmp,jpeg,gif,webp,zip,rar,xls,xlsx,doc,docx,wav,mp4,mp3,txt\",\"mode\": \"local\"}}";

           if(lang.equals("en")){
               api="{\"siteName\": \"Business Management Platform\",\"version\": \"v1.0.0\",\"cdnUrl\": \"\",\"apiUrl\": \"https://www.baidu.com/\",\"upload\": {\"maxsize\": 10485760,\"savename\":\"\\/storage\\/{topic}\\/{year}{mon}{day}\\/{filename}{filesha1}{.suffix}\",\"mimetype\": \"jpg,png,bmp,jpeg,gif,webp,zip,rar,xls,xlsx,doc,docx,wav,mp4,mp3,txt\",\"mode\": \"local\"}}";
           }
           JSONObject apiUrl=JSONObject.parseObject(api);
            data.put("time",System.currentTimeMillis()/1000);
            String t="{\n" +
                    "            \"installServicePort\":\"8000\",\n" +
                    "            \"npmPackageManager\":\"pnpm\"\n" +
                    "        }";
            JSONObject terminal=JSONObject.parseObject(t);
            data.put("terminal",terminal);
            data.put("siteConfig",apiUrl);
            if(customer.getType()==1&&project_key==null){
                String menus="";
                switch (lang){
                    case "en":
                        menus=" [ { \"extend\": \"none\",\"path\": \"project\",\"component\": \"\",\"menu_type\": null, \"keepalive\": 0,\"icon\": \"fa fa-group\",\"pid\": 0,\"type\": \"menu_dir\",\"title\": \"Project\",\"url\": \"\",\"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"project/list\",\"icon\": \"fa fa-group\",\"pid\": 0,\"type\": \"menu\",\"title\": \"Project List\",\"url\": \"\",\"extend\": \"none\",\"path\": \"project/list\",\"component\": \"/src/views/backend/project/list/index.vue\",\"children\":" +
                                " [{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"project/list/edit\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\"}," +
                                "{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"project/list/add\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\"}," +
                                "{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"project/list/del\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\"}],\"name\": \"project/list\",\"id\": 1}]}]";
                        break;

                    default:
                        menus="[\n" +
                                "            {\n" +
                                "                \"extend\": \"none\",\n" +
                                "                \"path\": \"project\",\n" +
                                "                \"component\": \"\",\n" +
                                "                \"menu_type\": null,\n" +
                                "                \"children\": [\n" +
                                "                    {\n" +
                                "                        \"menu_type\": \"tab\",\n" +
                                "                        \"keepalive\": \"project/list\",\n" +
                                "                        \"icon\": \"fa fa-group\",\n" +
                                "                        \"pid\": 0,\n" +
                                "                        \"type\": \"menu\",\n" +
                                "                        \"title\": \"项目列表\",\n" +
                                "                        \"url\": \"\",\n" +
                                "                        \"extend\": \"none\",\n" +
                                "                        \"path\": \"project/list\",\n" +
                                "                        \"component\": \"/src/views/backend/project/list/index.vue\",\n" +
                                "                        \"children\": [\n" +
                                "                            {\n" +
                                "                                \"extend\": \"none\",\n" +
                                "                                \"path\": \"\",\n" +
                                "                                \"component\": \"\",\n" +
                                "                                \"menu_type\": null,\n" +
                                "                                \"keepalive\": 0,\n" +
                                "                                \"name\": \"project/list/edit\",\n" +
                                "                                \"icon\": \"\",\n" +
                                "                                \"pid\": 3,\n" +
                                "                                \"id\": 4,\n" +
                                "                                \"type\": \"button\",\n" +
                                "                                \"title\": \"编辑\",\n" +
                                "                                \"url\": \"\"\n" +
                                "                            },\n" +
                                "                            {\n" +
                                "                                \"extend\": \"none\",\n" +
                                "                                \"path\": \"\",\n" +
                                "                                \"component\": \"\",\n" +
                                "                                \"menu_type\": null,\n" +
                                "                                \"keepalive\": 0,\n" +
                                "                                \"name\": \"project/list/add\",\n" +
                                "                                \"icon\": \"\",\n" +
                                "                                \"pid\": 3,\n" +
                                "                                \"id\": 4,\n" +
                                "                                \"type\": \"button\",\n" +
                                "                                \"title\": \"添加\",\n" +
                                "                                \"url\": \"\"\n" +
                                "                            },\n" +
                                "                            {\n" +
                                "                                \"extend\": \"none\",\n" +
                                "                                \"path\": \"\",\n" +
                                "                                \"component\": \"\",\n" +
                                "                                \"menu_type\": null,\n" +
                                "                                \"keepalive\": 0,\n" +
                                "                                \"name\": \"project/list/del\",\n" +
                                "                                \"icon\": \"\",\n" +
                                "                                \"pid\": 3,\n" +
                                "                                \"id\": 4,\n" +
                                "                                \"type\": \"button\",\n" +
                                "                                \"title\": \"删除\",\n" +
                                "                                \"url\": \"\"\n" +
                                "                            }\n" +
                                "                        ],\n" +
                                "                        \"name\": \"project/list\",\n" +
                                "                        \"id\": 1\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"keepalive\": 0,\n" +
                                "                \"icon\": \"fa fa-group\",\n" +
                                "                \"pid\": 0,\n" +
                                "                \"type\": \"menu_dir\",\n" +
                                "                \"title\": \"项目管理\",\n" +
                                "                \"url\": \"\"\n" +
                                "            },\n" +
                                "{\n" +
                                "                         \"menu_type\": \"tab\",\n" +
                                "                        \"keepalive\": \"system/list\",\n" +
                                "                        \"icon\": \"fa fa-group\",\n" +
                                "                        \"pid\": 0,\n" +
                                "                        \"type\": \"menu\",\n" +
                                "                        \"title\": \"系统设置\",\n" +
                                "                        \"url\": \"\",\n" +
                                "                        \"extend\": \"none\",\n" +
                                "                        \"path\": \"system/list\",\n" +
                                "                        \"component\": \"/src/views/backend/system/list/index.vue\"\n" +
                                "}        ]";

                    break;
                }

                //String menus="[{ \"extend\":\"none\", \"path\":\"project\", \"component\":\"\", \"menu_type\":null, \"children\":[  {\"extend\":\"none\",\"path\":\"project/list\",\"component\":\"/src/views/backend/project/list/index.vue\",\"menu_type\":\"tab\",\"keepalive\":\"project/list\",\"icon\":\"fa fa-group\",\"name\":\"project/list\",\"pid\":0,\"id\":1,\"type\":\"menu\",\"title\":\"项目列表\",\"url\":\"\"  },  {\"menu_type\":\"tab\",\"keepalive\":\"project/auth\",\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu\",\"title\":\"项目权限\",\"url\":\"\",\"extend\":\"none\",\"path\":\"project/auth\",\"component\":\"/src/views/backend/project/auth/index.vue\",\"children\":[ {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"project/auth/edit\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"编辑\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"project/auth/add\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"添加\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"project/auth/del\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"删除\",  \"url\":\"\" }],\"name\":\"project/auth\",\"id\":1  } ], \"keepalive\":0, \"icon\":\"fa fa-group\", \"pid\":0, \"type\":\"menu_dir\", \"title\":\"项目管理\", \"url\":\"\"},{ \"extend\":\"none\", \"path\":\"user\", \"component\":\"\", \"menu_type\":null, \"children\":[  {\"menu_type\":\"tab\",\"keepalive\":\"user/user\",\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu\",\"title\":\"用户列表\",\"url\":\"\",\"extend\":\"none\",\"path\":\"user/user\",\"component\":\"/src/views/backend/user/user/index.vue\",\"children\":[ {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/user/edit\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"编辑\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/user/add\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"添加\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/user/del\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"删除\",  \"url\":\"\" }],\"name\":\"user/user\",\"id\":1  },  {\"menu_type\":\"tab\",\"keepalive\":\"user/rule\",\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu\",\"title\":\"用户角色\",\"url\":\"\",\"extend\":\"none\",\"path\":\"user/rule\",\"component\":\"/src/views/backend/user/rule/index.vue\",\"children\":[ {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/rule/edit\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"编辑\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/rule/add\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"添加\",  \"url\":\"\" }, {  \"extend\":\"none\",  \"path\":\"\",  \"component\":\"\",  \"menu_type\":null,  \"keepalive\":0,  \"name\":\"user/rule/del\",  \"icon\":\"\",  \"pid\":3,  \"id\":4,  \"type\":\"button\",  \"title\":\"删除\",  \"url\":\"\" }],\"name\":\"user/rule\",\"id\":1  } ], \"keepalive\":0, \"icon\":\"fa fa-group\", \"pid\":0, \"type\":\"menu_dir\", \"title\":\"用户管理\", \"url\":\"\"}  ]";

                JSONArray menu=JSONArray.parseArray(menus);
                data.put("menus",menu);
                json.put("data",data);
              /*  String b="{    \"msg\": \"搞笑了\",    \"code\": 1,    \"data\": {   \"adminInfo\": {  \"super\": true,  \"last_login_time\": \"2023-07-25 14:18:59\",  \"nickname\": \"Admin\",  \"id\": 1,  \"avatar\": \"https://www.kunlunlink.com/wp-content/uploads/2021/04/logo.png\",  \"username\": \"admin\"   },   \"siteConfig\": {  \"apiUrl\": \"https://buildadmin.com\",  \"upload\": { \"mode\": \"local\", \"savename\": \"/storage/{topic}/{year}{mon}{day}/{filename}{filesha1}{.suffix}\", \"mimetype\": \"jpg,png,bmp,jpeg,gif,webp,zip,rar,xls,xlsx,doc,docx,wav,mp4,mp3,txt\", \"maxsize\": 10485760  },  \"cdnUrl\": \"https://demo.buildadmin.com\",  \"siteName\": \"KUNLUN资产管理\",  \"version\": \"v1.0.0\"   },   \"menus\": [{\"extend\": \"none\", \"path\": \"Station\", \"component\": \"\", \"menu_type\": null,\t \"keepalive\": 0, \"icon\": \"fa fa-group\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"网关管理\", \"url\": \"\", \"children\": [{    \"menu_type\": \"tab\",    \"keepalive\": 0,    \"icon\": \"fa fa-group\",    \"pid\": 0,    \"type\": \"menu_dir\",    \"title\": \"网关管理\",    \"url\": \"\",    \"extend\": \"none\",    \"path\": \"Station/list\",    \"component\": \"/src/views/backend/Station/list/index.vue\",    \"children\": [   {  \"extend\": \"none\",  \"path\": \"\",  \"component\": \"\",  \"menu_type\": null,  \"keepalive\": 0,  \"name\": \"Station/list/edit\",  \"icon\": \"\",  \"pid\": 3,  \"id\": 4,  \"type\": \"button\",  \"title\": \"编辑\",  \"url\": \"\"   },   {  \"extend\": \"none\",  \"path\": \"\",  \"component\": \"\",  \"menu_type\": null,  \"keepalive\": 0,  \"name\": \"Station/list/add\",  \"icon\": \"\",  \"pid\": 3,  \"id\": 4,  \"type\": \"button\",  \"title\": \"添加\",  \"url\": \"\"   },   {  \"extend\": \"none\",  \"path\": \"\",  \"component\": \"\",  \"menu_type\": null,  \"keepalive\": 0,  \"name\": \"Station/list/del\",  \"icon\": \"\",  \"pid\": 3,  \"id\": 4,  \"type\": \"button\",  \"title\": \"删除\",  \"url\": \"\"   }    ],    \"name\": \"Station/list\",    \"id\": 1} ]  }   ],   \"terminal\": {  \"installServicePort\": \"8000\",  \"npmPackageManager\": \"pnpm\"   }    },    \"time\": 1690265939}";
                        com.alibaba.fastjson.JSONObject jsonObject= JSON.parseObject(b);*/
                return json;
            }
            else if(customer.getType()==1&&project_key!=null||customer.getType()==2){
                myPrintln("账号"+customer.getProject_key());
                myPrintln("语言="+customer.getLang());
                switch (lang){
                    case  "en":
                        List<Menu_en> list=null;
                        if(customer.getType()==1){
                            //类型1，超级管理员，获取全部界面
                            MenuEn_Sql menu_sql=new MenuEn_Sql();
                            list=menu_sql.getAllMenu(menuEnMapper);
                            customer.setProject_key(project_key);
                            redisUtil.set(httpRequest.getHeader("batoken"),customer,600);
                        }else if(customer.getType()==2){
                            //普通管理员，获取对应的权限
                            project_key=customer.getProject_key();
                            if(project_key==null||project_key.equals("")){
                                return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",lang);
                            }
                            else{
                                myPrintln("RoseId"+customer.getRoles_id().substring(1));
                                String[] rolesid=customer.getRoles_id().substring(1).split("-");
                                Roles roles=null;
                                MenuEn_Sql menu_sql=new MenuEn_Sql();
                                Roles_Sql roles_sql=new Roles_Sql();
                                List<Integer> ids=new ArrayList<Integer>();
                                List<Integer> ids1=new ArrayList<Integer>();
                                for(String id:rolesid){
                                    myPrintln("获取具体的权限ID");
                                    roles=roles_sql.getOneRoles(rolesMapper,Integer.parseInt(id));
                                    String[] menu_ids=roles.getRules();
                                    for(String a:menu_ids){
                                        ids.add(Integer.parseInt(a));
                                    }
                                }
                                for(int id:ids){
                                    ids1.add(id);
                                    Menu_en menu=  menu_sql.getMenu(menuEnMapper,id);
                                    getMenuEn(menu_sql,menu.getPid(),ids1);
                                }
                                list=menu_sql.getMenu(menuEnMapper,ids1);
                            }
                        }
                        //  String menus="[{\"extend\":\"none\",\"path\":\"Station\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu_dir\",\"title\":\"网关管理\",\"url\":\"\",\"children\":[{\"menu_type\":\"tab\",\"keepalive\":0,\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu_dir\",\"title\":\"网关管理\",\"url\":\"\",\"extend\":\"none\",\"path\":\"Station/list\",\"component\":\"/src/views/backend/Station/list/index.vue\",\"children\":[{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/edit\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"编辑\",\"url\":\"\"},{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/add\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"添加\",\"url\":\"\"},{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/del\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"删除\",\"url\":\"\"}],\"name\":\"Station/list\",\"id\":1}]}]";
                        /* JSONArray.parseArray(list.*/
                        // JSONArray menu=JSONArray.parseArray(menus);
                        //   data.put("menus",menu);
                        myPrintln("id="+list.toString());
                        data.put("menus",list);
                        json.put("data",data);
                        return json;
                    case  "zh-cn":
                        List<Menu> list1=null;
                        if(customer.getType()==1){
                            //类型1，超级管理员，获取全部界面
                            Menu_Sql menu_sql=new Menu_Sql();
                            list1=menu_sql.getAllMenu(menuMapper);
                            customer.setProject_key(project_key);
                            redisUtil.set(httpRequest.getHeader("batoken"),customer,600);
                        }else if(customer.getType()==2){
                            myPrintln("类型2");
                            //普通管理员，获取对应的权限
                            project_key=customer.getProject_key();
                            if(project_key==null||project_key.equals("")){
                                return JsonConfig.getJsonObj(CODE_SQL_ERROR,"",lang);
                            }
                            else{
                                myPrintln("RoseId"+customer.getRoles_id().substring(1));
                                String[] rolesid=customer.getRoles_id().substring(1).split("-");
                                Roles roles=null;
                                Menu_Sql menu_sql=new Menu_Sql();
                                Roles_Sql roles_sql=new Roles_Sql();
                                List<Integer> ids=new ArrayList<Integer>();
                                List<Integer> ids1=new ArrayList<Integer>();
                                for(String id:rolesid){
                                    myPrintln("RoseId"+id);
                                    roles=roles_sql.getOneRoles(rolesMapper,Integer.parseInt(id));
                                    String[] menu_ids=roles.getRules();
                                    for(String a:menu_ids){
                                        myPrintln("RoseId"+a);
                                        ids.add(Integer.parseInt(a));
                                    }
                                }
                                for(int id:ids){
                                    myPrintln("ids1+"+id);
                                    ids1.add(id);
                                    Menu menu=  menu_sql.getMenu(menuMapper,id);
                                    myPrintln(menu.toString());
                                    getMenu(menu_sql,menu.getPid(),ids1);
                                }
                                myPrintln("sdsd"+ids1);
                                list1=menu_sql.getMenu(menuMapper,ids1);
                            }
                        }
                        //  String menus="[{\"extend\":\"none\",\"path\":\"Station\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu_dir\",\"title\":\"网关管理\",\"url\":\"\",\"children\":[{\"menu_type\":\"tab\",\"keepalive\":0,\"icon\":\"fa fa-group\",\"pid\":0,\"type\":\"menu_dir\",\"title\":\"网关管理\",\"url\":\"\",\"extend\":\"none\",\"path\":\"Station/list\",\"component\":\"/src/views/backend/Station/list/index.vue\",\"children\":[{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/edit\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"编辑\",\"url\":\"\"},{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/add\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"添加\",\"url\":\"\"},{\"extend\":\"none\",\"path\":\"\",\"component\":\"\",\"menu_type\":null,\"keepalive\":0,\"name\":\"Station/list/del\",\"icon\":\"\",\"pid\":3,\"id\":4,\"type\":\"button\",\"title\":\"删除\",\"url\":\"\"}],\"name\":\"Station/list\",\"id\":1}]}]";
                        /* JSONArray.parseArray(list.*/
                        // JSONArray menu=JSONArray.parseArray(menus);
                        //   data.put("menus",menu);

                        data.put("menus",list1);
                        json.put("data",data);
                        return json;

                }



            }
            else{
                //普通管理员登录
                return (JSONObject) new JSONObject().put("data","微红2");

            }
        }




      return (JSONObject) new JSONObject().put("data","cunstomer账号为空");
    }
private void getMenu(Menu_Sql menu_sql,int p_id,  List<Integer> ids){
    myPrintln(" pid="+p_id);
    if(p_id!=0) {
        ids.add(p_id);
        Menu m = menu_sql.getMenu(menuMapper, p_id);
        if(m==null){
            return;
        }
        getMenu(menu_sql, m.getPid(), ids);
    }

}

    private void getMenuEn(MenuEn_Sql menu_sql,int p_id,  List<Integer> ids){
        if(p_id!=0){
            ids.add(p_id);
            Menu_en m=menu_sql.getMenu(menuEnMapper,p_id);
            getMenuEn(menu_sql,m.getPid(),ids);
        }

    }
/*    @RequestMapping(value = "userApi/route1", method = RequestMethod.GET,produces = "application/json")
    public JSONObject getRoute1(HttpServletRequest httpRequest,@RequestParam("project_key") @ParamsNotNull String project_key){
        Customer customer= getCustomer(httpRequest);
        String time=( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(customer!=null&&customer.getType()==1){
                String a="{ \"msg\": \"搞笑了\", \"code\": 1, \"data\": {\"adminInfo\": {\"super\": false,\"last_login_time\": \""+time+"\",\"nickname\": \"Admin\",\"id\": 1,\"avatar\": \"https://demo.buildadmin.com/static/images/avatar.png\",\"username\": \"admin\"},\"siteConfig\": {\"apiUrl\": \"https://buildadmin.com\",\"upload\": { \"mode\": \"local\", \"savename\": \"/storage/{topic}/{year}{mon}{day}/{filename}{filesha1}{.suffix}\", \"mimetype\": \"jpg,png,bmp,jpeg,gif,webp,zip,rar,xls,xlsx,doc,docx,wav,mp4,mp3,txt\", \"maxsize\": 10485760},\"cdnUrl\": \"https://demo.buildadmin.com\",\"siteName\": \"BuildAdmin演示站\",\"version\": \"v1.0.0\"},\"menus\": [{ \"extend\": \"none\", \"path\": \"dashboard\", \"component\": \"/src/views/backend/dashboard.vue\", \"menu_type\": \"tab\", \"keepalive\": \"dashboard/dashboard\", \"name\": \"dashboard/dashboard\", \"icon\": \"fa fa-dashboard\", \"pid\": 0, \"id\": 1, \"type\": \"menu\", \"title\": \"控制台\", \"url\": \"\"},{ \"extend\": \"none\", \"path\": \"Station\", \"component\": \"\", \"menu_type\": null, \"keepalive\": 0, \"icon\": \"fa fa-group\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"网关管理\", \"url\": \"\",\"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"Station/list\",\"icon\": \"fa fa-dashboard\",\"pid\": 0,\"type\": \"menu\",\"title\": \"自定义\",\"url\": \"\",\"extend\": \"none\",\"path\": \"Station/list\",\"component\": \"/src/views/backend/Station/list/index.vue\",\"name\": \"Station/list\",\"id\": 1,\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"Station/list/edit\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"Station/list/add\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"Station/list/del\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }]} ]},{ \"menu_type\": null, \"keepalive\": 0, \"icon\": \"fa fa-group\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"权限管理\", \"url\": \"\", \"extend\": \"none\", \"path\": \"auth\", \"component\": \"\", \"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"auth/group\",\"icon\": \"fa fa-group\",\"pid\": 2,\"type\": \"menu\",\"title\": \"角色组管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"auth/group\",\"component\": \"/src/views/backend/auth/group/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/group/index\",\"icon\": \"\",\"pid\": 3,\"id\": 4,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/group/add\",\"icon\": \"\",\"pid\": 3,\"id\": 5,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/group/edit\",\"icon\": \"\",\"pid\": 3,\"id\": 6,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/group/del\",\"icon\": \"\",\"pid\": 3,\"id\": 7,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"auth/group\",\"id\": 3},{\"menu_type\": \"tab\",\"keepalive\": \"auth/admin\",\"icon\": \"el-icon-UserFilled\",\"pid\": 2,\"type\": \"menu\",\"title\": \"管理员管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"auth/admin\",\"component\": \"/src/views/backend/auth/admin/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/admin/index\",\"icon\": \"\",\"pid\": 8,\"id\": 9,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/admin/add\",\"icon\": \"\",\"pid\": 8,\"id\": 10,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/admin/edit\",\"icon\": \"\",\"pid\": 8,\"id\": 11,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/admin/del\",\"icon\": \"\",\"pid\": 8,\"id\": 12,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"auth/admin\",\"id\": 8},{\"menu_type\": \"tab\",\"keepalive\": \"auth/rule\",\"icon\": \"el-icon-Grid\",\"pid\": 2,\"type\": \"menu\",\"title\": \"菜单规则管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"auth/rule\",\"component\": \"/src/views/backend/auth/rule/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/rule/index\",\"icon\": \"\",\"pid\": 13,\"id\": 14,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/rule/add\",\"icon\": \"\",\"pid\": 13,\"id\": 15,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/rule/edit\",\"icon\": \"\",\"pid\": 13,\"id\": 16,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/rule/del\",\"icon\": \"\",\"pid\": 13,\"id\": 17,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/rule/sortable\",\"icon\": \"\",\"pid\": 13,\"id\": 18,\"type\": \"button\",\"title\": \"快速排序\",\"url\": \"\" }],\"name\": \"auth/rule\",\"id\": 13},{\"menu_type\": \"tab\",\"keepalive\": \"auth/adminLog\",\"icon\": \"el-icon-List\",\"pid\": 2,\"type\": \"menu\",\"title\": \"管理员日志管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"auth/adminLog\",\"component\": \"/src/views/backend/auth/adminLog/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"auth/adminLog/index\",\"icon\": \"\",\"pid\": 19,\"id\": 20,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }],\"name\": \"auth/adminLog\",\"id\": 19} ], \"name\": \"auth\", \"id\": 2},{ \"menu_type\": null, \"keepalive\": 0, \"icon\": \"fa fa-drivers-license\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"会员管理\", \"url\": \"\", \"extend\": \"none\", \"path\": \"user\", \"component\": \"\", \"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"user/user\",\"icon\": \"fa fa-user\",\"pid\": 21,\"type\": \"menu\",\"title\": \"会员管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"user/user\",\"component\": \"/src/views/backend/user/user/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/user/index\",\"icon\": \"\",\"pid\": 22,\"id\": 23,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/user/add\",\"icon\": \"\",\"pid\": 22,\"id\": 24,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/user/edit\",\"icon\": \"\",\"pid\": 22,\"id\": 25,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/user/del\",\"icon\": \"\",\"pid\": 22,\"id\": 26,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"user/user\",\"id\": 22},{\"menu_type\": \"tab\",\"keepalive\": \"user/group\",\"icon\": \"fa fa-group\",\"pid\": 21,\"type\": \"menu\",\"title\": \"会员分组管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"user/group\",\"component\": \"/src/views/backend/user/group/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/group/index\",\"icon\": \"\",\"pid\": 27,\"id\": 28,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/group/add\",\"icon\": \"\",\"pid\": 27,\"id\": 29,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/group/edit\",\"icon\": \"\",\"pid\": 27,\"id\": 30,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/group/del\",\"icon\": \"\",\"pid\": 27,\"id\": 31,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"user/group\",\"id\": 27},{\"menu_type\": \"tab\",\"keepalive\": \"user/rule\",\"icon\": \"fa fa-th-list\",\"pid\": 21,\"type\": \"menu\",\"title\": \"会员规则管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"user/rule\",\"component\": \"/src/views/backend/user/rule/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/rule/index\",\"icon\": \"\",\"pid\": 32,\"id\": 33,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/rule/add\",\"icon\": \"\",\"pid\": 32,\"id\": 34,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/rule/edit\",\"icon\": \"\",\"pid\": 32,\"id\": 35,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/rule/del\",\"icon\": \"\",\"pid\": 32,\"id\": 36,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/rule/sortable\",\"icon\": \"\",\"pid\": 32,\"id\": 37,\"type\": \"button\",\"title\": \"快速排序\",\"url\": \"\" }],\"name\": \"user/rule\",\"id\": 32},{\"menu_type\": \"tab\",\"keepalive\": 0,\"icon\": \"el-icon-Money\",\"pid\": 21,\"type\": \"menu\",\"title\": \"会员余额管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"user/moneyLog\",\"component\": \"/src/views/backend/user/moneyLog/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/moneyLog/index\",\"icon\": \"\",\"pid\": 38,\"id\": 39,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/moneyLog/add\",\"icon\": \"\",\"pid\": 38,\"id\": 40,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }],\"name\": \"user/moneyLog\",\"id\": 38},{\"menu_type\": \"tab\",\"keepalive\": \"user/scoreLog\",\"icon\": \"el-icon-Discount\",\"pid\": 21,\"type\": \"menu\",\"title\": \"会员积分管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"user/scoreLog\",\"component\": \"/src/views/backend/user/scoreLog/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/scoreLog/index\",\"icon\": \"\",\"pid\": 41,\"id\": 42,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"user/scoreLog/add\",\"icon\": \"\",\"pid\": 41,\"id\": 43,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }],\"name\": \"user/scoreLog\",\"id\": 41} ], \"name\": \"user\", \"id\": 21},{ \"menu_type\": null, \"keepalive\": 0, \"icon\": \"fa fa-cogs\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"常规管理\", \"url\": \"\", \"extend\": \"none\", \"path\": \"routine\", \"component\": \"\", \"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"routine/config\",\"icon\": \"el-icon-Tools\",\"pid\": 44,\"type\": \"menu\",\"title\": \"系统配置\",\"url\": \"\",\"extend\": \"none\",\"path\": \"routine/config\",\"component\": \"/src/views/backend/routine/config/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/config/index\",\"icon\": \"\",\"pid\": 45,\"id\": 46,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/config/edit\",\"icon\": \"\",\"pid\": 45,\"id\": 47,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/config/add\",\"icon\": \"\",\"pid\": 45,\"id\": 77,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }],\"name\": \"routine/config\",\"id\": 45},{\"menu_type\": \"tab\",\"keepalive\": \"routine/attachment\",\"icon\": \"fa fa-folder\",\"pid\": 44,\"type\": \"menu\",\"title\": \"附件管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"routine/attachment\",\"component\": \"/src/views/backend/routine/attachment/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/attachment/index\",\"icon\": \"\",\"pid\": 48,\"id\": 49,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/attachment/edit\",\"icon\": \"\",\"pid\": 48,\"id\": 50,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/attachment/del\",\"icon\": \"\",\"pid\": 48,\"id\": 51,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"routine/attachment\",\"id\": 48},{\"menu_type\": \"tab\",\"keepalive\": \"routine/adminInfo\",\"icon\": \"fa fa-user\",\"pid\": 44,\"type\": \"menu\",\"title\": \"个人资料\",\"url\": \"\",\"extend\": \"none\",\"path\": \"routine/adminInfo\",\"component\": \"/src/views/backend/routine/adminInfo.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/adminInfo/index\",\"icon\": \"\",\"pid\": 52,\"id\": 53,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"routine/adminInfo/edit\",\"icon\": \"\",\"pid\": 52,\"id\": 54,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }],\"name\": \"routine/adminInfo\",\"id\": 52} ], \"name\": \"routine\", \"id\": 44},{ \"menu_type\": \"tab\", \"keepalive\": \"moduleStore/moduleStore\", \"icon\": \"el-icon-GoodsFilled\", \"pid\": 0, \"type\": \"menu\", \"title\": \"模块市场\", \"url\": \"\", \"extend\": \"none\", \"path\": \"moduleStore\", \"component\": \"/src/views/backend/module/index.vue\", \"children\": [{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"moduleStore/moduleStore/update\",\"icon\": \"\",\"pid\": 78,\"id\": 83,\"type\": \"button\",\"title\": \"更新\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"moduleStore/moduleStore/uninstall\",\"icon\": \"\",\"pid\": 78,\"id\": 82,\"type\": \"button\",\"title\": \"卸载\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"moduleStore/moduleStore/changeState\",\"icon\": \"\",\"pid\": 78,\"id\": 81,\"type\": \"button\",\"title\": \"调整状态\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"moduleStore/moduleStore/install\",\"icon\": \"\",\"pid\": 78,\"id\": 80,\"type\": \"button\",\"title\": \"安装\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"moduleStore/moduleStore/index\",\"icon\": \"\",\"pid\": 78,\"id\": 79,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\"} ], \"name\": \"moduleStore/moduleStore\", \"id\": 78},{ \"menu_type\": null, \"keepalive\": 0, \"icon\": \"fa fa-shield\", \"pid\": 0, \"type\": \"menu_dir\", \"title\": \"数据安全管理\", \"url\": \"\", \"extend\": \"none\", \"path\": \"security\", \"component\": \"\", \"children\": [{\"menu_type\": \"tab\",\"keepalive\": \"security/dataRecycleLog\",\"icon\": \"fa fa-database\",\"pid\": 55,\"type\": \"menu\",\"title\": \"数据回收站\",\"url\": \"\",\"extend\": \"none\",\"path\": \"security/dataRecycleLog\",\"component\": \"/src/views/backend/security/dataRecycleLog/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycleLog/index\",\"icon\": \"\",\"pid\": 56,\"id\": 57,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycleLog/del\",\"icon\": \"\",\"pid\": 56,\"id\": 58,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycleLog/restore\",\"icon\": \"\",\"pid\": 56,\"id\": 59,\"type\": \"button\",\"title\": \"还原\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycleLog/info\",\"icon\": \"\",\"pid\": 56,\"id\": 60,\"type\": \"button\",\"title\": \"查看详情\",\"url\": \"\" }],\"name\": \"security/dataRecycleLog\",\"id\": 56},{\"menu_type\": \"tab\",\"keepalive\": \"security/sensitiveDataLog\",\"icon\": \"fa fa-expeditedssl\",\"pid\": 55,\"type\": \"menu\",\"title\": \"敏感数据修改记录\",\"url\": \"\",\"extend\": \"none\",\"path\": \"security/sensitiveDataLog\",\"component\": \"/src/views/backend/security/sensitiveDataLog/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveDataLog/index\",\"icon\": \"\",\"pid\": 61,\"id\": 62,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveDataLog/del\",\"icon\": \"\",\"pid\": 61,\"id\": 63,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveDataLog/rollback\",\"icon\": \"\",\"pid\": 61,\"id\": 64,\"type\": \"button\",\"title\": \"回滚\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveDataLog/info\",\"icon\": \"\",\"pid\": 61,\"id\": 65,\"type\": \"button\",\"title\": \"查看详情\",\"url\": \"\" }],\"name\": \"security/sensitiveDataLog\",\"id\": 61},{\"menu_type\": \"tab\",\"keepalive\": \"security/dataRecycle\",\"icon\": \"fa fa-database\",\"pid\": 55,\"type\": \"menu\",\"title\": \"数据回收规则管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"security/dataRecycle\",\"component\": \"/src/views/backend/security/dataRecycle/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycle/index\",\"icon\": \"\",\"pid\": 66,\"id\": 67,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycle/add\",\"icon\": \"\",\"pid\": 66,\"id\": 68,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycle/edit\",\"icon\": \"\",\"pid\": 66,\"id\": 69,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/dataRecycle/del\",\"icon\": \"\",\"pid\": 66,\"id\": 70,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"security/dataRecycle\",\"id\": 66},{\"menu_type\": \"tab\",\"keepalive\": \"security/sensitiveData\",\"icon\": \"fa fa-expeditedssl\",\"pid\": 55,\"type\": \"menu\",\"title\": \"敏感字段规则管理\",\"url\": \"\",\"extend\": \"none\",\"path\": \"security/sensitiveData\",\"component\": \"/src/views/backend/security/sensitiveData/index.vue\",\"children\": [ {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveData/index\",\"icon\": \"\",\"pid\": 71,\"id\": 72,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveData/add\",\"icon\": \"\",\"pid\": 71,\"id\": 73,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveData/edit\",\"icon\": \"\",\"pid\": 71,\"id\": 74,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\" }, {\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"security/sensitiveData/del\",\"icon\": \"\",\"pid\": 71,\"id\": 75,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\" }],\"name\": \"security/sensitiveData\",\"id\": 71} ], \"name\": \"security\", \"id\": 55},{ \"extend\": \"none\", \"path\": \"crud/crud\", \"component\": \"/src/views/backend/crud/index.vue\", \"menu_type\": \"tab\", \"keepalive\": \"crud/crud\", \"name\": \"crud/crud\", \"icon\": \"fa fa-code\", \"pid\": 0, \"id\": 90, \"type\": \"menu\", \"title\": \"CRUD代码生成\", \"url\": \"\"},{ \"menu_type\": \"tab\", \"keepalive\": 0, \"icon\": \"el-icon-Notebook\", \"pid\": 0, \"type\": \"menu\", \"title\": \"知识库（可修改）\", \"url\": \"\", \"extend\": \"none\", \"path\": \"testBuild\", \"component\": \"/src/views/backend/testBuild/index.vue\", \"children\": [{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"crud/crud/index\",\"icon\": \"\",\"pid\": 84,\"id\": 91,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"crud/crud/generate\",\"icon\": \"\",\"pid\": 84,\"id\": 92,\"type\": \"button\",\"title\": \"生成\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"crud/crud/delete\",\"icon\": \"\",\"pid\": 84,\"id\": 93,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"testBuild/index\",\"icon\": \"\",\"pid\": 84,\"id\": 85,\"type\": \"button\",\"title\": \"查看\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"testBuild/add\",\"icon\": \"\",\"pid\": 84,\"id\": 86,\"type\": \"button\",\"title\": \"添加\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"testBuild/edit\",\"icon\": \"\",\"pid\": 84,\"id\": 87,\"type\": \"button\",\"title\": \"编辑\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"testBuild/del\",\"icon\": \"\",\"pid\": 84,\"id\": 88,\"type\": \"button\",\"title\": \"删除\",\"url\": \"\"},{\"extend\": \"none\",\"path\": \"\",\"component\": \"\",\"menu_type\": null,\"keepalive\": 0,\"name\": \"testBuild/sortable\",\"icon\": \"\",\"pid\": 84,\"id\": 89,\"type\": \"button\",\"title\": \"快速排序\",\"url\": \"\"} ], \"name\": \"testBuild\", \"id\": 84}],\"terminal\": {\"installServicePort\": \"8000\",\"npmPackageManager\": \"pnpm\"} }, \"time\": 1690265939}";
                JSONObject aa=JSONObject.parseObject(a);
                return aa;
        }
        return  null;
    }*/


    @RequestMapping(value = "userApi/logout", method = RequestMethod.POST, produces = "text/plain")
    public String logout(HttpServletRequest request){
        String token=null;


        token=request.getHeader("batoken");
        myPrintln("token"+token);

        Customer customer=(Customer)  redisUtil.get(token);
        String lang=customer.getLang();
        redisUtil.set("tokenId:"+customer.getCustomerkey() , "", ExpireTime);
        //设置新的token
      //  redisUtil.set("tokenId:"+customer.getCustomerkey() , toketn, ExpireTime);
        //设置token对应内容
        redisUtil.set(token, "", ExpireTime);
       String  response = JsonConfig.getJson(CODE_ReLogin,null,lang);
        return response;
    }

    @RequestMapping(value = "userApi/getInfo", method = RequestMethod.GET, produces = "text/plain")
    public String getInfo(@RequestParam("token") @ParamsNotNull String token){
        Customer customer=(Customer) redisUtil.get(token );
        String lang=customer.getLang();
        String response=null;
        if(customer!=null){
            response = JsonConfig.getJson(CODE_OK, customer,lang);
        }else{
            response = JsonConfig.getJson(CODE_ReLogin, null,lang);
        }
        return response;
    }
    @RequestMapping(value = "userApi/dashboard", method = RequestMethod.GET, produces = "application/json")
    public com.alibaba.fastjson.JSONObject getDashboard(HttpServletRequest request){

       String token= request.getHeader("batoken");
        myPrintln("Token="+token);
       String a=    "{\n"+
                "    \"code\": 1,\n"+
                "    \"msg\": \"\",\n"+
                "    \"time\": 1689928427,\n"+
                "    \"data\": {\n"+
                "        \"remark\": \"开源等于互助；开源需要大家一起来支持，支持的方式有很多种，比如使用、推荐、写教程、保护生态、贡献代码、回答问题、分享经验、打赏赞助等；欢迎您加入我们！\"\n"+
                "    }\n"+
                "}";
        com.alibaba.fastjson.JSONObject jsonObject=JSON.parseObject(a);
        return jsonObject;
    }
    //直接导入菜单。执行一次
    @RequestMapping(value = "userApi/setMenu", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject setMenu(HttpServletRequest request, HttpServletResponse httpServletResponse, @RequestBody JSONArray json) {
        myPrintln(json.toString());
        Gson gson = new Gson();
        ArrayList<Menu> menuArrayList  = gson.fromJson(json.toString(), new TypeToken<List<Menu>>(){}.getType());
        for(Menu menu:menuArrayList){
            getNext(menu);
        }
        return null;

    }
    private Menu getNext(Menu menu){
       // myPrintln(menu);
        if(menu.getChildren()!=null&&menu.getChildren().size()>0){

            addMenu(menu);
            for(Menu menus:menu.getChildren()){
             //   myPrintln(menu);
                 getNext(menus);
            }
        }else{

            addMenu(menu);
           // myPrintln("111");
        }
    //    myPrintln("终止");
      return null;
    }
    private void addMenu(Menu menu){
        Menu_Sql menu_sql=new Menu_Sql();
        menu.setKeepalive(menu.getName());
        menu_sql.addArea(menuMapper,menu);
    }


    //获取界面的菜单
    @RequestMapping(value = "userApi/Menu/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getMenu(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        if(customer.getType()!=1){
            return null;
        }
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        json.put("code",1);
        json.put("msg","ok");
        String lang =customer.getLang();
        switch (lang){
            case "en":
                MenuEn_Sql menuEn_sql=new MenuEn_Sql();
                List<Menu_en> list=menuEn_sql.getAllMenu1(menuEnMapper);
                data.put("list",list);
                json.put("data",data);
                break;
            default:
                Menu_Sql menu_sql=new Menu_Sql();
                List<Menu> list1=menu_sql.getAllMenu1(menuMapper);
                data.put("list",list1);
                json.put("data",data);
                break;
        }
        return json;
    }

//获取超级管理员下的角色对应的权限
    @RequestMapping(value = "userApi/Roles/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getRoles(HttpServletRequest request) {

        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        String project_key=customer.getProject_key();
        if(project_key==null||project_key.length()==0){
           return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }else{
            Roles_Sql roles_sql=new Roles_Sql();
          List<Roles> roles=  roles_sql.getAllroles(rolesMapper,project_key);
            JSONObject data = new JSONObject();
            data.put("code",1);
            data.put("msg","ok");
            data.put("data",roles);
            return data;
        }
    }
    //获取单个角色
    @RequestMapping(value = "userApi/Roles/edit", method = {RequestMethod.GET }, produces = "application/json")
    @ResponseBody
    public JSONObject getRolesOne(HttpServletRequest request) {
        String method=request.getMethod();
       String ids= request.getParameter("id");

           int id=Integer.parseInt(ids);
           Customer customer=getCustomer(request);
        String lang=customer.getLang();
           String project_key=customer.getProject_key();
           if(project_key==null||project_key.length()==0){
               return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
           }else{
               Roles_Sql roles_sql=new Roles_Sql();
               Roles roles=  roles_sql.getOneRoles(rolesMapper,id);
               JSONObject data = new JSONObject();
               data.put("code",1);
               data.put("msg","ok");
               data.put("data",roles);
               return data;
           }
    }
    @RequestMapping(value = "userApi/Roles/edit", method = {RequestMethod. POST }, produces = "application/json")
    @ResponseBody
    public JSONObject getRolesOne(HttpServletRequest request, @RequestBody JSONObject json) {
        Roles_Sql roles_sql=new Roles_Sql();
        Customer customer=getCustomer(request);
        Roles roles=new Gson().fromJson(json.toString(),new TypeToken<Roles>(){}.getType());
        JSONArray jsonArray=json.getJSONArray("rules");
        String rules="";
        int id=100;
        for(int i=0;i<jsonArray.size();i++){
            myPrintln(jsonArray.get(i).toString());
            rules=rules+","+jsonArray.get(i).toString();
            if(Integer.parseInt(jsonArray.get(i).toString())<id){
                id=Integer.parseInt(jsonArray.get(i).toString());
            }
        }
        String lang=customer.getLang();
        String details="";
        switch (lang){
            case"en":
                MenuEn_Sql menuEn_sql=new MenuEn_Sql();
                Menu_en menu=  menuEn_sql.getMenu(menuEnMapper, id);
                details=menu.getTitle()+"...About "+jsonArray.size()+" Item";
                break;
            default:
                Menu_Sql menu_sql=new Menu_Sql();
                Menu menu1=  menu_sql.getMenu(menuMapper, id);
                details=menu1.getTitle()+"等"+jsonArray.size()+"项";
                break;
        }

        roles.setDetails(details);
        roles.setRuless(rules);
        roles.setUser_key(customer.getUserkey());
        roles.setProject_key(customer.getProject_key());
        roles_sql.update(rolesMapper,roles);
        JSONObject data = new JSONObject();
        data.put("code",1);
        data.put("msg","ok");
            return data;
    }

    //删除角色
    @RequestMapping(value = "userApi/Roles/del", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject deleteRoles(HttpServletRequest request, @RequestBody JSONArray json) {
        myPrintln(json.toString());
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(json==null||json.size()==0){
            return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
        }else{
            int id =0;
            Customer_sql customer_sql=new Customer_sql();
            for(Object jsonObject:json){
                try {
                    myPrintln(jsonObject.toString());
                     id = Integer.parseInt(jsonObject.toString());
                   boolean status= customer_sql.check(customerMapper,id,customer.getUserkey());
                   if(status){
                       myPrintln("1");
                       return JsonConfig.getJsonObj(CODE_10,CODE_UNBIND,lang);
                   }
                }catch (Exception e){
                    myPrintln("2"+e.getMessage());
                    return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
                }
            }
            Roles_Sql roles_sql=new Roles_Sql();
            for(Object jsonObject:json){
                try {
                    id = Integer.parseInt(jsonObject.toString());
                    roles_sql.delete(rolesMapper,id);
                }catch (Exception e){
                    myPrintln("3");
                    return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
                }
            }
            return JsonConfig.getJsonObj(CODE_OK,"",lang);
        }

    }
    //添加角色权限
    @RequestMapping(value = "userApi/Roles/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject addRoles(HttpServletRequest request, HttpServletResponse httpServletResponse, @RequestBody JSONObject json) {
        myPrintln(json.toString());
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        String project_key=customer.getProject_key();
        if(project_key==null||project_key.length()==0){
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }else{

            Roles_Sql roles_sql=new Roles_Sql();
            Roles roles=new Roles();
            roles.setName(json.getString("name"));
            roles.setStatus(json.getString("status"));
            JSONArray jsonArray=json.getJSONArray("rules");
            String rules="";
            int id=100;
            for(int i=0;i<jsonArray.size();i++){
                myPrintln(jsonArray.get(i).toString());
                rules=rules+","+jsonArray.get(i).toString();
                if(Integer.parseInt(jsonArray.get(i).toString())<id){
                    id=Integer.parseInt(jsonArray.get(i).toString());
                }
            }
            myPrintln("id="+id);

            String details="";
            try {
                switch (lang) {
                    case "en":
                        MenuEn_Sql menuEn_sql = new MenuEn_Sql();
                        Menu_en menu = menuEn_sql.getMenu(menuEnMapper, id);
                        details=menu.getTitle()+"... About "+jsonArray.size()+" Item";
                        break;
                    default:
                        Menu_Sql menu_sql = new Menu_Sql();
                        Menu menu1 = menu_sql.getMenu(menuMapper, id);
                        details = menu1.getTitle() + "等" + jsonArray.size() + "项";
                        break;
                }


            roles.setDetails(details);
            roles.setRuless(rules);
            roles.setUser_key(customer.getUserkey());
            roles.setProject_key(project_key);
            roles_sql.addroles(rolesMapper,roles);
            JSONObject data = new JSONObject();
            data.put("code",1);
            data.put("msg","ok");
            return data; }
            catch (Exception e){
                myPrintln(e.getMessage());
                return null;
            }
        }
    }


    //获取超级管理员下全部管理账号
    @RequestMapping(value = "userApi/Customer/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getCustomer1(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        String quickSearch=request.getParameter("quickSearch");
        if(quickSearch==null){
            quickSearch="";
        }
        if(customer.getType()==1){
            Customer_sql customer_sql=new Customer_sql();
            Roles_Sql roles_sql=new Roles_Sql();
            List<Customer> customerList= customer_sql.getCustomer(customerMapper,customer.getUserkey(),customer.getProject_key(),quickSearch);
            JSONObject data = new JSONObject();
            data.put("code",1);
            data.put("msg","ok");
            for(Customer customer1:customerList){
                String id=customer1.getRoles_id();
                if(id!=null&&id.startsWith("-")){
                    String ids[]=id.substring(1).split("-");
                    String group_name_arr[]=new String[ids.length];
                    int group_arr[]=new int[ids.length];
                    int i=0;
                    for(String d:ids){
                        myPrintln(d);
                       Roles roles= roles_sql.getOneRoles(rolesMapper,Integer.parseInt(d));
                       if(roles==null){
                           continue;
                       }
                        group_name_arr[i]=roles.getName();
                        group_arr[i]=roles.getId();
                        i++;
                    }
                    customer1.setGroup_arr(group_arr);
                    customer1.setGroup_name_arr(group_name_arr);
                }
            }
            data.put("data",customerList);
            return data;
        }
        return JsonConfig.getJsonObj(CODE_noP,CODE_noP_txt,lang);
    }

    //添加项目管理员
    @RequestMapping(value = "userApi/Customer/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject addCustomer(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        myPrintln(jsonObject.toString());
        if(customer.getType()!=1){
            return JsonConfig.getJsonObj(CODE_noP,null,lang);
        }
        Customer customer1=new Gson().fromJson(jsonObject.toString(),new TypeToken<Customer>(){}.getType());
        Customer_sql customer_sql=new Customer_sql();
        boolean status= customer_sql.checkUser(customerMapper,customer1);
        if(status){
            return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
        }else{
            customer1.setProject_key(customer.getProject_key());
            customer1.setUserkey(customer.getUserkey());
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
            //   myPrintln("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
            String customerkey = customer1.getUserkey()+"_"+Base64.getEncoder().encodeToString((customer1.getUsername() + "_" + date.getTime()).getBytes()).replaceAll("\\+", "");
            customer1.setCreate_time(sdf.format(date));
            customer1.setCustomerkey(customerkey);
            customer1.setType(2);
            String roles_ids="";
            JSONArray jsonArray= jsonObject.getJSONArray("group_arr");
            if(jsonArray!=null){
                for(int i=0;i<jsonArray.size();i++){
                    roles_ids=roles_ids+"-"+jsonArray.getString(i);
                }
            }
            else{
                return  JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
            customer1.setRoles_id(roles_ids);
           int id= customer_sql.addUser(customerMapper,customer1);
           if(id>0){
               return JsonConfig.getJsonObj(CODE_OK,null,lang);
           }else{
               return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
           }
        }
    }

    //更新管理员信息
    @RequestMapping(value = "userApi/Customer/edit", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject updateCustomer(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        myPrintln(jsonObject.toString());
        if(customer.getType()!=1){
            return JsonConfig.getJsonObj(CODE_noP,null,lang);
        }
        Customer customer1=new Gson().fromJson(jsonObject.toString(),new TypeToken<Customer>(){}.getType());
        Customer_sql customer_sql=new Customer_sql();
        Customer customer2= customer_sql.getCustomer(customerMapper,customer1.getId());
        if(!customer1.getUsername().equals(customer2.getUsername())){
            return JsonConfig.getJsonObj(CODE_noC,null,lang);
        }
        if(customer2==null){
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }else{
            customer1.setProject_key(customer2.getProject_key());
            customer1.setUserkey(customer2.getUserkey());
            customer1.setCustomerkey(customer2.getCustomerkey());
            customer1.setType(2);
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
            customer1.setUpdate_time(sdf.format(date));
            String roles_ids="";
            JSONArray jsonArray= jsonObject.getJSONArray("group_arr");
            if(jsonArray!=null){
                for(int i=0;i<jsonArray.size();i++){
                    roles_ids=roles_ids+"-"+jsonArray.getString(i);
                }
            }
            else{
                return  JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
            }
            customer1.setRoles_id(roles_ids);
            int id= customer_sql.updateCustomer(customerMapper,customer1);
            if(id>0){
                return JsonConfig.getJsonObj(CODE_OK,null,lang);
            }else{
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
        }
    }

    //获取一个管理员信息
    @RequestMapping(value = "userApi/Customer/edit", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getCustomer(HttpServletRequest request,@RequestParam("id") @ParamsNotNull String id) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(customer.getType()!=1){
            return JsonConfig.getJsonObj(CODE_noP,null,lang);
        }

        Customer_sql customer_sql=new Customer_sql();
        Customer customer2= customer_sql.getCustomer(customerMapper,Integer.parseInt(id));
        if(customer2==null){
            return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
        }else{
            if(customer2.getRoles_id()==null||customer2.getRoles_id().equals("")){
                return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
            }
            Roles_Sql roles_sql=new Roles_Sql();
            String ids[]=customer2.getRoles_id().substring(1).split("-");
            String[] roles_name=new String[ids.length];
            int[] roles_id=new int[ids.length];
            for(int i=0;i<ids.length;i++){
                if(!ids[i].equals("")){
                    Roles roles=roles_sql.getOneRoles(rolesMapper,Integer.parseInt(ids[i]));
                    roles_name[i]=roles.getName();
                    roles_id[i]=roles.getId();
                }
            }
            JSONObject jsonObject=JsonConfig.getJsonObj(CODE_OK,customer2,lang);
            myPrintln(jsonObject.toString());
            JSONObject jsonObject1=jsonObject.getJSONObject("data");
            jsonObject1.put("group_name_arr",roles_name);
            jsonObject1.put("group_arr",roles_id);
            jsonObject.put("data",jsonObject1);
            return jsonObject;
        }
    }

    //删除一个管理员
    @RequestMapping(value = "userApi/Customer/del", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject deleteCustomer(HttpServletRequest request,@RequestBody JSONArray json) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(customer.getType()!=1){
            return JsonConfig.getJsonObj(CODE_noP,null,lang);
        }
        Customer_sql customer_sql=new Customer_sql();
        if(json.size()<=0){
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }
        int id=0;
        for(Object jsonObject:json){
            try {
                myPrintln(jsonObject.toString());
                id = Integer.parseInt(jsonObject.toString());
                int status= customer_sql.deleteCustomer(customerMapper,id);
                if(status<0){
                    return JsonConfig.getJsonObj(CODE_SQL_ERROR,null,lang);
                }
            }catch (Exception e){
                myPrintln("2"+e.getMessage());
                return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
            }
        }
        return JsonConfig.getJsonObj(CODE_OK,null,lang);
    }



    //获取日志列表
    @RequestMapping(value = "userApi/Logs/index", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public JSONObject getLogs(HttpServletRequest request) {
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
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

        if(customer.getType()==1){
            Logs_Sql logs_sql=new Logs_Sql();
            PageLogs pageLogs= logs_sql.selectPageLogs(logsMapper,page,limit,quickSearch,customer.getUserkey(),customer.getProject_key());
            JSONObject jsonObject= JsonConfig.getJsonObj(CODE_OK,pageLogs.getMapList(),lang);
            jsonObject.put("count",pageLogs.getTotal());
            return jsonObject;
        }
        return JsonConfig.getJsonObj(CODE_noP,CODE_noP_txt,lang);
    }
    //删除角色
    @RequestMapping(value = "userApi/Logs/del", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject deleteLogs(HttpServletRequest request, @RequestBody JSONArray json) {
        myPrintln(json.toString());
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(json==null||json.size()==0){
            return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
        }else{
            int id =0;
            List<Integer> ids=new ArrayList<>();
            for(Object jsonObject:json){
                try {
                    myPrintln(jsonObject.toString());
                    id = Integer.parseInt(jsonObject.toString());
                    ids.add(id);
                }catch (Exception e){
                    myPrintln("2"+e.getMessage());
                    return JsonConfig.getJsonObj(CODE_DR,CODE_DR_txt,lang);
                }
            }

            int status=logsMapper.deleteBatchIds(ids);

            return JsonConfig.getJsonObj(CODE_OK,"",lang);
        }

    }















        //添加账号
    @RequestMapping(value = "userApi/createUser", method = RequestMethod.POST, produces = "text/plain")
    public String createCustomer(HttpServletRequest request, @RequestParam("userName") @ParamsNotNull String userName, @RequestParam("passWord") @ParamsNotNull String passWord, @RequestParam("nickName")  String nickName, @RequestParam("permission_key")  @ParamsNotNull String permission_key,@RequestParam("phoneNumber")  @ParamsNotNull String phoneNumber) {
        String response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if(customer.getPermission()==null||customer.getPermission().getEdituser()==0){
            return JsonConfig.getJson(CODE_noP,null,lang);
        }
       else if(customer.getPermission().getEdituser()==1 ){
            myPrintln("对象为" + customer.getUsername());
            User user = new User(userName, passWord,  nickName, phoneNumber,permission_key);
            User_sql user_sql = new User_sql();
            int result = user_sql.addUser(userMapper, user);
            if(result==-1){
                response = JsonConfig.getJson(CODE_REPEAT, null,lang);
            }else{
                response = JsonConfig.getJson(CODE_OK, null,lang);
            }
        }
        return response;
    }


    //创建部门

    @RequestMapping(value = "userApi/addDepartment", method = RequestMethod.POST, produces = "text/plain")
    public String addDepartment(HttpServletRequest request, @RequestParam("name") @ParamsNotNull String name, @RequestParam("p_id") @ParamsNotNull int p_id) {
        String response = null;
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if(customer.getPermission()==null||customer.getPermission().getEditdepartment()==0){
            return JsonConfig.getJson(CODE_noP,null,lang);
        }
        else if(customer.getPermission().getEditdepartment()==1 ){
            myPrintln("对象为" + customer.getUsername());
            Department department=new Department(name,customer.getUserkey(),p_id,customer.getCustomerkey());
            Department_Sql departmentSql = new Department_Sql();
            boolean result = departmentSql.addDepartment(departmentMapper, department);
            if(result){
                response = JsonConfig.getJson(CODE_OK, null,lang);
            }else{
                response = JsonConfig.getJson(CODE_REPEAT, null,lang);
            }
        }
        return response;
    }

    //获取全部部门

   /* @RequestMapping(value = "userApi/getDepartment", method = RequestMethod.GET, produces = "text/plain")
    public String getDepartment(HttpServletRequest request) {
        String response = null;
        Customer customer = getCustomer(request);
        if(customer.getPermission()==null||customer.getPermission().getLookdepartment()==0){
            return JsonConfig.getJson(CODE_noP,null);
        }
        else if(customer.getPermission().getLookdepartment()==1 ){
            myPrintln("对象为" + customer.getUsername());
            Department_Sql departmentSql = new Department_Sql();
            List<Department> departments = departmentSql.getAllDepartment(departmentMapper,customer.getUserkey());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CODE_OK);
            jsonObject.put("msg", CODE_OK_txt);

            List<Department> departmentList=new ArrayList<>();
            java.util.Map<Integer,Department> departmentMap=new HashMap<>();
            for(int i=0;i<departments.size();i++){
                Department department=departments.get(i);
                Department department1=departmentMap.get(department.getP_id());
                if(department1==null){
                    departmentMap.put(department.getId(),department);
                    departmentList.add(department);
                }else{
                    departmentMap.put(department.getId(),department);
                    department1.addDepartment(department);
                }
            }
             jsonObject.put("data", departmentList);
            response=jsonObject.toString();
            response=response.replaceAll("name","title");
            response=response.replaceAll("departmentlist","children");

        }
        return response;
    }
*/
    //添加人员
    /*@RequestMapping(value = "userApi/addPerson", method = RequestMethod.POST, produces = "text/plain")
    public String addPerson(HttpServletRequest request, @RequestParam("name") @ParamsNotNull String name,
                            @RequestParam("sex") @ParamsNotNull int sex,
                            @RequestParam("idcard") @ParamsNotNull String idcard,@RequestParam("phone") @ParamsNotNull String phone, @RequestParam("p_id") @ParamsNotNull int p_id) {
        String response = null;
        int isopen;

        Customer customer = getCustomer(request);
        if(customer.getPermission()==null||customer.getPermission().getEditperson()==0){
            return JsonConfig.getJson(CODE_noP,null);
        }
        else if(customer.getPermission().getEditperson()==1 ){
            myPrintln("对象为" + customer.getUsername());
            String pathss=null;
            Person person=new Person( name, phone, sex, pathss, p_id, "", 0,idcard, customer.getUserkey(),customer.getCustomerkey());
            Person_Sql person_sql=new Person_Sql();
          boolean status=  person_sql.addPerson(personMapper,person);
          if(status){
              response = JsonConfig.getJson(CODE_OK, null);
              personMap.put(person.getIdcard(),person);
          }else{
              response = JsonConfig.getJson(CODE_REPEAT, null);
          }
        }
        return response;
    }
*/

    //编辑人员信息
    @RequestMapping(value = "userApi/editPerson", method = RequestMethod.POST, produces = "text/plain")
    public String editPerson(HttpServletRequest request, @RequestParam("name") @ParamsNotNull String name,
                            @RequestParam("sex") @ParamsNotNull int sex,
                            @RequestParam("idcard") @ParamsNotNull String idcard,@RequestParam("phone") @ParamsNotNull String phone,@RequestParam("p_id") @ParamsNotNull int p_id) {
        String response = null;
        Customer user1 = getCustomer(request);
        String lang=user1.getLang();
        if(user1.getPermission()==null||user1.getPermission().getEditperson()==0){
            return JsonConfig.getJson(CODE_noP,null,lang);
        }
        else if(user1.getPermission().getEditperson()==1 ){
            myPrintln("对象为" + user1.getUsername());
            Person person=personMap.get(idcard);
            if(person!=null){
                String pathss=null;
               /* if(file!=null){
                    File path = new File(paths);

                    if (!path.exists()) {
                        myPrintln("文件夹不存在创建=" + path.mkdirs());
                    }
                    try {
                        file.transferTo(new File(path.getPath() + "/" + idcard + ".png"));
                        pathss =path.getPath() + "/" + idcard + ".png";
                    }catch (Exception e){
                        myPrintln("保存文件异常");
                    }
                }

                if(bind_mac!=null&&bind_mac.length()>0){
                    person.setBind_mac(bind_mac);
                    person.setIsbind(1);
                }else{
                    person.setIsbind(0);
                }*/
                person.setName(name);
                person.setIsopen(0);
                  person.setPhone(phone);
               /*     person.setPhoto(host+"/userApi/getPhoto?sn="+person.getIdcard());*/
                person.setDepartment_id(p_id);
                person.setSex(sex);
            }
            Person_Sql person_sql=new Person_Sql();
            boolean status=  person_sql.update(personMapper,person);
            if(status){
                response = JsonConfig.getJson(CODE_OK, null,lang);
            }else{
                response = JsonConfig.getJson(CODE_REPEAT, null,lang);
            }
        }
        return response;
    }







    @RequestMapping(value = "userApi/addProject", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addProject(HttpServletRequest request,    HttpServletResponse httpServletResponse ,  @RequestBody JSONObject json ){
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        JSONObject response =JSON.parseObject("{\"msg\":\"参数不为空\",\"code\":2}");
        String name=json.getString("project_name");
        String info=json.getString("project_info");
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        myPrintln("2665"+json.toString()+response.toString());
        if(json==null){
            return response;
        }
        if(name==null||name.equals("")||info==null||info.equals("")){
            return response;
        }
        if (customer.getCustomerkey() != null && customer.getCustomerkey().length() > 0) {
            Project_Sql project_sql=new Project_Sql();
            Project project=new Project(name,info,customer.getUserkey());
           boolean status= project_sql.addProject(projectMapper,project);
           if(status){
               response=JsonConfig.getJsonObj(CODE_OK,"OK",lang);
           }
        }
        return response;
    }


    @RequestMapping(value = "userApi/getAllProject", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllProject(HttpServletRequest request){
        JSONObject response =JSON.parseObject("{\"msg\":\"用户权限异常\",\"code\":2}");
        String name=request.getParameter("project_name");
        if(name==null){
            name="";
        }
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if(customer.getType()!=1){
            return response;
        }
        if (customer.getCustomerkey() != null && customer.getCustomerkey().length() > 0) {
            Project_Sql project_sql = new Project_Sql();
            List<Project> list = project_sql.getAllProject(projectMapper, customer.getUserkey(), name);
            response = JsonConfig.getJsonObj(CODE_OK, list,lang);
        }
        return response;
    }
    @RequestMapping(value = "userApi/deleteProject", method = RequestMethod.GET, produces = "application/json")
    public JSONObject deleteProject(HttpServletRequest request,@RequestParam("project_key") @ParamsNotNull String project_key){
        JSONObject response =JSON.parseObject("{\"msg\":\"用户权限异常\",\"code\":2}");

        if(project_key==null){
            return response;
        }
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if(customer.getType()!=1){
            return response;
        }
        if (customer.getCustomerkey() != null && customer.getCustomerkey().length() > 0) {
            Project_Sql project_sql = new Project_Sql();
            int status = project_sql.delete(projectMapper, project_key,customer.getUserkey());
            if(status>0){
                response = JsonConfig.getJsonObj(CODE_OK, "",lang);
            }
            else{
                response = JsonConfig.getJsonObj(CODE_SQL_ERROR, "",lang);
            }
        }
        return response;
    }
    @RequestMapping(value = "userApi/updateProject", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateProject(HttpServletRequest request, @RequestBody JSONObject json){
        JSONObject response =JSON.parseObject("{\"msg\":\"用户权限异常\",\"code\":2}");
        myPrintln(json.toString());
        String user_key=json.getString("user_key");
        String name=json.getString("project_name");
        String info=json.getString("project_info");
        String project_key=json.getString("project_key");
        if(project_key==null||user_key==null||info==null||project_key==null){
           return response;
        }

        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if(customer.getType()!=1){
            return response;
        }else if(!customer.getUserkey().equals(user_key)){
            return response;
        }
        if (customer.getCustomerkey() != null && customer.getCustomerkey().length() > 0) {
            Project_Sql project_sql = new Project_Sql();
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
            String time = sdf.format(date);
            int status = project_sql.update(projectMapper, project_key,customer.getUserkey(),info, name,time);
            if(status>0){
                response = JsonConfig.getJsonObj(CODE_OK, "",lang);
            }else{
                response = JsonConfig.getJsonObj(CODE_SQL_ERROR, "",lang);
            }
        }
        return response;
    }

    @RequestMapping(value = "userApi/setStationProject", method = RequestMethod.POST, produces = "text/plain")
    public String setStationProject(HttpServletRequest request, @RequestParam("address") @ParamsNotNull String address, @RequestParam("config_key") String config_key, @RequestParam("config_name") String config_name) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if (customer != null) {
            Station_sql Station_sql = new Station_sql();
            Station Station = (Station) redisUtil.get(redis_key_locator + address);
            redisUtil.set(redis_key_locator + address, Station);
            Station_sql.updateStation(StationMapper, Station);
            response = JsonConfig.getJson(CODE_OK, null,lang);
        }
        return response;
    }

    @RequestMapping(value = "userApi/addRules", method = RequestMethod.POST, produces = "text/plain")
    public String addRules(HttpServletRequest request, @RequestParam("name") @ParamsNotNull String name, @ParamsNotNull @RequestParam("type") int type, @ParamsNotNull @RequestParam("server") String server, @ParamsNotNull @RequestParam("port") int port) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        if (customer.getCustomerkey() != null && customer.getCustomerkey().length() > 0) {
            Rules rules = new Rules(name, type, server, port,customer.getUserkey(), customer.getCustomerkey());
            Rules_sql rules_sql = new Rules_sql();
            if (rules_sql.addRules(rulesMapper, rules)) {
                rulesMap.put(rules.getRule_key(), rules);
                response = JsonConfig.getJson(CODE_OK, null,lang);
            } else {
                response = JsonConfig.getJson(JsonConfig.CODE_REPEAT, null,lang);
            }
        }
        return response;
    }

    @RequestMapping(value = "userApi/getStatus", method = RequestMethod.GET, produces = "text/plain")
    public String getStatus() {
        String response = "默认参数";
        response = JsonConfig.getJson(CODE_OK, null,"en");
        return response;
    }







    @RequestMapping(value = "/userApi/getProjectStation", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getProjectStation(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "config_key") String config_key) {
        Customer customer=getCustomer(request);
        String response = "默认参数";
       Station_sql Station_sql=new Station_sql();
        List<StationTree> stringListHashMap=Station_sql.getAllStation(StationMapper,customer.getUserkey(),customer.getProject_key(),config_key);
        if(stringListHashMap.size()==0){
            List<StationTree> trees=new ArrayList<>();
            StationTree StationTree=new StationTree();
            String lang=customer.getLang();
            if(lang!=null&&lang.equals("en")){
                StationTree.setLabel("不关联配置");
                StationTree.setAddress("网关MAC");
            }
            StationTree.setLabel("UnLink Config");
            StationTree.setAddress("Station MAC");
            StationTree.setId(-1);
            StationTree.setChildren(trees);

            stringListHashMap.add(StationTree);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("data", stringListHashMap);

        return jsonObject;
    }





    @RequestMapping(value = "/userApi/addBeaconTag", method = RequestMethod.POST, produces = "text/plain")
    public String addBeaconTag(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "name") String name, @ParamsNotNull @RequestParam(value = "major") Integer major, @ParamsNotNull @RequestParam(value = "minor") Integer minor, @ParamsNotNull @RequestParam(value = "x") Double x, @ParamsNotNull @RequestParam(value = "y") Double y, @ParamsNotNull @RequestParam(value = "projectKey") String projectKey, @ParamsNotNull @RequestParam(value = "projectName") String projectName) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Btag_Sql btag_sql = new Btag_Sql();
        boolean status = btag_sql.addBeaconTag(bTagMapper, new Beacon_tag(name, major, minor, projectKey, customer.getUserkey(), x, y, projectName,customer.getCustomerkey()));
        if (status) {
            response = JsonConfig.getJson(CODE_OK, null,lang);
            //需要把信标信息添加到定位列表中、
        } else {
            response = JsonConfig.getJson(CODE_REPEAT, null,lang);
        }
        return response;
    }

    @RequestMapping(value = "/userApi/editBeaconTag", method = RequestMethod.GET, produces = "text/plain")
    public String editBeaconTag(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "id") Integer id, @ParamsNotNull @RequestParam(value = "name") String name, @ParamsNotNull @RequestParam(value = "major") Integer major, @ParamsNotNull @RequestParam(value = "minor") Integer minor, @ParamsNotNull @RequestParam(value = "x") Double x, @ParamsNotNull @RequestParam(value = "y") Double y, @ParamsNotNull @RequestParam(value = "projectKey") String projectKey, @ParamsNotNull @RequestParam(value = "projectName") String projectName) {
        String response = "默认参数";
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Btag_Sql btag_sql = new Btag_Sql();

        Beacon_tag beacon_tag = new Beacon_tag(id, name, major, minor, projectKey, customer.getUserkey(), x, y, projectName,customer.getCustomerkey());
        boolean status = btag_sql.update(bTagMapper, beacon_tag);
        beacon_tagMap = btag_sql.getAllBeacon(bTagMapper,customer.getUserkey());
        if (status) {
            response = JsonConfig.getJson(CODE_OK, null,lang);
            //需要把信标信息添加到定位列表中、
        } else {
            response = JsonConfig.getJson(CODE_SQL_ERROR, null,lang);
        }
        return response;
    }




    @RequestMapping(value = "/userApi/getUserFirmwareVersion", method = RequestMethod.GET, produces = "text/plain")
    public String getUserFirmwareVersion(HttpServletRequest request) {
        String response = "默认参数";
        Customer user = getCustomer(request);
        String lang=user.getLang();
        if (user == null) {
            response = JsonConfig.getJson(CODE_OFFLINE, null,lang);
            return response;
        }
        Wifi wifi = new Wifi();
        List<Wifi_firmware> wifi_firmware = wifi.getCustomerVersion(wifiMapper, user.getUserkey());
        Ble ble = new Ble();
        List<Ble_firmware> ble_firmware = ble.getCustomerVersion(bleMapper, user.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", constant.code_ok);
        jsonObject.put("type", "getFirmwareResponse");
        if (wifi_firmware == null) {
            jsonObject.put("wifi", "null");
        } else {
            jsonObject.put("wifi", wifi_firmware);
        }
        if (ble_firmware == null) {
            jsonObject.put("ble", "null");
        } else {
            jsonObject.put("ble", ble_firmware);
        }
        //   myPrintln("json="+jsonObject.toString());
        return jsonObject.toString();
    }

    @RequestMapping(value = "/userApi/getBleVersion", method = RequestMethod.GET, produces = "text/plain")
    public String getBleVersion(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "page") String page,
                                @RequestParam(value = "remake") String remake,
                                @RequestParam(value = "version") String version,
                                @ParamsNotNull @RequestParam(value = "limit") String limit) {
        Customer user = getCustomer(request);
        String response = "默认参数";
        Ble ble = new Ble();
        PageBleVersion pageBleVersion = ble.selectPageBleVersion(bleMapper, Integer.valueOf(page), Integer.valueOf(limit), remake, version, user.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageBleVersion.getTotal());
        jsonObject.put("data", pageBleVersion.getBle_firmwares());
        response = jsonObject.toString();
        return response;
    }

    @RequestMapping(value = "/userApi/deleteBleVersion", method = RequestMethod.GET, produces = "text/plain")
    public String deleteBleVersion(HttpServletRequest request, @RequestParam(value = "version") String version) {
        //   myPrintln("请求的地址"+request.getContextPath());
        // myPrintln("请求的地址"+request.getRequestURI());
        // myPrintln("请求的地址"+request.getServletPath());
        // myPrintln("请求的地址"+request.getServerPort());
        // myPrintln("请求的地址"+request.getLocalPort());
        Customer user = getCustomer(request);
        String lang=user.getLang();
        String response = "默认参数";
        if (user.getUsername().equals("test")) {
            response = JsonConfig.getJson(JsonConfig.CODE_noP, null,lang);
            return response;
        }
        Ble ble = new Ble();
        int status = ble.delete(bleMapper, user.getUserkey(), version);
        if (status != -1) {
            response = JsonConfig.getJson(CODE_OK, null,lang);
        } else {
            response = JsonConfig.getJson(CODE_SQL_ERROR, null,lang);
        }
        return response;

    }


    @RequestMapping(value = "/userApi/getWifiVersion", method = RequestMethod.GET, produces = "text/plain")
    public String getWifiVersion(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "page") String page,
                                 @RequestParam(value = "remake") String remake,
                                 @RequestParam(value = "version") String version,
                                 @ParamsNotNull @RequestParam(value = "limit") String limit) {
        Customer user = getCustomer(request);
        String response = "默认参数";
        Wifi ble = new Wifi();
        PageWifiVersion pageBleVersion = ble.selectPageWifiVersion(wifiMapper, Integer.valueOf(page), Integer.valueOf(limit), remake, version, user.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageBleVersion.getTotal());
        jsonObject.put("data", pageBleVersion.getWifi_firmwares());
        response = jsonObject.toString();
        return response;
    }

    @RequestMapping(value = "/userApi/deleteWifiVersion", method = RequestMethod.GET, produces = "text/plain")
    public String deleteWifiVersion(HttpServletRequest request, @RequestParam(value = "version") String version) {

        Customer user = getCustomer(request);
        String lang=user.getLang();
        String response = "默认参数";
        if (user.getUsername().equals("test")) {
            response = JsonConfig.getJson(JsonConfig.CODE_noP, null,lang);
            return response;
        }
        Wifi wifi = new Wifi();
        int status = wifi.delete(wifiMapper, user.getUserkey(), version);
        if (status != -1) {
            response = JsonConfig.getJson(CODE_OK, null,lang);
        } else {
            response = JsonConfig.getJson(CODE_SQL_ERROR, null,lang);
        }
        return response;

    }





    @RequestMapping(value = "/userApi/upload", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String upload(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("type") String type, @RequestParam("version") String version,
                         @RequestParam("company_name") String company_name,
                         @RequestParam("remake") String remake) throws IOException {

        InetAddress address = null;
        Customer user = getCustomer(request);
        String lang=user.getLang();
        if (user.getUsername().equals("test")) {
            String response = JsonConfig.getJson(JsonConfig.CODE_noP, null,lang);
            return response;
        }
        File path = new File(paths + user.getUserkey());
        if (!path.exists()) {
            myPrintln("文件夹不存在创建=" + path.mkdirs());
        }
        try {
            address = InetAddress.getLocalHost();
            myPrintln("输出地址=" + address.getHostAddress() + "文件路径=" + path.getName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // myPrintln("[文件类型] - [{}]"+ file.getContentType());
        // myPrintln("[文件名称] - [{}]"+ file.getOriginalFilename());
        // myPrintln("[文件大小] - [{}]"+ file.getSize());

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //  myPrintln("当前时间为: " + ft.format(dNow));
        //保存
        JSONObject jsonObject = new JSONObject();
        file.transferTo(new File(path.getPath() + "/" + type + "_" + version + ".firmware"));
        if (type.equals("ble")) {
            Ble_firmware ble_firmware = new Ble_firmware(url, remake, ft.format(dNow), company_name, user.getUserkey(), version,user.getCustomerkey());
            int d = bleMapper.insert(ble_firmware);
            // myPrintln("保存蓝牙固件数据的结果码="+d);
        } else if (type.equals("wifi")) {
            Wifi_firmware wifi_firmware = new Wifi_firmware(url, remake, ft.format(dNow), company_name, user.getUserkey(), version,user.getCustomerkey());

            int d = wifiMapper.insert(wifi_firmware);
            //  myPrintln("保存wifi数据的结果码="+d);
        }
        jsonObject.put("code", constant.code_ok);
        jsonObject.put("msg", "上传成功");
        return jsonObject.toString();
    }





    @RequestMapping(value = "/userApi/selectPageRules", method = RequestMethod.GET, produces = "text/plain")
    public String selectPageRules(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "page") String page,
                                  @ParamsNotNull @RequestParam(value = "limit") String limit) {
        Customer user = getCustomer(request);
        String response = "默认参数";
        Rules_sql rules_sql = new Rules_sql();
        PageRules pageRules = rules_sql.selectPageRules(rulesMapper, Integer.valueOf(page), Integer.valueOf(limit), user.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageRules.getTotal());
        jsonObject.put("data", pageRules.getRulesList());
        response = jsonObject.toString();
        return response;
    }


    @RequestMapping(value = "/userApi/selectAllRules", method = RequestMethod.GET, produces = "text/plain")
    public String selectAllRules(HttpServletRequest request) {
        Customer user = getCustomer(request);
        String lang=user.getLang();
        String response = "默认参数";
        Rules_sql rules_sql = new Rules_sql();
        List<Rules> list = rules_sql.selectAllRules(rulesMapper, user.getUserkey());

        response = JsonConfig.getJson(CODE_OK, list,lang);
        return response;
    }


    @RequestMapping(value = "/aa/getJsontest", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getJsontest(@RequestBody JSONObject json) {
        // myPrintln("收到="+json.toString());
        return "收";
    }

    @RequestMapping(value = "/userApi/setNull", method = RequestMethod.GET)
    @ResponseBody
    public String setNull() {
        for (String key : StationMap.keySet()) {
            redisUtil.set(redis_key_Station_revice_count + key, 0);
            redisUtil.set(redis_key_Station_onLine_time + key, null);
        }
        return "ok";
    }

    @RequestMapping(value = "/userApi/uploadStation", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String uploadStation(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        String response;
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        InetAddress address = null;
        Customer user = getCustomer(request);
        if (user.getUsername().equals("test")) {
            response = JsonConfig.getJson(JsonConfig.CODE_noP, null,lang);
            return response;
        }
        ArrayList<java.util.HashMap<String, String>> data = SystemUtil.readExcel(file, new String[]{ "ble_mac", "name","x", "y"});
        if (data == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", -100);
            jsonObject.put("msg", "文档为空或者格式不对");
            return jsonObject.toString();
        } else {
            String pkey = null;
            if (pkey == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", -101);
                jsonObject.put("msg", "没有符合的配置项，请检查后重试！");
                return jsonObject.toString();
            }
            Station_sql StationSql = new Station_sql();
            int ok = 0;
            int fail = 0;
            for (java.util.Map<String, String> map : data) {
                myPrintln("循环");
                Station Station = new Station(map.get("ble_mac"), Double.parseDouble(map.get("x")), Double.parseDouble(map.get("y")));
                Station.setUser_key(user.getUserkey());
                Station.setProject_key(user.getProject_key());
                boolean status = StationSql.addStation(StationMapper, Station);
                if (status) {
                    ok++;
                } else {
                    fail++;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg", "成功导入" + ok + "个记录，失败" + fail + "个记录");
            return jsonObject.toString();
        }
    }




    private Customer getCustomer(HttpServletRequest request) {
       String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
     //   myPrintln("customer="+customer);
        return customer;
    }


    @RequestMapping(value = "/userApi/selectAllPerson", method = RequestMethod.GET, produces = "text/plain")
    public String selectAllPerson( HttpServletRequest request, @RequestParam(value = "p_id") String p_ids){
        Customer customer=getCustomer(request);
        Person_Sql person_sql=new Person_Sql();
        int p_id=-1;
        if(p_ids!=null){
            p_id=Integer.parseInt(p_ids);
        }
        List<Person> personList=person_sql.getAllPerson(personMapper,customer.getUserkey(),customer.getProject_key());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", personList.size());
        jsonObject.put("data", personList);
        return jsonObject.toString();
    }

    //获取全部部门，经过排序
    @RequestMapping(value = "/userApi/selectAllDepartmentss", method = RequestMethod.GET, produces = "text/plain")
    public String selectAllDepartmentss(HttpServletRequest request){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Department_Sql departmentSql=new Department_Sql();
        List<Department> departments= departmentSql.getAllDepartment(departmentMapper,customer.getUserkey(),customer.getProject_key());

        java.util.Map<String,DD> ddMap=new HashMap<>();
        List<Department> departmentList=new ArrayList<>();
        java.util.Map<String,Department> departmentMap=new HashMap<>();
        for(int i=0;i<departments.size();i++){
            Department department=departments.get(i);
            Department department1=departmentMap.get(department.getP_id());
            if(department1==null){
                departmentMap.put(department.getId()+"",department);
                departmentList.add(department);
            }else{
                departmentMap.put(department.getId()+"",department);
                department1.addDepartment(department);
            }
        }

        String response = JsonConfig.getJson(CODE_OK, departmentList,lang);
        return response;
    }
   /* @RequestMapping(value = "/userApi/selectDepartment", method = RequestMethod.GET, produces = "text/plain")
    public String selectDepartment(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") int limit,   @ParamsNotNull @RequestParam(value = "page") int page){
        Customer customer=getCustomer(request);
        Department_Sql departmentSql=new Department_Sql();
        PageDepartment departments= departmentSql.selectPage(departmentMapper,page,limit,customer.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", departments.getTotal());
        jsonObject.put("data", departments.getDeviceList());
        return jsonObject.toString();
    }*/
    @RequestMapping(value = "/userApi/deletePerson", method = RequestMethod.GET, produces = "text/plain")
    public String deletePerson(HttpServletRequest request,  @RequestParam(value = "idcard") String idcard){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Person_Sql person_sql=new Person_Sql();
        person_sql.deletePerson(personMapper,idcard);
        if(personMap.get(idcard)!=null&&personMap.get(idcard).getIsbind()==1){
            if( wordcard_aMap.get(personMap.get(idcard).getBind_mac())!=null){
                wordcard_aMap.get(personMap.get(idcard).getBind_mac()).setIdcard("");
                wordcard_aMap.get(personMap.get(idcard).getBind_mac()).setIsbind(0);
            }
            WordCarda_Sql wordCarda_sql=new WordCarda_Sql();
            Wordcard_a wordCard_a=wordcard_aMap.get(personMap.get(idcard).getBind_mac());
            wordCarda_sql.update(wordCardaMapper,wordCard_a);
        }
        String response = JsonConfig.getJson(CODE_OK, null,lang);
        return response;
    }
    @RequestMapping(value = "/userApi/selectWordCard", method = RequestMethod.GET, produces = "text/plain")
    public String selectWordCard(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "mac") String mac){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        WordCarda_Sql wordCarda_sql=new WordCarda_Sql();
       /* Wordcard_a wordCard_a=new Wordcard_a();
        wordCard_a.setMac(mac);*/
        List<Wordcard_a> wordcard_aList =wordCarda_sql.getWordcard(wordCardaMapper,mac,customer.getUserkey());
        String response = JsonConfig.getJson(CODE_OK, wordcard_aList,lang);
        return response;
    }
    @RequestMapping(value = "/userApi/selectALlWordCard", method = RequestMethod.GET, produces = "text/plain")
    public String selectALlWordCard(HttpServletRequest request){
        WordCarda_Sql wordCarda_sql=new WordCarda_Sql();
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        List<Wordcard_a> wordcard_aList =wordCarda_sql.getAllWordCarda(wordCardaMapper,"","");
        String response = JsonConfig.getJson(CODE_OK, wordcard_aList,lang);
        return response;
    }
    @RequestMapping(value = "/userApi/unbindPerson", method = RequestMethod.GET, produces = "text/plain")
    public String unbindPerson(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "idcard") String idcard){
        Person_Sql person_sql=new Person_Sql();
        Person person=personMap.get(idcard);
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(person==null){
           // StationStatusTask.writeLog("接口 unbindPerson  解绑的用户不存在");
            return "解绑的用户不存在";
        }
        if(personMap.get(idcard)!=null&&personMap.get(idcard).getIsbind()==1){
            if( wordcard_aMap.get(personMap.get(idcard).getBind_mac())!=null){
                wordcard_aMap.get(personMap.get(idcard).getBind_mac()).setIdcard("");
                wordcard_aMap.get(personMap.get(idcard).getBind_mac()).setIsbind(0);
            }
            WordCarda_Sql wordCarda_sql=new WordCarda_Sql();
            Wordcard_a wordCard_a=wordcard_aMap.get(personMap.get(idcard).getBind_mac());
            wordCarda_sql.update(wordCardaMapper,wordCard_a);
            person.setIsopen(0);
            person.setIsbind(0);
            person.setBind_mac("");
            person_sql.update(personMapper,person);
        }
        String response = JsonConfig.getJson(CODE_OK, null,lang);
        return response;

    }

   /* @RequestMapping(value = "/userApi/getDeviceType", method = RequestMethod.GET, produces = "text/plain")
    public String getDeviceType(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") String limit, @ParamsNotNull @RequestParam(value = "page") String page,  @RequestParam(value = "name") String name){
       Customer customer=getCustomer(request);
        DevicePType_Sql devicePType_sql=new DevicePType_Sql();
        PageDevicePtype pageDevicePtype=devicePType_sql.selectPageDeviceP(devicepTypeMapper,Integer.valueOf(page),Integer.valueOf(limit),name,customer.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDevicePtype.getTotal());
        jsonObject.put("data", pageDevicePtype.getDeviceList());
        return jsonObject.toString();
    }*/
    /*@RequestMapping(value = "/userApi/addDeviceType", method = RequestMethod.GET, produces = "text/plain")
    public String addDeviceType(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "name") String name){
        Deviceptype devicePtype=devicePtypeMap.get(name);
        if(devicePtype!=null){
            String json=getJson(CODE_REPEAT,null);
            return json;
        }
        Customer user=getCustomer(request);
        DevicePType_Sql devicePType_sql=new DevicePType_Sql();
         devicePtype=new Deviceptype(name,user.getUserkey(),user.getCustomerkey());
        boolean status=devicePType_sql.check(devicepTypeMapper,devicePtype);
        if(!status){
            devicePType_sql.addDevicePType(devicepTypeMapper,devicePtype);
            devicePtypeMap.put(devicePtype.getId(),devicePtype);
            String json=getJson(CODE_OK,null);
            return json;
        }
        else{
            String json=getJson(CODE_REPEAT,null);
            return json;
        }
    }*/
    @RequestMapping(value = "/userApi/getPhoto", method = RequestMethod.GET, produces = "text/plain")
    public void getPhoto(HttpServletResponse response, HttpServletRequest request, @ParamsNotNull @RequestParam(value = "sn") String sn){


        String filePath = NewSystemApplication.paths;

        //String filePath = "E:\\蓝牙网关\\固件版本" ;
        File file = new File(filePath+"\\"+sn+".png");
        myPrintln("路劲="+file.getPath());
        if (file.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try {
                response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(sn + ".png", "UTF-8"));
            }
            catch (UnsupportedEncodingException e){

            }
            // response.setContentType("application/force-download");
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
                e.printStackTrace();
            }
            myPrintln("----------file download---" + sn);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else{
            myPrintln("文件异常"+file.getPath());
        }

    }
    @RequestMapping(value = "/userApi/getDevicerecord", method = RequestMethod.GET, produces = "text/plain")
    public String getDevicerecord(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") String limit, @ParamsNotNull @RequestParam(value = "page") String page,  @RequestParam(value = "name") String name
            , @RequestParam(value = "sn") String sn, @RequestParam(value = "type") String type,@RequestParam(value = "select_bind") String select_bind){
        Customer customer=getCustomer(request);
        DevicePrecord_Sql devicePrecord_sql=new DevicePrecord_Sql();
        PageDeviceP_record pageDeviceP=devicePrecord_sql.selectPageDeviceP(devicePRecordMapper,Integer.valueOf(page),Integer.valueOf(limit),sn,name,Integer.valueOf(type),Integer.valueOf(select_bind),customer.getUserkey());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data", pageDeviceP.getDeviceList());
        return jsonObject.toString();
    }
    /*@RequestMapping(value = "/userApi/getDevice", method = RequestMethod.GET, produces = "text/plain")
    public String getDevice(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") String limit, @ParamsNotNull @RequestParam(value = "page") String page,  @RequestParam(value = "name") String name
    , @RequestParam(value = "sn") String sn, @RequestParam(value = "type") String type,@RequestParam(value = "select_bind") String select_bind){
       Customer customer=getCustomer(request);
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        PageDeviceP pageDeviceP=deviceP_sql.selectPageDeviceP(devicePMapper,Integer.valueOf(page),Integer.valueOf(limit),sn,name,Integer.valueOf(type),Integer.valueOf(select_bind),customer.getUserkey());
        for( Devicep deviceP:pageDeviceP.getDeviceList() ){
            deviceP.setLasttime(devicePMap.get(deviceP.getSn()).getLasttime());
            deviceP.setType_name(devicePtypeMap.get(deviceP.getType_id()).getName());
            if(deviceP.getIsbind()==1&&deviceP.getBind_mac()!=null){
                myPrintln("报警状态="+beaconsMap.get(deviceP.getBind_mac()).getSos()+"==="+deviceP.getBind_mac());
                deviceP.setSos(beaconsMap.get(deviceP.getBind_mac()).getSos());
                deviceP.setRssi(beaconsMap.get(deviceP.getBind_mac()).getRssi());
                deviceP.setOnline(beaconsMap.get(deviceP.getBind_mac()).getOnline());
                deviceP.setLasttime(beaconsMap.get(deviceP.getBind_mac()).getLastTime());
                deviceP.setStation_mac(devicePMap.get(deviceP.getSn()).getStation_mac());
                deviceP.setPoint_name(devicePMap.get(deviceP.getSn()).getPoint_name());
            }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageDeviceP.getTotal());
        jsonObject.put("data", pageDeviceP.getDeviceList());
        return jsonObject.toString();
    }
*/


/*

    //添加资产
    @RequestMapping(value = "userApi/addDevice", method = RequestMethod.POST, produces = "text/plain")
    public String addDevice(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "name") String name,@ParamsNotNull @RequestParam(value = "sn") String sn,@ParamsNotNull @RequestParam(value = "type_id") int type_id,@ParamsNotNull @RequestParam(value = "beaconMac") String beaconMac, @RequestParam("file") MultipartFile file){
        String response = null;
        sn=sn.replaceAll(" ","");
        int isbind=0;
        if(beaconMac.equals("不绑定信标")){
            isbind=0;
            beaconMac="";
        }
        else{
            isbind=1;
        }
        Customer user1 = getCustomer(request);
        myPrintln("输出权限"+user1.getPermission());
        if(user1.getPermission()==null||user1.getPermission().getEditbeacon()==0){
            return JsonConfig.getJson(CODE_noP,null);
        }
        else if(user1.getPermission().getEditbeacon()==1 ){
            myPrintln("对象为" + user1.getUsername());
            String pathss=null;
            if(file!=null){
                File path = new File(paths);

                if (!path.exists()) {
                    myPrintln("文件夹不存在创建=" + path.mkdirs());
                }
                try {
                    file.transferTo(new File(path.getPath() + "/" + sn + ".png"));
                    pathss =path.getPath() + "/" + sn + ".png";
                }catch (Exception e){
                    myPrintln("保存文件异常");
                }
            }
            Devicep deviceP=new Devicep(type_id,name, "/userApi/getPhoto?sn="+sn,beaconMac,isbind,1,user1.getUserkey(),sn,0,user1.getCustomerkey());
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();

            boolean status=  deviceP_sql.addDeviceP(devicePMapper,deviceP);

            if(status){
                response = JsonConfig.getJson(CODE_OK, null);
                devicePMap.put(deviceP.getSn(),deviceP);
                if(beaconMac.length()>0){
                    Beacon beacon=beaconsMap.get(beaconMac);
                    beacon.setIsbind(1);
                    beacon.setDevice_sn(sn);
                    Beacon_Sql d=new Beacon_Sql();
                    d.update(beaconMapper,beacon);
                    response = JsonConfig.getJson(CODE_OK, null);
                }
            }else{
                response = JsonConfig.getJson(CODE_REPEAT, null);
            }
        }
        return response;
    }
*/


/*
    @RequestMapping(value = "/userApi/unbindDevice", method = RequestMethod.GET, produces = "text/plain")
    public String unbindDevice(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "sn") String sn){
        Customer user=getCustomer(request);
        String lang=user.getLang();
        if(user.getPermission().getEditbeacon()==0){
            String json=getJson(CODE_noP,null,lang);
            return json;
        }
        else{
            Devicep deviceP=devicePMap.get(sn);
            if(deviceP!=null&&deviceP.getBind_mac()!=null){
                Tag tag =beaconsMap.get(deviceP.getBind_mac());
                tag.setDevice_sn("");
                tag.setIsbind(0);
                tag.setDevice_name("");
                Tag_Sql tag_sql =new Tag_Sql();
                tag_sql.update(tagMapper, tag);
                deviceP.setIsbind(0);
                deviceP.setIsopen(0);
                deviceP.setBind_mac("");
                DeviceP_Sql deviceP_sql=new DeviceP_Sql();
                deviceP_sql.update(devicePMapper,deviceP);
                String json=getJson(CODE_OK,null,lang);
                return json;
            } else{
                String json=getJson(CODE_SQL_ERROR,null,lang);
                return json;
            }

        }

    }*/
    /*
    @RequestMapping(value = "/userApi/editDevice", method = RequestMethod.POST, produces = "text/plain")
    public String editDevice(HttpServletRequest request,
                             @ParamsNotNull @RequestParam(value = "sn") String sn,
                             @ParamsNotNull @RequestParam(value = "host") String host,
                             @ParamsNotNull @RequestParam(value = "name") String name,
                             @ParamsNotNull @RequestParam(value = "type") int type,
                             @RequestParam("file") MultipartFile file,
                             @ParamsNotNull @RequestParam(value = "bindmac") String bindmac) {
        Customer user=getCustomer(request);
        Beacon beacon;
        String pathss=null;
        if(file!=null){
            File path = new File(paths);

            if (!path.exists()) {
                myPrintln("文件夹不存在创建=" + path.mkdirs());
            }
            try {
                file.transferTo(new File(path.getPath() + "/" + sn + ".png"));
                pathss =path.getPath() + "/" + sn + ".png";
            }catch (Exception e){
                myPrintln("保存文件异常");
            }
        }
        if(user.getPermission().getEditbeacon()==0){
            String json=getJson(CODE_noP,null);
            return json;
        }else{
            Beacon_Sql beacon_sql=new Beacon_Sql();
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            Devicep deviceP=devicePMap.get(sn);
            deviceP.setType_id(type);
            deviceP.setName(name);
            deviceP.setType_name(devicePtypeMap.get(type).getName());
            deviceP.setPhoto("/userApi/getPhoto?sn="+deviceP.getSn());
            //已绑定，但是更换绑定信标或者解绑
            myPrintln("deviceP.getBind_mac()"+deviceP.getBind_mac());

            if (deviceP.getIsbind()==1&&!deviceP.getBind_mac().equals(bindmac)){
                //解绑原有信标
                 beacon=beaconsMap.get(deviceP.getBind_mac());
                beacon.setIsbind(0);
                beacon.setDevice_name("");
                beacon.setDevice_sn("");
                //更新信标
                beacon_sql.update(beaconMapper,beacon);
                //更改资产绑定
                if(bindmac.equals("不绑定信标")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    beacon=beaconsMap.get(bindmac);
                    beacon.setDevice_sn(deviceP.getSn());
                    beacon.setDevice_name(deviceP.getName());
                    beacon.setIsbind(1);
                    beacon_sql.update(beaconMapper,beacon);
                }


            }else if(deviceP.getIsbind()==0){
                //没有变更
                if(bindmac.equals("不绑定信标")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    beacon=beaconsMap.get(bindmac);
                    beacon.setDevice_sn(deviceP.getSn());
                    beacon.setDevice_name(deviceP.getName());
                    beacon.setIsbind(1);
                    beacon_sql.update(beaconMapper,beacon);
                }

            }
            deviceP_sql.update(devicePMapper,deviceP);
        }
        return getJson(CODE_OK,null);
    }*/
/*

    @RequestMapping(value = "/userApi/editDevice", method = RequestMethod.POST, produces = "text/plain")
    public String editDevice(HttpServletRequest request,
                             @ParamsNotNull @RequestParam(value = "sn") String sn,

                             @ParamsNotNull @RequestParam(value = "name") String name,
                             @ParamsNotNull @RequestParam(value = "type") int type,

                             @ParamsNotNull @RequestParam(value = "bindmac") String bindmac,   @ParamsNotNull @RequestParam(value = "person_name") String person_name,   @ParamsNotNull @RequestParam(value = "idcard") String idcard) {
        Customer user=getCustomer(request);
        Beacon beacon;


        if(user.getPermission().getEditbeacon()==0){
            String json=getJson(CODE_noP,null);
            return json;
        }else{
            Beacon_Sql beacon_sql=new Beacon_Sql();
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            Devicep deviceP=devicePMap.get(sn);
            deviceP.setType_id(type);
            deviceP.setName(name);
            deviceP.setJobnumber(jobnumber);
            deviceP.setPerson_name(person_name);
            deviceP.setType_name(devicePtypeMap.get(type).getName());
            deviceP.setPhoto("/userApi/getPhoto?sn="+deviceP.getSn());
            //已绑定，但是更换绑定信标或者解绑
            myPrintln("deviceP.getBind_mac()"+deviceP.getBind_mac());

            if (deviceP.getIsbind()==1&&!deviceP.getBind_mac().equals(bindmac)){
                //解绑原有信标
                beacon=beaconsMap.get(deviceP.getBind_mac());
                beacon.setIsbind(0);
                beacon.setDevice_name("");
                beacon.setDevice_sn("");
                //更新信标
                beacon_sql.update(beaconMapper,beacon);
                //更改资产绑定
                if(bindmac.equals("不绑定信标")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    beacon=beaconsMap.get(bindmac);
                    beacon.setDevice_sn(deviceP.getSn());
                    beacon.setDevice_name(deviceP.getName());
                    beacon.setIsbind(1);
                    beacon_sql.update(beaconMapper,beacon);
                }


            }else if(deviceP.getIsbind()==0){
                //没有变更
                if(bindmac.equals("不绑定信标")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    beacon=beaconsMap.get(bindmac);
                    beacon.setDevice_sn(deviceP.getSn());
                    beacon.setDevice_name(deviceP.getName());
                    beacon.setIsbind(1);
                    beacon_sql.update(beaconMapper,beacon);
                }

            }
            deviceP_sql.update(devicePMapper,deviceP);
        }
        return getJson(CODE_OK,null);
    }
*/
/*

    @RequestMapping(value = "/userApi/editDevicenoFile", method = RequestMethod.POST, produces = "text/plain")
    public String editDevicenoFile(HttpServletRequest request,
                             @ParamsNotNull @RequestParam(value = "sn") String sn,
                             @ParamsNotNull @RequestParam(value = "name") String name,
                             @ParamsNotNull @RequestParam(value = "type") String type,

                             @ParamsNotNull @RequestParam(value = "bindmac") String bindmac) {
        Customer user=getCustomer(request);
        Tag tag;

        myPrintln(name);
        String lang=user.getLang();
        if(user.getPermission().getEditbeacon()==0){
            String json=getJson(CODE_noP,null,lang);
            return json;
        }else{
            Tag_Sql tag_sql =new Tag_Sql();
            DeviceP_Sql deviceP_sql=new DeviceP_Sql();
            Devicep deviceP=devicePMap.get(sn);
            deviceP.setType(type);
            deviceP.setName(name);

            myPrintln("1111111111");
            //已绑定，但是更换绑定信标或者解绑
            if (deviceP.getIsbind()==1&&!deviceP.getBind_mac().equals(bindmac)){
                //解绑原有信标
                tag =beaconsMap.get(deviceP.getBind_mac());
                tag.setIsbind(0);
                tag.setDevice_name("");
                tag.setDevice_sn("");
                //更新信标
                tag_sql.update(tagMapper, tag);
                //更改资产绑定
                if(bindmac.equals("不绑定信标")||bindmac.equals("UnBind")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    tag =beaconsMap.get(bindmac);
                   // myPrintln("2222beacon="+beacon);
                    if(tag !=null){
                        tag.setDevice_sn(deviceP.getSn());
                        tag.setDevice_name(deviceP.getName());
                        tag.setIsbind(1);
                        tag_sql.update(tagMapper, tag);
                    }

                }
                deviceP_sql.update(devicePMapper,deviceP);

            }else if(deviceP.getIsbind()==0){
                //没有变更
                if(bindmac.equals("不绑定信标")||bindmac.equals("UnBind")){
                    deviceP.setBind_mac("");
                    deviceP.setIsopen(0);
                    deviceP.setIsbind(0);
                }else {//绑定新标签
                    deviceP.setBind_mac(bindmac);
                    deviceP.setIsopen(1);
                    deviceP.setIsbind(1);
                    tag =beaconsMap.get(bindmac);
                    myPrintln("111beacon="+ tag);
                    if(tag !=null){
                        tag.setDevice_sn(deviceP.getSn());
                        tag.setDevice_name(deviceP.getName());
                        tag.setIsbind(1);
                        tag_sql.update(tagMapper, tag);
                    }
                }
                deviceP_sql.update(devicePMapper,deviceP);
            }
            deviceP_sql.update(devicePMapper,deviceP);
        }
        return getJson(CODE_OK,null,lang);
    }

*/


    @RequestMapping(value = "/userApi/getCheckRecord", method = RequestMethod.GET, produces = "text/plain")
    public String getCheckRecord(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") String limit, @ParamsNotNull @RequestParam(value = "page") String page,@RequestParam(value = "time") String time,@RequestParam(value = "type") String type){
        Customer customer=getCustomer(request);
        CheckRecord_Sql checkRecordSql=new CheckRecord_Sql();
     //   PageCheckRecord pageCheckRecord=checkRecordSql.selectPageRecord(checkRecordMapper,Integer.valueOf(page),Integer.valueOf(limit),sn,name,Integer.valueOf(type),Integer.valueOf(select_bind));
        PageCheckRecord pageCheckRecord=checkRecordSql.selectPageRecord(checkRecordMapper,Integer.valueOf(page),Integer.valueOf(limit),time,Integer.valueOf(type),customer.getUserkey());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageCheckRecord.getTotal());
        jsonObject.put("data", pageCheckRecord.getCheckRecordList());
        return jsonObject.toString();
    }

    @RequestMapping(value = "/userApi/deleteCheckRecord", method = RequestMethod.GET, produces = "text/plain")
    public String deleteCheckRecord(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "id") int id){
        CheckRecord_Sql checkRecordSql=new CheckRecord_Sql();
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        checkRecordSql.delete(checkRecordMapper,id);
        return getJson(CODE_OK,null,lang);
    }



    @RequestMapping(value = "/userApi/checkRecord", method = RequestMethod.GET)
    @ResponseBody
    public void checkRecord(HttpServletResponse response) throws UnsupportedEncodingException {

        long thisTime = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Date date=new Date(thisTime);
            int count = devicePMap.size();
            int online = 0;
            int offline = 0;
            int unbind = 0;
            int onbind = 0;
            int sos_count = 0;
            Devicep deviceP;
            for (String sn : devicePMap.keySet()) {
                deviceP = devicePMap.get(sn);
              /*  if (deviceP.getIsbind() == 1) {
                    onbind++;
                    //只有绑定信标的设备才会有在线离线的说法。
                    if (deviceP.getOnline() == 1) {
                        online++;
                    } else {
                        offline++;
                    }
                    if (deviceP.getSos() == 1) {
                        sos_count++;
                    }
                } else {
                    unbind++;
                }*/
            }
            String[] titles = {"设备名称", "序列号", "绑定状态", "信标mac", "谁添加", "资产类型", "报警状态", "在线情况", "在线时间", "区域位置", "信号值", "网关mac", "电量", "入库时间"};
            //在这里进行添加excel
         //   SystemUtil.getUtil().createExcelTwo(paths + "/" + thisTime + ".xls", titles, devicePMap);
            //每个excel保存在数据库
         /*   Check_record check_record = new Check_record(df.format(date), online, offline, count, 0, sos_count, unbind, onbind,thisTime+"");
            CheckRecord_Sql checkRecordSql = new CheckRecord_Sql();
            checkRecordSql.addRecord(checkRecordMapper, check_record);*/



        String filePath = NewSystemApplication.paths;
        //String filePath = "E:\\蓝牙网关\\固件版本" ;
        File file = new File(filePath + "/" + thisTime+".xls");
        if (file.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(df.format(date)+".xls", "UTF-8"));
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
                e.printStackTrace();
            }
            myPrintln("----------file download---" + file.getPath());
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/userApi/deleteDevice", method = RequestMethod.GET, produces = "text/plain")
    public String deleteDevice(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "sn") String sn){
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Devicep deviceP=devicePMap.get(sn);
        if(deviceP!=null){
            deviceP_sql.delete(devicePMapper,sn);
            return getJson(CODE_OK,null,lang);
        }
        else{
            return getJson(CODE_SQL_ERROR,null,lang);
        }
    }
    //删除入库记录
    @RequestMapping(value = "/userApi/deleteDeviceRecord", method = RequestMethod.GET, produces = "text/plain")
    public String deleteDeviceRecord(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "sn") String sn){
        DevicePrecord_Sql deviceP_sql=new DevicePrecord_Sql();
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Devicep deviceP=devicePMap.get(sn);
        if(deviceP!=null){
            deviceP_sql.delete(devicePRecordMapper,sn);
            return getJson(CODE_OK,null,lang);
        }
        else{
            return getJson(CODE_SQL_ERROR,null,lang);
        }
    }


///其实是获取一个管理账号下的customer
    @RequestMapping(value = "/userApi/getAllUser", method = RequestMethod.GET, produces = "text/plain")
    public String getAllUser(HttpServletRequest request){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
       ArrayList<Customer> userArrayList=new ArrayList<>();
       for(String key:customerMap.keySet()){
           if(customerMap.get(key).getUserkey().equals(customer.getUserkey())){
               userArrayList.add(customerMap.get(key));
           }
       }
        return getJson(CODE_OK,userArrayList,lang);
    }

    @RequestMapping(value = "/userApi/addArea", method = RequestMethod.POST, produces = "text/plain")
    public String addArea(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "macs") String macs,@ParamsNotNull @RequestParam(value = "name") String name){
        Customer user=getCustomer(request);
        String lang=user.getLang();
        Area area=new Area(name,macs,user.getUserkey(),user.getCustomerkey());
        Area_Sql area_sql=new Area_Sql();
        area_sql.addArea(areaMapper,area);
        String[] macs_s=macs.split(",");
        Station_sql Station_sql=new Station_sql();

        return getJson(CODE_OK,null,lang);

    }



    @RequestMapping(value = "/userApi/editArea", method = RequestMethod.POST, produces = "text/plain")
    public String editArea(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "macs") String macs,@ParamsNotNull @RequestParam(value = "name") String name,@ParamsNotNull @RequestParam(value = "id") String id){
        Customer user=getCustomer(request);
        String lang=user.getLang();
        //把旧的区域以及对应的网关全部取消关联一次
        Area area=area_Map.get(Integer.valueOf(id));
        String oldMac=area.getStation_mac();

        Area_Sql area_sql=new Area_Sql();
        area.setStation_mac(macs);
        area.setName(name);
        area_sql.update(areaMapper,area);

        return getJson(CODE_OK,null,lang);

    }



    @RequestMapping(value = "/userApi/deleteArea", method = RequestMethod.POST, produces = "text/plain")
    public String deleteArea(HttpServletRequest request,  @ParamsNotNull @RequestParam(value = "id") String id){
        Customer user=getCustomer(request);
        String lang=user.getLang();
        Area area=area_Map.get(Integer.valueOf(id));
        Area_Sql area_sql=new Area_Sql();
        //删除这个区域
        area_sql.delete(areaMapper,Integer.valueOf(id));
        String macs=area.getStation_mac();

        return getJson(CODE_OK,null,lang);

    }
    @RequestMapping(value = "/userApi/getDeviceOffline", method = RequestMethod.GET, produces = "text/plain")
    public String getDeviceOffline(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "limit") String limit, @ParamsNotNull @RequestParam(value = "page") String page,@RequestParam(value = "name") String name,@RequestParam(value = "sn") String sn){
       Customer customer=getCustomer(request);
        DeviceOffline_Sql deviceOffline_sql=new DeviceOffline_Sql();
        //   PageCheckRecord pageCheckRecord=checkRecordSql.selectPageRecord(checkRecordMapper,Integer.valueOf(page),Integer.valueOf(limit),sn,name,Integer.valueOf(type),Integer.valueOf(select_bind));
        PageDeviceOffline pageCheckRecord=deviceOffline_sql.selectPage(deviceOfflineMapper,Integer.valueOf(page),Integer.valueOf(limit),name,sn,customer.getUserkey());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", CODE_OK);
        jsonObject.put("msg", CODE_OK_txt);
        jsonObject.put("count", pageCheckRecord.getTotal());
        jsonObject.put("data", pageCheckRecord.getDevice_offlines());
        return jsonObject.toString();
    }
    @RequestMapping(value = "/userApi/getCheckSheet", method = RequestMethod.GET, produces = "text/plain")
    public String getCheckSheet(HttpServletRequest request){
        //check_sheet
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Check_sheet check_sheet=check_sheetMap.get(customer.getUserkey());
                String response=getJson(CODE_OK,check_sheet,lang);
        return response;
    }
    @RequestMapping(value = "/userApi/setCheckSheet", method = RequestMethod.GET, produces = "text/plain")
    public String setCheckSheet(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "linetime") String linetime, @ParamsNotNull @RequestParam(value = "starttime") String starttime, @ParamsNotNull @RequestParam(value = "stoptime") String stoptime){
        //check_sheet
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        Check_sheet check_sheet=check_sheetMap.get(customer.getUserkey());

        CheckSheet_Sql checkSheet_sql=new CheckSheet_Sql();
        checkSheet_sql.update(checkSheetMapper,check_sheet);
        String response=getJson(CODE_OK,null,lang);
        return response;
    }

    @RequestMapping(value = "/userApi/addUser", method = RequestMethod.POST, produces = "text/plain")
    public String addUser(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "username") String username,@ParamsNotNull @RequestParam(value = "password") String password,@ParamsNotNull @RequestParam(value = "nickname") String nickname,@ParamsNotNull @RequestParam(value = "admin") String admin){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        if(admin==null||!admin.equals("andesen")){
            return JsonConfig.getJson(CODE_noP,null,lang);
        }
        User user=new User(username,password,nickname,"","all_permission");
        user.setUserkey(username);
        User_sql user_sql=new User_sql();
       int status= user_sql.addUser(userMapper,user);
       if(status==-1){
           return JsonConfig.getJson(CODE_REPEAT,null,lang);
       }
         customer=new Customer(username,password,nickname,"","all_permission",user.getUserkey(),"",1);
        Customer_sql customer_sql=new Customer_sql();
        customer_sql.addUser(customerMapper,customer);

        Check_sheet check_sheet=new Check_sheet();
        check_sheet.setHost("emqx");
        check_sheet.setSub("GwData");
        check_sheet.setPub("SrvData");
        check_sheet.setPort(1883);
        check_sheet.setLine_time(3);
        CheckSheet_Sql checkSheet_sql=new CheckSheet_Sql();
        checkSheet_sql.addCheck_sheet(checkSheetMapper,check_sheet);
        check_sheetMap=checkSheet_sql.getCheckSheet(checkSheetMapper);
        return JsonConfig.getJson(CODE_OK,null,lang);
    }
    @RequestMapping(value = "/userApi/deleteDeviceOffline", method = RequestMethod.POST, produces = "text/plain")
    public String deleteDeviceOffline(HttpServletRequest request,@ParamsNotNull @RequestParam(value = "id") int id){
        Customer customer=getCustomer(request);
        String lang=customer.getLang();
        DeviceOffline_Sql deviceOfflineSql=new DeviceOffline_Sql();
        deviceOfflineSql.delete(deviceOfflineMapper,id);
        String response=getJson(CODE_OK,null,lang);
        return response;
    }


}