package com.kunlun.firmwaresystem.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Logs;
import com.kunlun.firmwaresystem.mappers.LogsMapper;
import com.kunlun.firmwaresystem.sql.Logs_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kunlun.firmwaresystem.controllers.UserControl.ExpireTime;

public class RedisSessionInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private LogsMapper logsMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       // myPrintln("handler="+handler);
        Customer customer=null;
        String token = request.getHeader("batoken");
        if(token==null||token.equals("")){
            token=request.getParameter("token");
        }
        if(token!=null){
            Object o=redisTemplate.opsForValue().get(token );
            if(o==null){
                response401(response);
                return false;
            }
            if(o.getClass().getName().contains("Customer")){
                customer=(Customer) (redisTemplate.opsForValue().get(token ));
               // myPrintln("获取信息"+customer);
               String lang= request.getParameter("lang");

               if(lang!=null){
                   customer.setLang(lang);
                  // myPrintln("语言"+ customer.getLang());
                  // myPrintln("11获取信息"+customer);
               }
            }
        }
       /* String loginSessionId =(String) redisTemplate.opsForValue().get("sessionId:" + session.getId());
        String tokenId = (String)redisTemplate.opsForValue().get("tokenId:" + token);
*/
        if (customer == null) {
            response401(response);
            return false;
        }
        //更新时间
        redisTemplate.opsForValue().set("tokenId:"+customer.getCustomerkey() , token, 600, TimeUnit.SECONDS);
        //更新时间
        redisTemplate.opsForValue().set(token, customer, 600,TimeUnit.SECONDS);
        customer=(Customer) (redisTemplate.opsForValue().get(token ));
       // myPrintln("22获取信息"+customer);
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }
        List<String> list = getParamsName((HandlerMethod) handler);
        for (String s : list) {
            String parameter = request.getParameter(s);
            if (StringUtils.isBlank(parameter)) {
                String result = JsonConfig.getJson(JsonConfig.CODE_PARAMETER_NULL, s,"en");
                response.setHeader("Content-type", "application/json;charset=UTF-8");
                response.setHeader("Access-Control-Allow-Origin", "*");//跨域
                response.getWriter().write(result);
                return false;
            }
        }
        if(handler.toString().contains("setMenu")){
            return true;
        }
        if(handler.toString().contains("getStationbyMap")){
            return true;
        }
        if(handler.toString().contains("getAoALocatorByMap")){
            return true;
        }
        if(handler.toString().contains("getAllMap1")){
            return true;
        }
        if(handler.toString().contains("getAreaByMap")){
            return true;
        }
        if(handler.toString().contains("getAoALocatorByMap")){
            return true;
        }
        if(handler.toString().contains("getRoute")){
            return true;
        }
        if(handler.toString().contains("getLogs")){
            return true;
        }
        if(handler.toString().contains("Logs")){
            return true;
        }
        Logs logs=new Logs();
        logs.setAgent(request.getHeader("User-Agent"));
        logs.setCreate_time(System.currentTimeMillis()/1000);
        logs.setCustomer_key(customer.getCustomerkey());
        logs.setIp(request.getRemoteAddr());
        logs.setNickname(customer.getNickname());
        logs.setProject_key(customer.getProject_key());
        logs.setUrl(request.getRequestURI());
        logs.setUsername(customer.getUsername());
        logs.setUserkey(customer.getUserkey());
        String op=getOP(request.getRequestURI(),customer.getLang());
        logs.setOperation(op);
        String method=request.getMethod();

        if(method.contains("GET")){
            List<String> lists = getParamsName1(request);
            String data="";
            for (String s : lists) {

                String parameter = request.getParameter(s);
                data=s+"="+parameter+"&"+data;
            }
            if(data==null){
                data=" ";
            }
           JSONObject jsonObject= new JSONObject();

            jsonObject.put("data", data);
            logs.setData(jsonObject.toString());

        }else if(method.contains("POST")){
         String data  = HttpHelper.getBodyString(new RequestReaderHttpServletRequestWrapper(request));
            logs.setData(data);
        }
        Logs_Sql logs_sql=new Logs_Sql();
        logs_sql.add(logsMapper,logs);

        return true;

        //无论访问的地址是不是正确的，都进行登录验证，登录成功后的访问再进行分发，404的访问自然会进入到错误控制器中


       /* if (session.getAttribute("userKey") != null)
        {
            try
            {
                //验证当前请求的session是否是已登录的session
                String loginSessionId = redisTemplate.opsForValue().get("sessionId:" +session.getId());
                if (loginSessionId != null && loginSessionId.equals(session.getId()))
                {
                    return true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        response401(response);
        return false;*/
    }
    private String getOP(String data,String lang){
        if(data.contains("Logs")){
            return "";
        }
        String b="";
        String a="";
        switch (lang){
            case "en":
                if(data.contains("edit")){
                    b="Edit";
                }else if(data.contains("del")){
                    b="Delete";
                }
                else if(data.contains("add")){
                    b="Add";
                } else if(data.contains("index")){
                    b="View";
                }else if(data.contains("addProject")){
                    b="Project-Add";
                }
                else if(data.contains("getAllProject")){
                    b="Project-Show";
                }  else if(data.contains("deleteProject")){
                    b="Project-Delete";
                }
                else if(data.contains("updateProject")){
                    b="Project-Update";
                }
                String s[]=data.split("/");
                if(s!=null&&s.length>=3){
                    //     myPrintln(s[2]);
                    switch (s[2]){
                        case "Station":
                            a="Station";
                            break;
                        case "Customer":
                            a="Administrator";
                            break;
                        case "Roles":
                            a="Group Roles";
                            break;

                    }
                }
                if(a.length()>0){
                    b=a+"-"+b;
                }
                break;
            default:
                if(data.contains("edit")){
                    b="编辑";
                }else if(data.contains("del")){
                    b="删除";
                }
                else if(data.contains("add")){
                    b="添加";
                } else if(data.contains("index")){
                    b="查看";
                }else if(data.contains("addProject")){
                    b="项目-添加";
                }
                else if(data.contains("getAllProject")){
                    b="项目-查看全部";
                }  else if(data.contains("deleteProject")){
                    b="项目-删除";
                }
                else if(data.contains("updateProject")){
                    b="项目-更新";
                }
                    String s1[]=data.split("/");
                    if(s1!=null&&s1.length>=3){
                        //     myPrintln(s[2]);
                        switch (s1[2]){
                            case "Station":
                                a="网关管理";
                                break;
                            case "Customer":
                                a="项目管理员";
                                break;
                            case "Roles":
                                a="角色组管理";
                                break;

                        }
                    }
                    if(a.length()>0){
                        b=a+"-"+b;
                    }
                    break;
        }




        return b;
    }

    private byte[] getParameterByte(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();

        if (contentLength <=0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readLen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readLen == -1) {
                break;
            }
            i += readLen;
        }

        return buffer;
    }

    private void response401(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().print(JsonConfig.getJson1(JsonConfig.CODE_ReLogin, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private List getParamsName(HandlerMethod handlerMethod) {
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        List<String> list = new ArrayList<>();
        for (Parameter parameter : parameters) {
            //判断这个参数时候被加入了 ParamsNotNull. 的注解
            //.isAnnotationPresent()  这个方法可以看一下
          //  myPrintln("参数11="+parameter.getName());
            if (parameter.isAnnotationPresent(ParamsNotNull.class)) {
                list.add(parameter.getName());
            }
        }
        return list;
    }
    private List getParamsName1(HttpServletRequest request) {
        Enumeration parameters = request.getParameterNames();
        List<String> list = new ArrayList<>();
        while (parameters.hasMoreElements()){
            list.add(parameters.nextElement().toString());
        }
        return list;
    }
}
