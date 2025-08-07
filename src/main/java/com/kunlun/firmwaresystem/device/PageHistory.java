package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Fence;
import com.kunlun.firmwaresystem.entity.History;

import java.util.List;

public class PageHistory {
    List<History> historyList;
    long page;
    long total;

    public PageHistory(List<History> historyList,
                       long page,
                       long total) {
        this.historyList = historyList;
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

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }
}