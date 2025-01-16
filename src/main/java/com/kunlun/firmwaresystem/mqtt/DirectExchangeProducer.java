package com.kunlun.firmwaresystem.mqtt;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DirectExchangeProducer {
    @Autowired
    private AmqpTemplate rabbitMQTemplate;
    SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
    public void send(String msg, String routingKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                rabbitMQTemplate.convertAndSend(DirectExchangeRabbitMQConfig.directExchangeName, routingKey, msg);
          /*      System.out.println(routingKey+"开始时间="+dfs.format(new Date())+msg);

                System.out.println("结束时间="+dfs.format(new Date()));
                System.out.println("线程="+Thread.currentThread().getName());*/
            }
        }).start();

    }
}