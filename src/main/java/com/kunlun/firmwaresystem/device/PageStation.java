package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Station;

import java.util.List;

public class PageStation {
    List<Station> StationList;
    long page;
    long total;

    public PageStation(List<Station> StationList,
                       long page,
                       long total) {
        this.StationList = StationList;
        this.page = page;
        this.total = total;
    }

    public List<Station> getStationList() {
        return StationList;
    }

    public void setStationList(List<Station> StationList) {
        this.StationList = StationList;
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

    @Override
    public String toString() {
        return "PageStation{" +
                "StationList=" + StationList +
                ", page=" + page +
                ", total=" + total +
                '}';
    }
}