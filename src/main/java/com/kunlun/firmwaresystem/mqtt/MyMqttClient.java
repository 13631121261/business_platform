package com.kunlun.firmwaresystem.mqtt;

import com.kunlun.firmwaresystem.NewSystemApplication;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class MyMqttClient {

    private MqttClient client = null;
    private String content = "Hello World";
    private int qos = 2;
    private String host;
    private int port;
    private String clientId = "Client12_Paal";
    private MessageCallback messageCallback;
    private String sub,pub,user,password;
    public  ExecutorService executorService;
    public   int connect_count=0;
    HashMap<String,String> sub_topic;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    public   MyMqttClient(String host,int port,String sub,String pub,int Qos,String user,String password,String project_key) {
         qos=Qos;
         this.pub=pub;
         this.sub=sub;
         this.user=user;
         this.password=password;
        this.host=host;
        this.port=port;
        sub_topic=new HashMap<>();
        myPrintln("host="+host);
         this.clientId=clientId+"_"+project_key+System.currentTimeMillis()/1000;
        if(host==null){
            return;
        }
        executorService = Executors.newFixedThreadPool(1000);
        MyMqttClient1(host,port);
    }

    public void reConnect() throws MqttException {
        myPrintln("重连");
        client.reconnect();
        myPrintln(client.getServerURI());
       // client.subscribe("/cle/mqtt",0);
    }
    public void MyMqttClient1(String host,int port) {
        try {

            MemoryPersistence persistence = new MemoryPersistence();

            myPrintln("地址=tcp://"+host+":"+port);
            client = new MqttClient("tcp://"+host+":"+port, clientId, persistence);

            myPrintln("地址="+  client.getServerURI());
            myPrintln("ID="+  clientId);
        } catch (MqttException me) {
            myPrintln("reason " + me.getReasonCode());
            myPrintln("msg " + me.getMessage());
            myPrintln("loc " + me.getLocalizedMessage());
            myPrintln("cause " + me.getCause());
            myPrintln("excep " + me);
            me.printStackTrace();
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean start() {
        try {



            MqttConnectionOptions connOpts = new MqttConnectionOptions();

            if(password!=null&& !password.isEmpty()){
                connOpts.setPassword(password.getBytes());
            }
            if(user!=null&& !user.isEmpty()){
                connOpts.setUserName(user);
            }
            connOpts.setKeepAliveInterval(60);

// 禁用自动重连（避免因重连逻辑干扰）
           connOpts.setAutomaticReconnect(true);
           //connOpts.setConnectionTimeout(10);




            myPrintln("客户端="+client.getServerURI());
            // 设置回调
            MessageCallback c=new  MessageCallback(this);
            myPrintln("回调="+c);
            client.setCallback(c);
            // 建立连接
            myPrintln("Connecting to broker: " + client);
            client.connect(connOpts);
            myPrintln(sub);
           /* if(sub!=null){
                addSubTopic(sub);
            }*/
            client.subscribe("location_engine",0);
            client.subscribe("/cle/mqtt",0);
           // client.subscribe("GwData12",0);
        //    client.subscribe("GwData",0);
           // client.subscribe("AlphaRsp",0);
           // client.subscribe("connected",0);
          //  client.subscribe("disconnected",0);
          //  client.subscribe("GwData12",0);
            connect_count=0;
        } catch (MqttException e) {
            myPrintln("重连="+host);
            connect_count++;
            if(connect_count>5){
                myPrintln("启动异常,不在重连");
            }
            else{
                myPrintln("启动异常,尝试重连计数="+connect_count);
                start();
            }
            return false;
        }
        return true;
    }
public boolean getStatus(){
        if(client!=null){
            return client.isConnected();
        }else{
            return  false;
        }
}
    //主题订阅
    public synchronized  void addSubTopic(String topic) {
        try {
            if(topic==null){
                return;
            }
            else if(topic.contains("${bleMac}")){
                topic=topic.replace("${bleMac}","#");
            }
            else if(topic.contains("{blemac}")){
                topic=topic.replace("{blemac}","#");
            }

            if(topic.equals("#")){
                return;
            }
            sub_topic.put(topic,topic);
         /*   if(sub_topic.get(topic)!=null)
            {
                myPrintln("重复订阅主题=" + topic);
                return;
            }
            else{
                myPrintln("实际添加主题=" + topic);

            }*/
        if(topic.equals("GwData12")){
            return;
        }

            client.subscribe(topic,0);
        } catch (Exception e) {
            myPrintln("订阅主题异常 Topic=" + topic);
        }
    }
    //发布消息
    public void sendToTopic(String topic, String msg, int id) {
        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setId(id);
            message.setQos(qos);
            //myPrintln("发布主题消息"+topic);
            if (topic == null || topic.length() == 0) {
                myPrintln("主题有问题，return返回" + topic);
                return;
            }
            //  myPrintln(df.format(new Date())+"真实发布 Topic=" + topic + "  meg=" + msg);
            client.publish(topic, message);
            //  myPrintln("Message published");
        } catch (Exception e) {
            myPrintln("发布消息异常 Topic=" + topic + "  meg=" + msg);
        }
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setConnect_count(int connect_count) {
        this.connect_count = connect_count;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDf(SimpleDateFormat df) {
        this.df = df;
    }

    public MqttClient getClient() {
        return client;
    }

    public String getContent() {
        return content;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public String getClientId() {
        return clientId;
    }

    public MessageCallback getMessageCallback() {
        return messageCallback;
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void disConnect() {
        try {

            client.disconnect();
            client.close();
            client=null;
        } catch (MqttException e) {
            myPrintln("断开MQTT连接异常=" + e.getMessage());
        }
    }

}
