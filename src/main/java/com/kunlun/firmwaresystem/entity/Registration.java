package com.kunlun.firmwaresystem.entity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class Registration {
    int time;
    boolean run=false;
    HashMap<String,Person> personList;
    CallRecord callRecord;
    String path;

    public Registration(int time,CallRecord callRecord) {
        this.time = time;
        this.personList = new HashMap<>();
       this. callRecord = callRecord;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public CallRecord getCallRecord() {
        return callRecord;
    }

    public void addPerson(Person person) {
        myPrintln("人员状态="+person.getIdcard());
        this.personList.put(person.getIdcard(), person);
    }
    public void clearPerson() {
        this.personList=new HashMap<>();
    }
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isRun() {
        return run;
    }

    public Registration setRun(boolean run) {
        this.run = run;
        return this;
    }

    public HashMap<String,Person> getPersonList() {
        return personList;
    }


}
