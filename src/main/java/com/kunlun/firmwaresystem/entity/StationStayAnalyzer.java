package com.kunlun.firmwaresystem.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StationStayAnalyzer {

    public static List<StationStay> analyzeStationStays(List<History> historyList) {
        List<StationStay> result = new ArrayList<>();

        if (historyList == null || historyList.isEmpty()) {
            return result;
        }




        for (History record : historyList) {
            result.add(new StationStay(record.getSn(), record.getStation_mac(), record.getStart_time(), record.getEnd_time(),record.getX(),record.getY()));
        }



        return result;
    }

    // Station stay record class
    public static class StationStay {
        public String sn;           // Device serial number
        public String stationMac;   // Station MAC address
        public String station_name;   // Station MAC address
        public long startTime;      // Start timestamp
        public long endTime;        // End timestamp
        public double x;
        public double y;
        String name;
        int duration_sec;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

        public StationStay(String sn, String stationMac, long startTime, long endTime,double x,double y) {
            this.sn = sn;
            this.stationMac = stationMac;
            this.startTime = startTime;
            this.endTime = endTime;
            if (endTime==0){
                this.endTime = startTime;
            }
            this.x = x;
            this.y=y;
            duration_sec=(int) ((endTime-startTime)/1000);

        }

        public void setDuration_sec(int duration_sec) {
            this.duration_sec = duration_sec;
        }

        public int getDuration_sec() {
            return duration_sec;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setStation_name(String station_name) {
            this.station_name = station_name;
        }

        public String getStation_name() {
            return station_name;
        }





        public String getStationMac() {
            return stationMac;
        }

    }


}
