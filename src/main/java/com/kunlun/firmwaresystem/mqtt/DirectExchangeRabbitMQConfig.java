package com.kunlun.firmwaresystem.mqtt;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectExchangeRabbitMQConfig {

    public static final String directExchangeName = "directExchangeName";
    public static final String go_to_connect = "go_to_connect";
    // public static final String config_Station = "config_Station";
    //转发的
    public static final String transpond = "transpond";
    private static final String queue3BindingKey1 = "on_state";


   /* //状态推送第三方
    public static final String Push = "Push";*/

    //推送给网页地图展示
    public static final String sendtoMap = "sendtoMap";
    //  private static final String queue3BindingKey = "#";
    // 声明直连交换机
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName,false,    false);
    }

    // 声明消息队列
    @Bean
    public Queue messageQueue1() {
        return new Queue("sendToStation",false);
    }

/*
    @Bean
    public Queue messageQueue2() {
        return new Queue("scan_report");
    }

    @Bean
    public Queue messageQueue3() {
        return new Queue("state");
    }
*/


    @Bean
    public Queue messageQueue2() {
        return new Queue("transpond",false);
    }
    @Bean
    public Queue messageQueue5() {
        return new Queue("mqtt_topic",false);
    }
/*    @Bean
    public Queue messageQueue4() {
        return new Queue(Push);
    }*/
    @Bean
    public Queue messageQueue3() {
        return new Queue("sendtoHtml",false);
    }
    @Bean
    public Queue messageQueue6() {
        return new Queue(sendtoMap,false);
    }

    // 向主题交换机上绑定队列
    @Bean
    Binding bindingQueue1Exchange(Queue messageQueue1, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue1)
                .to(directExchange)
                .with("sendToStation");
    }

    @Bean
    Binding bindingQueue2Exchange(Queue messageQueue2, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue2)
                .to(directExchange)
                .with(transpond);
    }

    @Bean
    Binding bindingQueue3Exchange(Queue messageQueue3, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue3)
                .to(directExchange)
                .with("sendtoHtml");
    }
/*    @Bean
    Binding bindingQueue4Exchange(Queue messageQueue4, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue4)
                .to(directExchange)
                .with(Push);
    }*/
    @Bean
    Binding bindingQueue5Exchange(Queue messageQueue5, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue5)
                .to(directExchange)
                .with("mqtt_topic");
    }
    @Bean
    Binding bindingQueue6Exchange(Queue messageQueue6, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue6)
                .to(directExchange)
                .with(sendtoMap);
    }
/*
    @Bean
    Binding bindingQueue3Exchange(Queue messageQueue3, DirectExchange directExchange) {
        return BindingBuilder.bind( messageQueue3 )
                .to( directExchange )
                .with( queue3BindingKey1 );
    }*/


}