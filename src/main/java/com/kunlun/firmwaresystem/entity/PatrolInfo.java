package com.kunlun.firmwaresystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;

public  class PatrolInfo {
    long startTime;
    long endTime;
    int status;
    int area_id;
    String area_name;
    int minStayMinutes;
    @TableField(exist = false)
    String[] enableDays;
    String enableDay;
    String projext_key;
    String user_key;
    String name;

    public void setEnableDay(String enableDay) {
        this.enableDay = enableDay;
        if (enableDay != null && !enableDay.isEmpty()) {
            String[] enableDayss = enableDay.split(",");
            int s=0;

            for (int i = 0; i < enableDayss.length; i++) {
                if (enableDayss[i] == null || enableDayss[i].isEmpty()) {
                    s++;
                }

            }
            String[] enableDays =new String[s];
            int j=0;
            for (int i = 0; i < enableDayss.length; i++) {
                if (enableDayss[i] != null &&! enableDayss[i].isEmpty()) {
                    enableDays[j]=enableDayss[i];
                    j++;
                }
            }
            this.enableDays = enableDays;
        }
    }

    public String getEnableDay() {
        return enableDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getProjext_key() {
        return projext_key;
    }

    public void setProjext_key(String projext_key) {
        this.projext_key = projext_key;
    }

    public String[] getEnableDays() {
        return enableDays;
    }

    public void setEnableDays(String[] enableDays) {
        this.enableDays = enableDays;
    }

    public int getMinStayMinutes() {
        return minStayMinutes;
    }

    public void setMinStayMinutes(int minStayMinutes) {
        this.minStayMinutes = minStayMinutes;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}