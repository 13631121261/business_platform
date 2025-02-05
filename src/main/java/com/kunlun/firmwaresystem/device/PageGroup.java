package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.device.Group;

import java.util.List;

public class PageGroup {
    List<Group> groupList;
    long page;
    long total;

    public PageGroup(List<Group> groupList,
                     long page,
                     long total) {
        this.groupList = groupList;
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

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Group> getGroupList() {
        return groupList;
    }
}