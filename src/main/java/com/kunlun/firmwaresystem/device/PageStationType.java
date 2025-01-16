package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.StationType;

import java.util.List;

public class PageStationType {
    List<StationType> stationTypeList;
    long page;
    long total;

    public PageStationType(List<StationType> alarmList,
                           long page,
                           long total) {
        this.stationTypeList = alarmList;
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

    public void setStationTypeList(List<StationType> stationTypeList) {
        this.stationTypeList = stationTypeList;
    }

    public List<StationType> getStationTypeList() {
        return stationTypeList;
    }
}