package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Permission;

import java.util.List;

public class PagePermission {
    List<Permission> permissionList;
    long page;
    long total;

    public PagePermission(List<Permission> permissionList,
                          long page,
                          long total) {
        this.permissionList = permissionList;
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

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}