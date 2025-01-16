package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Tag;

import java.util.List;

public class PageTag {
    List<Tag> tagList;
    long page;
    long total;

    public PageTag(List<Tag> tagList,
                   long page,
                   long total) {
        this.tagList = tagList;
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

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "PageBeacon{" +
                "beaconList=" + tagList +
                ", page=" + page +
                ", total=" + total +
                '}';
    }
}