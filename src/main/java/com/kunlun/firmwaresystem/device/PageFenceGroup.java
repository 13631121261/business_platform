package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Fence_group;
import com.kunlun.firmwaresystem.entity.Logs;

import java.util.List;

public class PageFenceGroup {
    List<Fence_group> fenceGroups;
    long page;
    long total;

    public PageFenceGroup(List<Fence_group> mapList,
                          long page,
                          long total) {
        this.fenceGroups = mapList;
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

    public void setFenceGroups(List<Fence_group> fenceGroups) {
        this.fenceGroups = fenceGroups;
    }

    public List<Fence_group> getFenceGroups() {
        return fenceGroups;
    }

}