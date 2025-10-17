package com.kunlun.firmwaresystem.mqtt;

import com.google.gson.Gson;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.SpringUtil;
import com.kunlun.firmwaresystem.util.StringUtil;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.scheduling.annotation.Scheduled;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

import static com.kunlun.firmwaresystem.NewSystemApplication.*;

public class MessageCallback implements MqttCallback {

    int dd=0;
    private static int ExpireTime = 60;   // redis中存储的过期时间60s
    Gson gson = new Gson();


    MyMqttClient mqttClient;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    String time;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 1. 定义内存队列（线程安全）
    private final BlockingQueue<MyMqttMessage> messageQueue = new LinkedBlockingQueue<>();
    private static final int BATCH_SIZE = 100; // 每批处理100条数据
    private static final int BATCH_TIMEOUT_MS = 3000; // 每2s强制处理一次

    public MessageCallback(MyMqttClient mqttClient) {
        this.mqttClient = mqttClient;

        scheduler.scheduleAtFixedRate(this::processBatch, 0, 3, TimeUnit.SECONDS);
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if (topic.equals("/cle/mqtt")) {
           // dd++;
            // myPrintln("dd计数=" + dd);
        }else{
        //    myPrintln("其他计数=" + new String(message.getPayload()));
             //myPrintln("1111");
            messageQueue.offer(new MyMqttMessage(topic,message)); // 非阻塞写入队列
            // myPrintln("2222---"+messageQueue.size());
            // 3. 触发条件：队列大小达到阈值
            if (messageQueue.size() >= BATCH_SIZE) {
                //myPrintln("批量处理");
                processBatch();
        }
          //  mqttClient.executorService.submit(new CallBackHandlers(topic, message));

        }
       /*     mqttClient.executorService.submit(() -> {

                    new CallBackHandlers(topic, message).run();
                    message.clearPayload();


            });*/
      //  }
    }

    private void processBatch()  {
        List<MyMqttMessage> batchMessages = new ArrayList<>();
        List<Alarm> alarmList = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        List<Station> stationList = new ArrayList<>();
        messageQueue.drainTo(batchMessages, BATCH_SIZE+100); // 批量取出数据
        if (!batchMessages.isEmpty()) {
            List<Future<List<Object>>> futureList = new ArrayList<>();
            for (MyMqttMessage message : batchMessages)
            {
                Future<List<Object>> future = mqttClient.executorService.submit(() -> {
                    CallBackHandlers callBackHandlers=  new CallBackHandlers( message.getTopic(),message.getMqttMessage());
                  //  myPrintln(message.getTopic());
                  //  myPrintln(StringUtil.byteArrToHex(message.getMqttMessage().getPayload()));
                   // myPrintln(new String(message.getMqttMessage().getPayload()));
                    callBackHandlers.run();
                    message.getMqttMessage().clearPayload();
                    return callBackHandlers .getObjects();

                });

                futureList.add(future);
            }
            for (Future<List<Object>> future : futureList) {
                try {
                    List<Object> result = future.get();  // 此时任务可能已经完成或接近完成
                    if (result==null || result.isEmpty()) {
                        continue;
                    }
                    objects.addAll(result);
                } catch (InterruptedException | ExecutionException e) {
                    myPrintln("获取异步任务结果异常：" + e.getMessage());
                }
            }
            for(Object object : objects) {
              //  myPrintln("类明名" + object.getClass().getName());
                if (object.getClass().getName().contains("Station")){
                    stationList.add((Station)object);
                }else if (object.getClass().getName().contains("Alarm")){
                    alarmList.add((Alarm)object);
                }
            }
            if (!stationList.isEmpty()) {
             boolean s= stationService.saveOrUpdateBatch(stationList);
                myPrintln("批量保存网关：" + stationList.size()+" 条"+s);
            }
            if (!alarmList.isEmpty()) {
               boolean s= alarmService.saveOrUpdateBatch(alarmList);
                myPrintln("批量保存警报：" + alarmList.size()+" 条"+s);
            }
        }
    }


    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {

    }

    @Override
    public void connectComplete(boolean b, String s) {

    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {

    }

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {

        // 连接丢失后，一般在这里面进行重连
        myPrintln("连接断开，可以做重连" + mqttDisconnectResponse.getReasonString()   );
        try {
            myPrintln("没有手动冲脸"   );
            //   mqttClient.reConnect();
        } catch (Exception e) {
            myPrintln("重连失败---"+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {

    }



}