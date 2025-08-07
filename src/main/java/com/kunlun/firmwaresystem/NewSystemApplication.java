package com.kunlun.firmwaresystem;

import com.kunlun.firmwaresystem.Tcp.NettyTcpServer;
import com.kunlun.firmwaresystem.entity.*;
import com.kunlun.firmwaresystem.entity.device.DeviceModel;
import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.interceptor.HttpServletRequestReplacedFilter;
import com.kunlun.firmwaresystem.mappers.*;
import com.kunlun.firmwaresystem.mqtt.DirectExchangeProducer;
import com.kunlun.firmwaresystem.mqtt.MyMqttClient;
import com.kunlun.firmwaresystem.sql.*;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.kunlun.firmwaresystem.gatewayJson.Constant.redis_key_company;

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

    public static Map<String, History> historyMap = new ConcurrentHashMap<>();
    public static HashMap<String,Registration> registration_map = new HashMap<>();
    //信号在1米时的值暂定为-51；
    public static double rssi_At_1m = 47;
    public static RedisUtils redisUtil;
    public static WordCardaMapper wordCardaMapper;
    public static CallRecordMapper callRecordMapper;
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

    public static Map<String, String> station_maps;
    public static Map<String, Rules> rulesMap;
    public static Map<String, Tag> tagsMap;
    public static Map<String, Bracelet> braceletsMap;
    //public static Map<String, MyMqttClient> myMqttClientMap;
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
    public static CompanyMapper companyMapper;
    public static Map<String,Person> personMap;
    public static Map<Integer,Fence> fenceMap;
    public static Map<Integer,Fence_group> fenceGroupMap;

    public static DevicePMapper devicePMapper;
    public static DeviceP_recordMapper devicePRecordMapper;
    public static Map<String, Devicep> devicePMap;

    public static Map<String,Check_sheet> check_sheetMap;
    public static CheckSheetMapper checkSheetMapper;
    public static CheckRecordMapper checkRecordMapper;
    public static CustomerMapper customerMapper;
    public static MapMapper mapMapper;
    public static  FenceMapper fenceMapper;
    public static  FenceGroupMapper fenceGroupMapper;
    public static  LocatorMapper locatorMapper;
    public static NettyTcpServer nettyTcpServer;
    public static FWordcardMapper fWordcardMapper;
    public static GroupMapper groupMapper;
    public static StationMapper stationMapper;
    public static PatrolListMapper patrolListMapper;
    public static PatrolMapper patrolMapper;
    static ExecutorService  executorService;
    public  static  MyMqttClient mqttClient;
    public static   Real_PointMapper realPointMapper;
    @Autowired
    public void setDataSource(CompanyMapper companyMapper, CallRecordMapper callRecordMapper, Real_PointMapper realPointMapper,PatrolMapper patrolMapper,PatrolListMapper patrolListMapper,StationMapper stationMapper,GroupMapper groupMapper, FenceGroupMapper fenceGroupMapper,Mqtt mqtt, FWordcardMapper fWordcardMapper, NettyTcpServer nettyTcpServer, HistoryMapper historyMapper, LocatorMapper locatorMapper, AlarmMapper alarmMapper, FenceMapper fenceMapper, MapMapper mapMapper, DeviceP_recordMapper devicePRecordMapper, MofflineMapper mofflineMapper, CheckRecordMapper checkRecordMapper, CheckSheetMapper checkSheetMapper, DevicePMapper devicePMapper, PersonMapper personMapper, BTagMapper bTagMapper, UserMapper userMapper, CustomerMapper customerMapper, Record_SosMapper recordSosMapper, AreaMapper areaMapper, WordCardaMapper wordCardaMapper, RecordMapper recordMapper, TagMapper tagMapper, BraceletMapper braceletMapper, WifiMapper wifiMapper, BleMapper bleMapper, RedisUtils redisUtil, DeviceModelMapper deviceModelMapper, DirectExchangeProducer topicExchangeProducer, StationMapper StationMapper, RulesMapper rulesMapper) {
        NewSystemApplication.mqtt=mqtt;
        NewSystemApplication.companyMapper=companyMapper;
        NewSystemApplication.callRecordMapper=callRecordMapper;
        NewSystemApplication.realPointMapper=realPointMapper;
        NewSystemApplication.patrolListMapper=patrolListMapper;
        NewSystemApplication.patrolMapper=patrolMapper;
        NewSystemApplication.groupMapper=groupMapper;
        NewSystemApplication.fenceGroupMapper=fenceGroupMapper;
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
        executorService = Executors.newCachedThreadPool();

        NewSystemApplication.checkSheetMapper=checkSheetMapper;
        NewSystemApplication.checkRecordMapper=checkRecordMapper;
        NewSystemApplication.customerMapper=customerMapper;
        NewSystemApplication.mofflineMapper=mofflineMapper;
        NewSystemApplication.mapMapper=mapMapper;
        NewSystemApplication.fenceMapper=fenceMapper;
        NewSystemApplication.alarmMapper=alarmMapper;
        NewSystemApplication.locatorMapper=locatorMapper;
        NewSystemApplication.historyMapper=historyMapper;
        NewSystemApplication.stationMapper=stationMapper;
    }

    /* @Autowired
     private RedisUtils redisUtil;
     @Autowired
     DeviceModelMapper deviceModelMapper;*/
    private static final String SECRET_KEY =  "ThisIsA16ByteKey"; // 128/192/256位密钥
    private static final String INIT_VECTOR = "16ByteInitVector"; // 16字节IV

    // AES加密
    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // AES解密
    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String getSystemUUID() {
        SystemInfo systemInfo = new SystemInfo();
        ComputerSystem computerSystem = systemInfo.getHardware().getComputerSystem();
        return computerSystem.getHardwareUUID();
    }
    public static void main(String[] args) {
        String uuid= getSystemUUID();
        // 加密
        String encrypted = encrypt(uuid);
        System.out.println("加密后: " + encrypted);
        // 解密
        String decrypted = decrypt(encrypted);
        System.out.println("解密后: " + decrypted);
        // 验证
        System.out.println("验证结果: " + uuid.equals(decrypted));
        if(!uuid.equals(decrypted)){
            return;
        }
        SpringApplication.run(NewSystemApplication.class, args);

      //  myMqttClientMap=new HashMap<>();
        //  myPrintln("启动结果："+redisUtil+"====>"+deviceModelMapper);

        // client.addSubTopic("GwData");
 /*
        String id="178BFBFF00860F016FB55D78-B9E5-11EA-80DE-002B67BD78A6";

        try {C24186F5-6A82-5956-A4E1-C51F80E6CEDD
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
            myPrintln("需要激活文件，退出");
            System.exit(0);
            return;
        }else{
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] bytes=new byte[inputStream.available()];
                inputStream.read(bytes);
                String license=new String(bytes).replaceAll(" ","");
               myPrintln("验证吗="+license+"原来码="+(MachineCodeUtil.encode(id,"KUNLUN")+"acfc"));
                if(!(MachineCodeUtil.encode(id,"KUNLUN")+"acfc").equals(license)){
                    myPrintln("激活码不对");
                    System.exit(0);
                }
            }catch (Exception e){
                myPrintln("异常="+e);
                return;
            }
        }
*/

        boolean result = redisUtil.deleteAll();

        if (result) {
            myPrintln("成功删除所有的 key");
        } else {
            myPrintln("未能删除所有的 key");
        }
        myPrintln("线程=" + Thread.currentThread().getName());
        CheckSheet_Sql checkSheet_sql=new CheckSheet_Sql();
        check_sheetMap=checkSheet_sql.getCheckSheet(checkSheetMapper);
        Tag_Sql tagSql = new Tag_Sql();
        tagsMap = tagSql.getAllTag(tagMapper);
        DeviceP_Sql deviceP_sql=new DeviceP_Sql();
        devicePMap=deviceP_sql.getAllDeviceP(devicePMapper);
        Fence_Sql fence_sql=new Fence_Sql();
        fenceMap=  fence_sql.getAllFence(fenceMapper);
        Fence_Group_Sql fenceGroupSql=new Fence_Group_Sql();
        fenceGroupMap=fenceGroupSql.getAll(fenceGroupMapper);
        Station_sql stationSql=new Station_sql();
        station_maps =stationSql.getAllStation(redisUtil,stationMapper);
        Person_Sql personSql=new Person_Sql();
        personMap=personSql.getAllPerson(personMapper);
        Company_Sql companySql=new Company_Sql();
        List<Company> list= companySql.getAll(companyMapper);
        for (Company company:list) {
            redisUtil.setnoTimeOut(redis_key_company+company.getId(), company);
        }
       //
        // Person_Sql person_sql=new Person_Sql();
        /*DeviceModel_sql deviceModel_sql = new DeviceModel_sql();
        deviceModels = deviceModel_sql.getAllModel(redisUtil, deviceModelMapper);
        Station_sql Station_sql = new Station_sql();
        StationMap = Station_sql.getAllStation(redisUtil, StationMapper);

        Rules_sql r = new Rules_sql();
        rulesMap = r.getAllRules(rulesMapper);

        Btag_Sql btag_sql = new Btag_Sql();
        beacon_tagMap = btag_sql.getAllBeacon(bTagMapper);


        personMap=  person_sql.getAllPerson(personMapper);


        Bracelet_Sql braceletSql=new Bracelet_Sql();
       braceletsMap= braceletSql.getAllBracelet(braceletMapper);
        WordCarda_Sql wordCarda_sql = new WordCarda_Sql();
        wordcard_aMap = wordCarda_sql.getAllWordCarda(wordCardaMapper);
     *//*   User_sql user_sql = new User_sql();
        userMap = user_sql.getAllUser(userMapper);*//*
        Customer_sql customer_sql = new Customer_sql();
        customerMap = customer_sql.getAllCustomer(customerMapper);

        Area_Sql area_sql=new Area_Sql();
        area_Map= area_sql.getAllArea(areaMapper);

        Map_Sql map_sql=new Map_Sql();
        map_sql.getAllMap(mapMapper,redisUtil);
       *//*  String a="aaa";
         String[] b=a.split("1");
         myPrintln("长度="+b[0]);*//*
        *//*topicExchangeProducer.send("连接","connect");
        topicExchangeProducer.send("状态","state");
        topicExchangeProducer.send("扫描","scan_report");*//*

        List<Locator> locators=  locatorMapper.selectList(null);
        LocatorsMaps=new HashMap<>();*/

            new Thread(new Runnable() {
                public void run() {

                    myPrintln("配置="+mqtt.getServer());
                    mqttClient = new MyMqttClient(mqtt.getServer(),mqtt.getPort(),mqtt.getSubTopic(),"",0,"business"+System.currentTimeMillis(),"45","");
                    mqttClient.start();
                   // myMqttClientMap.put("default",client);
                }
            }).start();

            /*  for (Map.Entry<String, Check_sheet> entry : check_sheetMap.entrySet()) {
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
        WebSocket_Registration webSocketRegistration = WebSocket_Registration.getWebSocket();
        webSocketRegistration.start();

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


    // 示例：使用队列批量处理日志
    private BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private void startLogConsumer() {
        executorService.submit(() -> {
            while (!Thread.interrupted()) {
                String log = null;
                try {
                    log = logQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 实际输出到文件或控制台
                System.out.println(log);
            }
        });
    }

    // 修改 myPrintln 方法
    public void myPrintlns(String msg) {
        logQueue.offer(msg); // 非阻塞写入队列
    }
    public static void myPrintln(String log){

            StackWalker.getInstance().walk(frames -> {
                StackWalker.StackFrame frame = frames
                        .skip(1) // 跳过当前 walk 方法的调用
                        .findFirst()
                        .orElseThrow();
                // 提取信息
                String fileName = frame.getFileName();
                int lineNumber = frame.getLineNumber();
                String methodName = frame.getMethodName();

                // 打印结果
                System.out.println("文件: " + fileName + "   方法: " + methodName + "   行号: " + lineNumber + "  Log=" + log);
                return null;
            });
        }


}
