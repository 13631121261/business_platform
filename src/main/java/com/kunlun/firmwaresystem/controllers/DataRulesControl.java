package com.kunlun.firmwaresystem.controllers;

import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Rules;
import com.kunlun.firmwaresystem.mappers.RulesMapper;
import com.kunlun.firmwaresystem.sql.Rules_sql;
import com.kunlun.firmwaresystem.util.RedisUtils;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class DataRulesControl {

    @Resource
    private RedisUtils redisUtil;
    @Resource
    private RulesMapper rulesMapper;

    @RequestMapping(value = "userApi/rules/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAllRules(HttpServletRequest request) {
        long a=System.currentTimeMillis();
        Customer customer = getCustomer(request);
        Rules_sql rules_sql=new Rules_sql();
       List<Rules> rulesList= rules_sql.getRules(rulesMapper,customer.getUserkey(),customer.getProject_key());
        Rules rules=new Rules();
        rules.setName("不转发数据");
        rules.setRule_key("noRules");
        rulesList.add(0,rules);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", rulesList.size());
        jsonObject.put("data", rulesList);
        return jsonObject;
    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }
}
