package com.kunlun.firmwaresystem.util;


import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mqtt.RabbitMessage;
import com.kunlun.firmwaresystem.sql.Station_sql;
import com.kunlun.firmwaresystem.sql.Moffline_Sql;
import net.sf.json.JSONObject;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.kunlun.firmwaresystem.DeviceTask.writeLog;
import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.mqtt.DirectExchangeRabbitMQConfig.sendtoMap;

public class StringUtil {

    private static int id = 0;
    private static final char HexCharArr[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String HexStr = "0123456789ABCDEF";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //int 转字节数组
    public static byte[] intTo4ByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    //long 转字节数组
    public static byte[] intTo4ByteArray(long i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    public static String byteArrToHex(byte[] btArr) {
        char strArr[] = new char[btArr.length * 2];
        int i = 0;
        for (byte bt : btArr) {
            strArr[i++] = HexCharArr[bt >>> 4 & 0xf];
            strArr[i++] = HexCharArr[bt & 0xf];
        }
        return new String(strArr);
    }

    public static byte[] hexToByteArr(String hexStr) {
        hexStr = hexStr.replaceAll(" ", "");
        char[] charArr = hexStr.toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }
    //int 转字节数组
    public static long ByteToLong(byte[] data) {
        long a= (data[0]&0xFF)*16777216;
        long b=(data[1]&0xff)*65536;
        long c=(data[2]&0xff)*256;
        long d=(data[3]&0xff);
        long e=a+b+c+d;
        return e*1000;
    }
    //int 转字节数组
    public static long eByteToLong(byte[] data) {
        long a= (data[0]&0xFF)*281474976710656l;
        long b=(data[1]&0xff)*1099511627776l;
        long c=(data[2]&0xff)*4294967296l;
        long d=(data[3]&0xff)*16777216;
        long e=(data[4]&0xff)*65536;
        long f=(data[5]&0xff)*256;
        long g=(data[6]&0xff);
        long h=a+b+c+d+e+f+g;
        return h;
    }
    //小端转大端
    public static String LtoB(byte[] l) {
        byte[] b = new byte[l.length];
        for (int i = 0; i < l.length; i++) {
            b[i] = l[l.length - i];
        }
        return byteArrToHex(b);
    }
    //字节数组转int
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < bytes.length; i++) {
            int shift = (bytes.length - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }


    //int 转字节数组
    public static byte[] intTo2ByteArray(int i) {
        byte[] result = new byte[2];
        // 由高位到低位
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

















  /* public static void  createExcelTwo( String path, Map<String, List<Integer>>map_list) {
       try {
        //创建Excel文件薄
        HSSFWorkbook workbook=new HSSFWorkbook();

        //创建工作表sheeet
        HSSFSheet sheet = workbook.createSheet();
        //创建第一行
       HSSFRow row = sheet.createRow(0);

       HSSFCell cell;
       int i=0;
        try {
            for (String key : map_list.keySet()) {
                cell = row.createCell(i);
                cell.setCellValue(key.split("==")[1]);
                i++;
            }
        }catch (Exception e){
            myPrintln("异常在这里="+e.toString());
        }
       i=0;
       for (String key:map_list.keySet()) {
           List<Integer> list=map_list.get(key);
           if(i==0){
               for(int j=1;j<=list.size();j++){
                   row = sheet.createRow(j);
                   cell = row.createCell(i);
                   cell.setCellValue(list.get(j-1));
               }
           }else{
               for(int j=1;j<=list.size();j++){
                   row = sheet.getRow(j);
                   if(row==null){
                       row=sheet.createRow(j);
                   }
                   cell = row.createCell(i);
                   cell.setCellValue(list.get(j-1));
               }
           }
           i++;
       }
        File file = new File("D:\\测试数据\\"+ path);

            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            workbook.write(stream);
            stream.close();
            myPrintln("----保存文档成功");
        }catch (Exception e){
            myPrintln("----"+e.toString());
        }


    }

*/

    //室内定位，计算位置公式




    //人员围栏报警
    public static void sendFenceSosPerson(Person person) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("data", person);
                jsonObject1.put("type", "fence_person");
                RabbitMessage rabbitMessage1 = new RabbitMessage("", jsonObject1.toString(),person.getProject_key());
                directExchangeProducer.send(rabbitMessage1.toString(), "sendtoHtml");
    }
    //资产围栏报警
    public static void sendFenceSosDevice(Devicep devicep) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("data", devicep);
        jsonObject1.put("type", "fence_devicep");
        RabbitMessage rabbitMessage1 = new RabbitMessage("", jsonObject1.toString(),devicep.getProject_key());
        directExchangeProducer.send(rabbitMessage1.toString(), "sendtoHtml");
    }

    //给地图推送位置
    public static void sendTagPush(ArrayList<Object> devicep, String map_key ) {
       // myPrintln("原始" +devicep.size() );
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("device", devicep);
        id++;
        jsonObject1.put("id", id);
        jsonObject1.put("time", sdf.format(new Date()));
       // myPrintln("原始" + jsonObject1);
      //  myPrintln("原始" + map_key);
        RabbitMessage rabbitMessage1 = new RabbitMessage("", jsonObject1.toString(),map_key);
       directExchangeProducer.send(rabbitMessage1.toString(), sendtoMap);
    }
    //针对客户的项目配置，转发原始数据
    public static void sendRelayPush(ArrayList<Object> devicep, String map_key ) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("tag", devicep);
        id++;
        jsonObject1.put("id", id);
        jsonObject1.put("time", sdf.format(new Date()));
        //myPrintln("原始" + map_key);
        //myPrintln("原始" + map_key);
        RabbitMessage rabbitMessage1 = new RabbitMessage("", jsonObject1.toString(),map_key);
        //directExchangeProducer.send(rabbitMessage1.toString(), sendtoMap);
    }


    public static String unzip(byte[] data) throws IOException,
            DataFormatException {

        Inflater inf = new Inflater();
            inf.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while(!inf.finished())
        {
            int count = inf.inflate(buffer);
            baos.write(buffer, 0, count);
        }
            baos.close();
            data=baos.toByteArray();

           String json = new String(data, 0, data.length);
            return json;

}


   /*     //保存设备的离线以及在线记录
    public static void saveRecord(String mac,long lasttime,String userkey,int type,int status,String project_key){
        Moffline moffline=new Moffline(mac,type,status,lasttime,userkey,project_key);
        Moffline_Sql mofflineSql=new Moffline_Sql();
        mofflineSql.addMoffline(mofflineMapper,moffline);

    }*/
}
