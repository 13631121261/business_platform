package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Rules;

import java.util.List;

public class PageRules {
    List<Rules> rulesList;
    long page;
    long total;

    public PageRules(List<Rules> rulesList,
                     long page,
                     long total) {
        this.rulesList = rulesList;
        this.page = page;
        this.total = total;
    }


    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Rules> getRulesList() {
        return rulesList;
    }

    public void setRulesList(List<Rules> rulesList) {
        this.rulesList = rulesList;
    }

    @Override
    public String toString() {
        return "PageStationConfig{" +
                "rulesList=" + rulesList +
                ", page=" + page +
                ", total=" + total +
                '}';
    }
}