package com.kunlun.firmwaresystem;

import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.CheckRecordMapper;
import com.kunlun.firmwaresystem.sql.CheckRecord_Sql;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;

@Component
public class DeviceTask {
    @Autowired
    private CheckRecordMapper checkRecordMapper;
    private HashMap<String, Devicep> DevicePMap;

    int runcount = 0;
    int runH=0;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");//设置日期格式1

    @Scheduled(cron = "*/59 * * * * ?")
    public void execute() throws Exception
    {
        runcount++;
        if (runcount > 10000) {
            runcount = 0;
        }
   /* for(String key:check_sheetMap.keySet()){
        long thisTime = System.currentTimeMillis()/1000;
        Date date=new Date(thisTime);
        int h=date.getHours();
        myPrintln("小时="+h);
        int stoptime= check_sheetMap.get(key).getStoptime();
        int starttime= check_sheetMap.get(key).getStarttime();
      //  stoptime=1;
        long dtime=60*60*(stoptime-starttime);
        myPrintln("资产设备定时检测运行  60秒一次-------------------------------------------"+runcount);
        myPrintln(stoptime+"时 才会检测-------------------------------------------");

        //每个时间周期只执行一次
        if(h==stoptime&&runH!=h) {
            myPrintln("资产盘点  "+stoptime+"小时一次-------------------------------------------"+runcount);
            runH = h;
            runcount = 0;
            int count = 0;
            int online = 0;
            int offline = 0;
            int unbind = 0;
            int onbind = 0;
            int sos_count = 0;
            int check_count=0;
            int check_countout=0;
            Devicep deviceP=null;
            //    DeviceOffline_Sql deviceOffline_sql=new DeviceOffline_Sql();
            for (String sn : devicePMap.keySet()) {
                deviceP = devicePMap.get(sn);
                if(!deviceP.getUserkey().equals(key)){
                    continue;
                }

                //不是这个账号下的设备，不在这里做判断

                count++;
                myPrintln("输出多少次" + runcount++ + "  sn= " + sn);
                if (deviceP.getIsbind() == 1) {
                    onbind++;
                    //只有绑定信标的设备才会有在线离线的说法。
                    //当前在线状态，也就是最新在线
                    if (deviceP.getOnline() == 1) {
                        online++;
                    } else {
                        offline++;
                        //     deviceOffline_sql.addDeviceOffline(deviceOfflineMapper,new Device_offline(deviceP.getSn(),deviceP.getName(),deviceP.getRssi(),deviceP.getBt(),deviceP.getBind_mac(),deviceP.getPhoto(),deviceP.getType_id(),deviceP.getPoint_name(),deviceP.getStation_mac(),deviceP.getLasttime()));
                    }
                    if (deviceP.getSos() == 1) {
                        sos_count++;
                    }
                    //在盘点时间内在线，和普通在线离线的区别就在于，假设盘点间隔是12小时，只需要在这12小时内在线，都属于在线
                    if(deviceP.getLasttime()>thisTime-dtime){
                        check_count++;
                    }//盘点时间内 不在线
                    else{
                        check_countout++;
                    }
                } else {
                    unbind++;
                }
            }
            if(count==0){
                return;
            }
            String[] titles = {"设备名称", "序列号", "绑定状态", "信标mac", "谁添加", "资产类型", "报警状态", "在线情况", "在线时间", "区域", "信号值", "网关mac", "电量", "入库时间"};
            //在这里进行添加excel
            createExcelTwo(paths + "/" + thisTime + ".xls", titles, devicePMap);
            //每个excel保存在数据库
            Check_record check_record = new Check_record(df.format(date), online, offline, count,check_count,check_countout, 1, sos_count, unbind, onbind,thisTime+"",key,deviceP.getCustomer_key());
            CheckRecord_Sql  checkRecordSql = new CheckRecord_Sql();
            checkRecordSql.addRecord(checkRecordMapper, check_record);
        }
        else{
            myPrintln("等到"+stoptime+"在运行");
        }
    }*/

    }

    /*public   void  createExcelTwo(String path , String[] title, Map<String, Devicep> map_list) {
        HSSFWorkbook workbook =new  HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row=null;
        row = sheet.createRow(0);
        HSSFCell cell= null;
        int i=0;
        for(String t:title){
            cell=row.createCell(i);
            cell.setCellValue(t);
            i++;
        }
        i=1;
        Devicep deviceP;
        for(String sn:map_list.keySet()){
            deviceP=map_list.get(sn);
            row=sheet.createRow(i);
            i++;
            cell=row.createCell(0);
            cell.setCellValue(deviceP.getName());
            cell=row.createCell(1);
            cell.setCellValue(deviceP.getSn());
            cell=row.createCell(2);
            if(deviceP.getIsbind()==1){
                cell.setCellValue("已绑定信标");
            }else{
                cell.setCellValue("未绑定信标");
            }
            cell=row.createCell(3);
            cell.setCellValue(deviceP.getBind_mac());
            cell=row.createCell(4);
            cell.setCellValue(customerMap.get(deviceP.getCustomer_key()).getNickname());
            cell=row.createCell(5);
            cell.setCellValue(deviceP.getType());
            cell=row.createCell(6);
            if(deviceP.getSos()==1){
                cell.setCellValue("触发报警");
            }else{
                cell.setCellValue("正常");
            }
            cell=row.createCell(7);
            if(deviceP.getOnline()==1){
                cell.setCellValue("在线");
            }else {
                cell.setCellValue("离线");
            }
            cell=row.createCell(8);
            cell.setCellValue(deviceP.getLasttime());
            cell=row.createCell(9);
            cell.setCellValue(deviceP.getPoint_name());
            cell=row.createCell(10);
            cell.setCellValue(deviceP.getRssi());
            cell=row.createCell(11);
            cell.setCellValue(deviceP.getStation_mac());
            cell=row.createCell(12);
            cell.setCellValue(deviceP.getBt()+"V");
            cell=row.createCell(13);
            cell.setCellValue(deviceP.getCreatetime());
        }
        File file = new File(path);
        try{
            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            workbook.write(stream);
            stream.close();
        }catch (IOException e){
            myPrintln("盘点记录文件保存异常"+e.getMessage());
        }

    }*/
    public static void writeLog(String log) {
        /*try {
            String fileName = paths + "log.txt";
          //  myPrintln("文件名称=" + fileName);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try {
                FileWriter writer = new FileWriter(fileName, true);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(new Date());
                writer.write("\n\t" + time);
                writer.write("\n\t" + log);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            myPrintln("文件夹创建失败");
        }*/
    }
}