package com.kunlun.firmwaresystem.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunlun.firmwaresystem.device.PageTag;
import com.kunlun.firmwaresystem.entity.Tag;
import com.kunlun.firmwaresystem.entity.Customer;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.ParamsNotNull;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.sql.Tag_Sql;
import com.kunlun.firmwaresystem.sql.DeviceP_Sql;
import com.kunlun.firmwaresystem.util.JsonConfig;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.SystemUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.tag_address;
import static com.kunlun.firmwaresystem.util.JsonConfig.*;

@RestController
public class TagControl {
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private StationMapper StationMapper;

    @RequestMapping(value = "userApi/Tag/index", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAlltag(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        Tag_Sql tag_sql =new Tag_Sql();
        String quickSearch=request.getParameter("quickSearch");
        String pages=request.getParameter("page");
        String limits=request.getParameter("limit");
        int page=1;
        int limit=10;
        if (!StringUtils.isBlank(pages)) {
            page=Integer.parseInt(pages);
        }
        if (!StringUtils.isBlank(limits)) {
            limit=Integer.parseInt(limits);
        }
        if (StringUtils.isBlank(quickSearch)) {
            quickSearch="";
        }

        PageTag pageTag = tag_sql.selectPageTag(tagMapper,page,limit,quickSearch,customer.getUserkey(),customer.getProject_key());
        if(pageTag.getTagList().size()>0){
            for(Tag tag : pageTag.getTagList()){
                Tag tag1 =(Tag)redisUtil.get(tag_address+tag.getMac());
                if(tag1!=null){
                    tag.setMap_key(tag1.getMap_key());
                    tag.setOnline(tag1.getOnline());
                    tag.setLastTime(tag1.getLastTime());
                }
                }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", pageTag.getTotal());
        jsonObject.put("data", pageTag.getTagList());
         return jsonObject;
    }

    @RequestMapping(value = "userApi/Tag/index1", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getAlltag1(HttpServletRequest request) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        String type=request.getParameter("type");
        Tag_Sql tag_sql =new Tag_Sql();
        System.out.println("类型="+type);
        List<Tag> tagList = tag_sql.getunAllTag(tagMapper,customer.getUserkey(),customer.getProject_key(),type);
        JSONObject jsonObject = new JSONObject();
        if(lang!=null&&lang.equals("en")){
            tagList.add(0,new Tag("UnBind"));
        }
        else{
            tagList.add(0,new Tag("不绑定标签"));
        }

        jsonObject.put("code", 1);
        jsonObject.put("msg", "ok");
        jsonObject.put("count", tagList.size());
        jsonObject.put("data", tagList);
        return jsonObject;
    }



    @RequestMapping(value = "userApi/Tag/del", method = RequestMethod.POST, produces = "application/json")
    public JSONObject deletetag(HttpServletRequest request, @RequestBody JSONArray jsonArray) {
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Tag_Sql tag_sql =new Tag_Sql();
        List<Integer> id=new ArrayList<Integer>();
        for(Object ids:jsonArray){
            if(ids!=null&&ids.toString().length()>0){
                id.add(Integer.parseInt(ids.toString()));
            }
        }
        if(id.size()>0){
            int status = tag_sql.deletes(tagMapper, id);
            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }else{
            return JsonConfig.getJsonObj(CODE_PARAMETER_NULL,null,lang);
        }
    }
    @RequestMapping(value = "userApi/Tag/add", method = RequestMethod.POST, produces = "application/json")
    public JSONObject addtag(HttpServletRequest request, @RequestBody JSONObject json) {
        System.out.println(json.toString());
        Customer customer = getCustomer(request);
        String lang=customer.getLang();
        Tag_Sql tag_sql =new Tag_Sql();
        Tag tag =new Gson().fromJson(json.toString(),new TypeToken<Tag>(){}.getType());
        tag.setUser_key(customer.getUserkey());
        tag.setProject_key(customer.getProject_key());
        tag.setCreatetime(System.currentTimeMillis()/1000);
        tag.setCustomer_key(customer.getCustomerkey());
        if(tag.getMac()!=null){
            tag.setMac(tag.getMac().replaceAll(" ","").toLowerCase());
        }
        switch (tag.getType()){
            case 1:
                tag.setRun(-1);
                tag.setSos(-1);
                break;
             case 2:
                 tag.setRun(-1);
                 tag.setSos(0);
                    break;
            case 3:
                tag.setRun(0);
                tag.setSos(-1);
                break;
            case 4:
                tag.setRun(0);
                tag.setSos(0);
                break;
        }
        boolean status= tag_sql.addTag(tagMapper, tag);
        if(status){

            return JsonConfig.getJsonObj(CODE_OK,null,lang);
        }
          else{
            return JsonConfig.getJsonObj(CODE_REPEAT,null,lang);
            }
    }
     @RequestMapping(value = "/userApi/uploadTag", method = RequestMethod.POST)
    public JSONObject uploadtag(HttpServletRequest request, @RequestParam("data") MultipartFile file, HttpServletResponse response)
             throws IOException {
         System.out.println("ssssssssssss" + file.getName());
         Customer customer = getCustomer(request);
         File outfile = new File(file.getName()+System.currentTimeMillis() + ".xlsx");
         outfile.createNewFile();
         System.out.println("666" + file.getName());
         if (file == null) {
          return  JsonConfig.getJsonObj(CODE_13,null,customer.getLang());
         }
         ArrayList<HashMap<String, String>> data = null;
         try {
             data = SystemUtil.readExcel(file, new String[]{"mac", "type"});
         } catch (Exception e) {
             System.out.println("特别特别" + e);
         }
         if (data == null) {
                 return  JsonConfig.getJsonObj(CODE_PARAMETER_TYPE_ERROR,null,customer.getLang());
         } else {
             Tag_Sql tag_sql = new Tag_Sql();
             for (Map<String, String> map : data) {
                 System.out.println("循环");
                 Tag tag = new Tag();
                 String types = map.get("type");
                 String mac=map.get("mac");
                 if(types==null||types.equals("")||mac==null||mac.equals("")){
                     map.put("result", "Failed, incomplete data");
                     continue;
                 }
                 int type = 0;
                 switch (types) {
                     case "KTBB818":
                         type = 1;
                         break;
                     case "KTBB818-K":
                         type = 2;
                         break;
                     case "KTBB818-A":
                         type = 3;
                         break;
                     case "KTBB818-KA":
                         type = 4;

                         break;
                     case "AOA":
                         type = 5;
                         break;
                     default:
                         map.put("result", "Failed, tag type does not match");
                         continue;
                 }
                 tag.setType(type);
                 tag.setProject_key(customer.getProject_key());
                 tag.setUser_key(customer.getUserkey());
                 tag.setMac(map.get("mac"));
                 boolean status = tag_sql.addTag(tagMapper, tag);
                 if (status) {

                     map.put("result", "Import was successful");
                 } else {
                     map.put("result", "Import failed, data duplication or other anomalies. Please contact the administrator");
                 }
             }
             SystemUtil.writeExcel(outfile, new String[]{"mac","type","result"}, data);
             String file_name=outfile.getAbsolutePath();
             System.out.println("文件保存地址="+file_name);
             redisUtil.set("file_name",file_name);
             return JsonConfig.getJsonObj(CODE_OK,file_name,customer.getLang());

         }
     }

    @RequestMapping(value = "/userApi/downResult", method = RequestMethod.GET)
    @ResponseBody
    public void checkRecord(HttpServletResponse response, @ParamsNotNull @RequestParam(value = "file_name") String file_name) throws UnsupportedEncodingException {



        file_name=file_name.replaceAll("5678","//").replaceAll("8765",":");
        //String filePath = "E:\\蓝牙网关\\固件版本" ;
        File file = new File(file_name);
        if (file.exists()) { //判断文件父目录是否存在q
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=result.xls" );
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
            System.out.println("----------file download---" + file.getPath());
            try {
                bis.close();
                fis.close();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("删除文件异常");
            }
        }
    }
  /*  @RequestMapping(value = "userApi/uploadtag", method = RequestMethod.POST, produces = "application/json")
    public String upload(MultipartHttpServletRequest request){
        System.out.println("上传信标");
        Enumeration<String> parameter= request.getParameterNames();
        String project_key="";
        while (parameter.hasMoreElements()) {

            project_key=project_key+request.getParameter(parameter.nextElement());
        }
        if(project_key.length()>0){
            System.out.println("旧的输出key=" +project_key);
        }else{
            System.out.println("需要新的key  异常"  );
        }
        Map<String, MultipartFile> map = request.getFileMap();
        List<MultipartFile> files=new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
            if(entry.getKey().equals("tag.xlsx")){
                byte[] data = null;
                try {

                    data = entry.getValue().getBytes();
                    // System.out.println(svg_data);
                    File outfile=new File("result.xlsx");
                    outfile.createNewFile();
                    FileOutputStream inputStream=new FileOutputStream(outfile);
                    inputStream.write(data);

                    return map_key;
                } catch (IOException ioException) {
                    System.err.println("File Error!");
                    return  "";
                }
            }
            //    System.out.println(entry.getKey());
            //   System.out.println(entry.getValue().getSize());
            files.add(entry.getValue());
        }

        return  "";
    }*/
    @RequestMapping(value = "userApi/getTagByMap", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getStationbyMap(HttpServletRequest request, @ParamsNotNull @RequestParam(value = "map_key") String map_key) {
        // System.out.println(System.currentTimeMillis());
        Customer customer = getCustomer(request);
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        Map<String,Devicep> deviceps=deviceP_sql.getAllDeviceP(devicePMapper,customer.getUserkey(),customer.getProject_key());
        ArrayList<Devicep> deviceps1=new ArrayList<>();



        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "ok");
            jsonObject.put("count", deviceps1.size());
            jsonObject.put("data", deviceps1);
            // System.out.println(System.currentTimeMillis());
            return jsonObject;}catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    private Customer getCustomer(HttpServletRequest request) {
        String  token=request.getHeader("batoken");
        Customer customer = (Customer) redisUtil.get(token);
        //   System.out.println("customer="+customer);
        return customer;
    }


}
