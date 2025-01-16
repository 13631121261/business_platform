package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Bracelet;

import java.util.List;

public class PageBracelet {
    List<Bracelet> braceletList;
    long page;
    long total;

    public PageBracelet(List<Bracelet> braceletList,
                        long page,
                        long total) {
        this.braceletList = braceletList;
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

    public void setBraceletList(List<Bracelet> braceletList) {
        this.braceletList = braceletList;
    }

    public List<Bracelet> getBraceletList() {
        return braceletList;
    }
}