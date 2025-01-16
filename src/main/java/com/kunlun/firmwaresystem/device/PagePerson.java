package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.Person;

import java.util.List;

public class PagePerson {
    List<Person> personList;
    long page;
    long total;

    public PagePerson(List<Person> personList,
                      long page,
                      long total) {
        this.personList = personList;
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

    public void setBeaconList(List<Person> personList) {
        this.personList = personList;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    @Override
    public String toString() {
        return "PageBeacon{" +
                "beaconList=" + personList +
                ", page=" + page +
                ", total=" + total +
                '}';
    }
}