package com.kunlun.firmwaresystem.entity.check;

import com.kunlun.firmwaresystem.entity.Station;

import java.util.ArrayList;
import java.util.List;

public class DevStatus<T> {
    int online_sum;
    int sum;
    int offline_sum;
    List<T> online_list;
    List<T> offline_list;

    public DevStatus(){
        online_sum=0;
        sum=0;
        offline_sum=0;
        offline_list=new ArrayList<>();
        online_list=new ArrayList<>();
    }
    public void addOffLine(T t){
        offline_sum++;
       // offline_list.add(t);
        sum++;
    }

    public void addOnLine(T t){
        online_sum++;
       // online_list.add(t);
        sum++;
    }
    public int getOnline_sum() {
        return online_sum;
    }

    public void setOnline_sum(int online_sum) {
        this.online_sum = online_sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getOffline_sum() {
        return offline_sum;
    }

    public void setOffline_sum(int offline_sum) {
        this.offline_sum = offline_sum;
    }

    public List<T> getOnline_list() {
        return online_list;
    }

    public void setOnline_list(List<T> online_list) {
        this.online_list = online_list;
    }

    public List<T> getOffline_list() {
        return offline_list;
    }

    public void setOffline_list(List<T> offline_list) {
        this.offline_list = offline_list;
    }
}
