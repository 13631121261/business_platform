package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Company;

import java.util.List;

public class PageCompany {
    List<Company> companyList;
    long page;
    long total;

    public PageCompany(List<Company> companyList,
                       long page,
                       long total) {
        this.companyList = companyList;
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

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    @Override
    public String toString() {
        return "PageCompany{" +
                "companyList=" + companyList +
                ", page=" + page +
                ", total=" + total +
                '}';
    }
}