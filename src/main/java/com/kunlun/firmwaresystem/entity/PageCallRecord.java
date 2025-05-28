package com.kunlun.firmwaresystem.entity;

import java.util.List;

public class PageCallRecord {
    List<CallRecord> callRecords;
    long page;
    long total;

    public PageCallRecord(List<CallRecord> callRecords,
                          long page,
                          long total) {
        this.callRecords = callRecords;
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

    public void setCallRecords(List<CallRecord> callRecords) {
        this.callRecords = callRecords;
    }

    public List<CallRecord> getCallRecords() {
        return callRecords;
    }
}