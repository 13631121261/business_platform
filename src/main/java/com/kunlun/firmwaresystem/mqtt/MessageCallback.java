package com.kunlun.firmwaresystem.mqtt;

import com.google.gson.Gson;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.SpringUtil;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;


import java.text.SimpleDateFormat;
import java.util.Arrays;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class MessageCallback implements MqttCallback {

    int dd=0;
    private static int ExpireTime = 60;   // redis中存储的过期时间60s
    Gson gson = new Gson();


    MyMqttClient mqttClient;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    String time;

    public MessageCallback(MyMqttClient mqttClient) {
        this.mqttClient = mqttClient;


    }


    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if (topic.equals("/cle/mqtt")) {
           // dd++;
            // myPrintln("dd计数=" + dd);
        }else{
           // myPrintln("其他计数=" + new String(message.getPayload()));
        }
          //  mqttClient.executorService.submit(new CallBackHandlers(topic, message));

            mqttClient.executorService.submit(() -> {
             //   try {
                 //   myPrintln(Thread.currentThread().getName());
                    new CallBackHandlers(topic, message).run();
                    message.clearPayload();

                    // 实际处理逻辑
                  //  handleMessage(topic, new String(message.getPayload()));
             //   } catch (Exception e) {
              //      myPrintln("处理消息异常: " + e.getMessage());
             //   }

            });
      //  }
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