package com.kunlun.firmwaresystem.Tcp;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ServerChannelHandler serverChannelHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //IdleStateHandler心跳机制,如果超时触发Handle中userEventTrigger()方法
        pipeline.addLast("idleStateHandler",
                new IdleStateHandler(15,0,0, TimeUnit.MINUTES));
        // 字符串编解码器
  /*      pipeline.addLast(
                new StringDecoder(),
                new StringEncoder()
        );*/
        pipeline.addLast(
                new ByteArrayDecoder(),
                new ByteArrayEncoder()
        );

        pipeline.addLast("serverChannelHandler",serverChannelHandler);
    }


}
