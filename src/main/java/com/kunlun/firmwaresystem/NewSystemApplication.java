package com.kunlun.firmwaresystem;
import com.kunlun.firmwaresystem.Tcp.NettyTcpServer;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.DeviceModel;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.HttpServletRequestReplacedFilter;
import com.kunlun.firmwaresystem.mappers.*;
import com.kunlun.firmwaresystem.mqtt.DirectExchangeProducer;
import com.kunlun.firmwaresystem.mqtt.MyMqttClient;
import com.kunlun.firmwaresystem.util.RedisUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Map;

@EnableCaching // 启用缓存功能
@EnableScheduling // 开启定时任务功能
@EnableTransactionManagement

@MapperScan(basePackages = "com.kunlun.firmwaresystem.mappers")
@SpringBootApplication
public class NewSystemApplication {
    @Autowired
    private static Mqtt mqtt;

    //本地参数
  // public static final String paths="E:\\蓝牙网关\\固件版本\\photo\\";
  //  public static final String url="http://192.168.1.14/download";
 //    public static final String host="http://localhost:801/";
    public static int beacon_time = 6;
    public static int wordcarda_time = 60;
//昆仑云参数

   public static final String paths = "C:\\Users\\Administrator\\Desktop\\WordSpace\\Web\\file\\photo\\";
  public static final String url = "http://120.77.232.76:80/download";

    //哲凌本地
   /* public static final String paths="D:\\kunlBluetooth\\上海环境监测蓝牙项目安装文件\\Server\\log\\";
    public static final String url="http://172.17.73.62:808/download";*/



    //信号在1米时的值暂定为-51；
    public static double rssi_At_1m = 47;
    public static RedisUtils redisUtil;
    public static WordCardaMapper wordCardaMapper;
    public static WifiMapper wifiMapper;
    public static TagMapper tagMapper;
    public static BraceletMapper braceletMapper;
    public static BleMapper bleMapper;
    private static DeviceModelMapper deviceModelMapper;
    public static RulesMapper rulesMapper;
    public static StationMapper StationMapper;
    public static RecordMapper recordMapper;
    public static MofflineMapper mofflineMapper;
    public static DirectExchangeProducer directExchangeProducer;
    public static Map<String, String> StationMap;
    public static Map<String, String> LocatorsMaps;


    public static Map<String, Rules> rulesMap;
    public static Map<String, Tag> beaconsMap;
    public static Map<String, Bracelet> braceletsMap;
    public static Map<String, MyMqttClient> myMqttClientMap;
    public static Map<String, Wordcard_a> wordcard_aMap;
    public static Map<String, Customer> customerMap;
    public static Map<String, Beacon_tag> beacon_tagMap;
    public static Map<Integer, Area> area_Map;
    public static List<DeviceModel> deviceModels;
    public static AreaMapper areaMapper;
    public static HistoryMapper historyMapper;
    public static AlarmMapper alarmMapper;
    public static Record_SosMapper recordSosMapper;
    public static UserMapper userMapper;
    public static BTagMapper bTagMapper;
    public static PersonMapper personMapper;
    public static Map<String,Person> personMap;
    public static Map<Integer,Fence> fenceMap;
    public static DevicePMapper devicePMapper;
    public static DeviceP_recordMapper devicePRecordMapper;
    public static Map<String, Devicep> devicePMap;

    public static Map<String,Check_sheet> check_sheetMap;
    public static CheckSheetMapper checkSheetMapper;
    public static CheckRecordMapper checkRecordMapper;
    public static CustomerMapper customerMapper;
    public static MapMapper mapMapper;
    public static  FenceMapper fenceMapper;
    public static  LocatorMapper locatorMapper;
    public static NettyTcpServer nettyTcpServer;
    public static FWordcardMapper fWordcardMapper;

    @Autowired
    public void setDataSource(Mqtt mqtt, FWordcardMapper fWordcardMapper, NettyTcpServer nettyTcpServer, HistoryMapper historyMapper, LocatorMapper locatorMapper, AlarmMapper alarmMapper, FenceMapper fenceMapper, MapMapper mapMapper, DeviceP_recordMapper devicePRecordMapper, MofflineMapper mofflineMapper, CheckRecordMapper checkRecordMapper, CheckSheetMapper checkSheetMapper, DevicePMapper devicePMapper, PersonMapper personMapper, BTagMapper bTagMapper, UserMapper userMapper, CustomerMapper customerMapper, Record_SosMapper recordSosMapper, AreaMapper areaMapper, WordCardaMapper wordCardaMapper, RecordMapper recordMapper, TagMapper tagMapper, BraceletMapper braceletMapper, WifiMapper wifiMapper, BleMapper bleMapper, RedisUtils redisUtil, DeviceModelMapper deviceModelMapper, DirectExchangeProducer topicExchangeProducer, StationMapper StationMapper, RulesMapper rulesMapper) {
        NewSystemApplication.mqtt=mqtt;
       NewSystemApplication.redisUtil = redisUtil;
        NewSystemApplication.fWordcardMapper=fWordcardMapper;
        NewSystemApplication.devicePRecordMapper =devicePRecordMapper;
        NewSystemApplication.deviceModelMapper = deviceModelMapper;
        NewSystemApplication.directExchangeProducer = topicExchangeProducer;
        NewSystemApplication.StationMapper = StationMapper;
        NewSystemApplication.rulesMapper = rulesMapper;
        NewSystemApplication.bleMapper = bleMapper;
        NewSystemApplication.nettyTcpServer=nettyTcpServer;
        NewSystemApplication.wifiMapper = wifiMapper;
        NewSystemApplication.tagMapper = tagMapper;
        NewSystemApplication.braceletMapper = braceletMapper;
        NewSystemApplication.recordMapper = recordMapper;
        NewSystemApplication.wordCardaMapper = wordCardaMapper;
        NewSystemApplication.areaMapper = areaMapper;
        NewSystemApplication.recordSosMapper = recordSosMapper;
        NewSystemApplication.userMapper = userMapper;
        NewSystemApplication.bTagMapper = bTagMapper;
        NewSystemApplication.personMapper=personMapper;
        NewSystemApplication.devicePMapper=devicePMapper;

        NewSystemApplication.checkSheetMapper=checkSheetMapper;
        NewSystemApplication.checkRecordMapper=checkRecordMapper;
        NewSystemApplication.customerMapper=customerMapper;
        NewSystemApplication.mofflineMapper=mofflineMapper;
        NewSystemApplication.mapMapper=mapMapper;
        NewSystemApplication.fenceMapper=fenceMapper;
        NewSystemApplication.alarmMapper=alarmMapper;
        NewSystemApplication.locatorMapper=locatorMapper;
        NewSystemApplication.historyMapper=historyMapper;
    }

    /* @Autowired
     private RedisUtils redisUtil;
     @Autowired
     DeviceModelMapper deviceModelMapper;*/
    public static void main(String[] args) {

        SpringApplication.run(NewSystemApplication.class, args);
        //  System.out.println("启动结果："+redisUtil+"====>"+deviceModelMapper);

        // client.addSubTopic("GwData");
 /*
        String id="178BFBFF00860F016FB55D78-B9E5-11EA-80DE-002B67BD78A6";

        try {
            File file=new File(paths+"kunlun");
            file.mkdir();
            file=new File(paths+"kunlun/kunlun.key");
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] bytes=id.getBytes();
            outputStream.write(bytes);
            outputStream.close();
        }catch (Exception e){
            return;
        }
//wmic csproduct get UUID
        //92E011EF-C31C-4DB4-869D-BE922BD1532B
       File file=new File(paths+"kunlun/kunlun.license");
        if(!file.exists()){
            System.out.println("需要激活文件，退出");
            System.exit(0);
            return;
        }else{
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] bytes=new byte[inputStream.available()];
                inputStream.read(bytes);
                String license=new String(bytes).replaceAll(" ","");
               System.out.println("验证吗="+license+"原来码="+(MachineCodeUtil.encode(id,"KUNLUN")+"acfc"));
                if(!(MachineCodeUtil.encode(id,"KUNLUN")+"acfc").equals(license)){
                    System.out.println("激活码不对");
                    System.exit(0);
                }
            }catch (Exception e){
                System.out.println("异常="+e);
                return;
            }
        }
*/

        boolean result = redisUtil.deleteAll();

        if (result) {
            System.out.println("成功删除所有的 key");
        } else {
            System.out.println("未能删除所有的 key");
        }
        System.out.println("线程=" + Thread.currentThread().getName());
        /*DeviceModel_sql deviceModel_sql = new DeviceModel_sql();
        deviceModels = deviceModel_sql.getAllModel(redisUtil, deviceModelMapper);
        Station_sql Station_sql = new Station_sql();
        StationMap = Station_sql.getAllStation(redisUtil, StationMapper);

        Rules_sql r = new Rules_sql();
        rulesMap = r.getAllRules(rulesMapper);

        Btag_Sql btag_sql = new Btag_Sql();
        beacon_tagMap = btag_sql.getAllBeacon(bTagMapper);

        Fence_Sql fence_sql=new Fence_Sql();
       fenceMap=  fence_sql.getAllFence(fenceMapper);
        Person_Sql person_sql=new Person_Sql();
        personMap=  person_sql.getAllPerson(personMapper);

        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
        Beacon_Sql beacon_sql = new Beacon_Sql();
        beaconsMap = beacon_sql.getAllBeacon(beaconMapper);
        Bracelet_Sql braceletSql=new Bracelet_Sql();
       braceletsMap= braceletSql.getAllBracelet(braceletMapper);
        WordCarda_Sql wordCarda_sql = new WordCarda_Sql();
        wordcard_aMap = wordCarda_sql.getAllWordCarda(wordCardaMapper);
     *//*   User_sql user_sql = new User_sql();
        userMap = user_sql.getAllUser(userMapper);*//*
        Customer_sql customer_sql = new Customer_sql();
        customerMap = customer_sql.getAllCustomer(customerMapper);
        CheckSheet_Sql checkSheet_sql=new CheckSheet_Sql();
        check_sheetMap=checkSheet_sql.getCheckSheet(checkSheetMapper);
        Area_Sql area_sql=new Area_Sql();
        area_Map= area_sql.getAllArea(areaMapper);

        Map_Sql map_sql=new Map_Sql();
        map_sql.getAllMap(mapMapper,redisUtil);
       *//*  String a="aaa";
         String[] b=a.split("1");
         System.out.println("长度="+b[0]);*//*
        *//*topicExchangeProducer.send("连接","connect");
        topicExchangeProducer.send("状态","state");
        topicExchangeProducer.send("扫描","scan_report");*//*

        List<Locator> locators=  locatorMapper.selectList(null);
        LocatorsMaps=new HashMap<>();*/


              System.out.println("配置="+mqtt.getServer());
            MyMqttClient client = new MyMqttClient(mqtt.getServer(),mqtt.getPort(),mqtt.getSubTopic(),"",0,"123","45","");
            client.start();
            //myMqttClientMap.put(key,client);
           /*   for (Map.Entry<String, Check_sheet> entry : check_sheetMap.entrySet()) {
                  String key = entry.getKey();
                  Check_sheet value = entry.getValue();
                  if(value!=null&& !value.getHost().isEmpty()){
                      new Thread(new Runnable() {
                          @Override
                          public void run() {
                      MyMqttClient client = new MyMqttClient(value.getHost(),value.getPort(),value.getSub(),value.getPub(),value.getQos(),value.getUser(),value.getPassword(),value.getProject_key());
                      client.start();
                      myMqttClientMap.put(key,client);
                          }
                      }).start();
                  }
              }*/

        MyWebSocket webSocket = MyWebSocket.getWebSocket();
        webSocket.start();
        MyWebSocketTag webSockettag = MyWebSocketTag.getWebSocket();
        webSockettag.start();


    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }

    @Bean
    public FilterRegistrationBean httpServletRequestReplacedRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestReplacedFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("httpServletRequestReplacedFilter");
        registration.setOrder(1);
        return registration;
    }

}
