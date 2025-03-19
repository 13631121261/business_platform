package com.kunlun.firmwaresystem.controllers;

import com.kunlun.firmwaresystem.NewSystemApplication;
import com.kunlun.firmwaresystem.entity.Ble_firmware;
import com.kunlun.firmwaresystem.entity.Wifi_firmware;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.BleMapper;
import com.kunlun.firmwaresystem.mappers.WifiMapper;
import com.kunlun.firmwaresystem.sql.Ble;
import com.kunlun.firmwaresystem.sql.Wifi;
import com.kunlun.firmwaresystem.util.constant;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

@RestController
public class FirmwareControl {
    @Autowired
    private WifiMapper wifiMapper;
    @Autowired
    private BleMapper bleMapper;

    /*  @RequestMapping(value = "/getFirmwareVersion",method = RequestMethod.GET)
      public String getFirmwareVersion(@RequestParam("type") String type,@RequestParam("mac") String mac,@RequestParam("wifi_version") String wifi_version,@RequestParam("ble_version") String ble_version) {
          myPrintln("Mac="+mac);
          myPrintln("type="+type);
          myPrintln("wifi_version="+wifi_version);
          myPrintln("ble_version="+ble_version);
          Wifi wifi=new Wifi();
          Wifi_firmware wifi_firmware= wifi.getVersionOnAuto(wifiMapper);
          Ble ble=new Ble();
          Ble_firmware ble_firmware= ble.getVersionOnAuto(bleMapper);
          JSONObject jsonObject=new JSONObject();
          jsonObject.put("code", constant.code_ok);
          jsonObject.put("type","getFirmwareResponse");
          if(wifi_firmware==null){
              jsonObject.put("wifi","null");
          }else {
              jsonObject.put("wifi", wifi_firmware);
          }
          if(ble_firmware==null){
              jsonObject.put("ble","null");
          }
          else {
              jsonObject.put("ble", ble_firmware);
          }
          myPrintln("json="+jsonObject.toString());
          return jsonObject.toString();
      }*/
    @RequestMapping(value = "/getCustomerFirmwareVersion", method = RequestMethod.GET)
    public String getCustomerFirmwareVersion(@RequestParam("userKey") String userKey) {
        Wifi wifi = new Wifi();
        List<Wifi_firmware> wifi_firmware = wifi.getCustomerVersion(wifiMapper, userKey);
        Ble ble = new Ble();
        List<Ble_firmware> ble_firmware = ble.getCustomerVersion(bleMapper, userKey);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", constant.code_ok);
        jsonObject.put("type", "getFirmwareResponse");
        if (wifi_firmware == null) {
            jsonObject.put("wifi", "null");
        } else {
            jsonObject.put("wifi", wifi_firmware);
        }
        if (ble_firmware == null) {
            jsonObject.put("ble", "null");
        } else {
            jsonObject.put("ble", ble_firmware);
        }
        myPrintln("json=" + jsonObject.toString());
        return jsonObject.toString();
    }
    //  String version;
    //    int id;
    //    String remake;
    //    String uploadtime;
    //    int customization;
    //    String customization_name;
    //    String finishtime;
    //    String author;
    //    String administrator;
    //    int auto;
/*    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,@RequestParam("type") String type,@RequestParam("version") String version,@RequestParam("customization") int customization,
                                       @RequestParam("customization_name") String customization_name,@RequestParam("finishtime") String finishtime,@RequestParam("author") String author,@RequestParam("administrator") String administrator,
                                    @RequestParam("auto") int auto,@RequestParam("remake") String remake) throws IOException {
        InetAddress address = null;
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        try {
            address = InetAddress.getLocalHost();
            myPrintln("输出地址="+address.getHostAddress()+"文件路径="+path.getName());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        myPrintln("[文件类型] - [{}]"+ file.getContentType());
        myPrintln("[文件名称] - [{}]"+ file.getOriginalFilename());
        myPrintln("[文件大小] - [{}]"+ file.getSize());

        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        myPrintln("当前时间为: " + ft.format(dNow));
        //保存
        JSONObject jsonObject=new JSONObject();
        if(file==null||type==null||version==null||finishtime==null||author==null||administrator==null){
            jsonObject.put("code",constant.code_error);
            jsonObject.put("msg","参数不完整");
            return jsonObject.toString();
        }else{
            file.transferTo(new File("C:\\Users\\Administrator\\Desktop\\Station\\Server_update\\firmware\\" + type+"_"+version+".firmware"));
            if(type.equals("ble")){
                Ble_firmware ble_firmware=new Ble_firmware( "http://"+"192.168.1.10"+":80/download",remake,   ft.format(dNow),  customization,  customization_name,  finishtime,  administrator,  "",  version);

                if(auto==1){
                    UpdateWrapper<Ble_firmware> bleFirmwareUpdateWrapper=new UpdateWrapper<>();
                    bleFirmwareUpdateWrapper.eq("auto",1).set("auto",0);
                    bleMapper.update(null,bleFirmwareUpdateWrapper);
                }
                int d=bleMapper.insert(ble_firmware);
                myPrintln("保存蓝牙固件数据的结果码="+d);
            }else if(type.equals("wifi")){
                Wifi_firmware wifi_firmware=new Wifi_firmware( "http://"+"47.241.68.29"+":8080/download",remake,   ft.format(dNow),  customization,  customization_name,  finishtime,  author,  administrator,  "",  version);
                if(auto==1){
                    UpdateWrapper<Wifi_firmware> wifiFirmwareUpdateWrapper=new UpdateWrapper<>();
                    wifiFirmwareUpdateWrapper.eq("auto",1).set("auto",0);
                    wifiMapper.update(null,wifiFirmwareUpdateWrapper);
                }
                int d=wifiMapper.insert(wifi_firmware);
                myPrintln("保存wifi数据的结果码="+d);
            }
            jsonObject.put("code",constant.code_ok);
            jsonObject.put("msg","上传成功");
        }

        return jsonObject.toString();
    }

   */

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void download(HttpServletRequest request,HttpServletResponse response, @RequestParam("type") String type, @RequestParam("version") String version, @RequestParam("userKey") String userKey) throws UnsupportedEncodingException {
        String filename = userKey + "/" + type + "_" + version + ".firmware";
      
        String filePath = NewSystemApplication.paths;

        //String filePath = "E:\\蓝牙网关\\固件版本" ;
        File file = new File(filePath + "/" + filename);
        if (file.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(type + "_" + version + ".firmware", "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myPrintln("----------file download---" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/userApi/downloadExcel", method = RequestMethod.GET)
    @ResponseBody
    public void downloadExcel(HttpServletResponse response, @ParamsNotNull @RequestParam("key") String key) throws UnsupportedEncodingException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");//设置日期格式
        Date date=new Date(Long.parseLong(key));
        String filePath = NewSystemApplication.paths;
        //String filePath = "E:\\蓝牙网关\\固件版本" ;
        File file = new File(filePath + "/" + key+".xls");
        if (file.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(df.format(date)+".xls", "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myPrintln("----------file download---" + file.getPath());
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
