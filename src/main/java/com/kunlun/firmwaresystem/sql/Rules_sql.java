package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageRules;
import com.kunlun.firmwaresystem.entity.Rules;
import com.kunlun.firmwaresystem.mappers.RulesMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rules_sql {
    public boolean addRules(RulesMapper rulesMapper, Rules rules) {
        if (!checkRules(rulesMapper, rules)) {
            rulesMapper.insert(rules);
            return true;
        } else {
            return false;
        }
    }


    public PageRules selectPageRules(RulesMapper rulesMapper, int page, int limt, String userKey) {
        LambdaQueryWrapper<Rules> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Rules> userPage = new Page<>(page, limt);
        IPage<Rules> userIPage;
        userLambdaQueryWrapper.eq(Rules::getUser_key,userKey);
        userIPage = rulesMapper.selectPage(userPage, userLambdaQueryWrapper);
        System.out.println("总页数： " + userIPage.getPages());
        System.out.println("总记录数： " + userIPage.getTotal());
        //   userIPage.getRecords().forEach(System.out::println);
        PageRules pageRules = new PageRules(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageRules;
    }

    public List<Rules> selectAllRules(RulesMapper rulesMapper, String userKey) {
        QueryWrapper<Rules> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", userKey);
        List<Rules> a = rulesMapper.selectList(queryWrapper);
        return a;
    }

    public Map<String, Rules> getAllRules(RulesMapper rulesMapper,String user_key,String project_key) {
        QueryWrapper<Rules> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key", project_key);
        queryWrapper.eq("user_key", user_key);
        List<Rules> rulesList = rulesMapper.selectList(queryWrapper);
        HashMap<String, Rules> rulesMap = new HashMap<>();
        for (Rules rules : rulesList) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            rulesMap.put(rules.getRule_key(), rules);
        }
        return rulesMap;

    }
    public Map<String, Rules> getAllRules(RulesMapper rulesMapper) {
        List<Rules> rulesList = rulesMapper.selectList(null);
        HashMap<String, Rules> rulesMap = new HashMap<>();
        for (Rules rules : rulesList) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            rulesMap.put(rules.getRule_key(), rules);
        }
        return rulesMap;

    }
    public List<Rules> getRules(RulesMapper rulesMapper,String user_key,String project_key) {
        QueryWrapper<Rules> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key", project_key);
        queryWrapper.eq("user_key", user_key);
        List<Rules> rulesList = rulesMapper.selectList(queryWrapper);
        return rulesList;

    }

    public void delete(RulesMapper rulesMapper, String rules_key, String userkey) {
        QueryWrapper<Rules> queryWrapper = Wrappers.query();
        queryWrapper.eq("rule_key", rules_key);
        queryWrapper.eq("user_key", userkey);
        rulesMapper.delete(queryWrapper);
    }

    /**
     * 查询是否已c存在此网关设备
     */
    private boolean checkRules(RulesMapper rulesMapper, Rules rules) {
        QueryWrapper<Rules> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", rules.getName());
        queryWrapper.eq("user_key", rules.getCustomer_key());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Rules> a = rulesMapper.selectList(queryWrapper);
        if (a != null && a.size() > 0) {
            return true;
        } else
            return false;
    }
}
