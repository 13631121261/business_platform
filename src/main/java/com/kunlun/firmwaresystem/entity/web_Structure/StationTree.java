package com.kunlun.firmwaresystem.entity.web_Structure;

import java.util.List;

public class StationTree {
    int id;
    String address;
    String label;
    boolean disabled;
    boolean is_append=false;
    List<StationTree> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disable) {
        this.disabled = disable;
    }

    public boolean isIs_append() {
        return is_append;
    }

    public void setIs_append(boolean is_append) {
        this.is_append = is_append;
    }

    public void setChildren(List<StationTree> children) {
        this.children = children;
    }

    public List<StationTree> getChildren() {
        return children;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
