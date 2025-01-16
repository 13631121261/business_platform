package com.kunlun.firmwaresystem.Tcp;


import com.kunlun.firmwaresystem.entity.Beacon_tag;
import com.kunlun.firmwaresystem.entity.FWordcard;
import com.kunlun.firmwaresystem.gatewayJson.Constant;
import com.kunlun.firmwaresystem.mappers.FWordcardMapper;
import com.kunlun.firmwaresystem.sql.Btag_Sql;
import com.kunlun.firmwaresystem.sql.FWordcard_Sql;
import com.kunlun.firmwaresystem.util.RedisUtils;
import com.kunlun.firmwaresystem.util.StringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandler.class);
    @Resource
    private RedisUtils redisUtil;

    @Resource
    private FWordcardMapper fWordcardMapper;
    static HashMap<ChannelHandlerContext,String> wordCardhashMap=new HashMap<>();
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
       // System.out.println("11111111111111111有设备连接"+ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
      //  System.out.println("222222222222222d断开设备连接"+ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {



//BDBDBDBDF3898604F01123C0757297A3BDBDBDBDBB104738303543472E434154312E4B4C313806473830354347F9BDBDBDBDA90100011A4169724D324D5F373830455F56313135365F4C54455F4C534154C2
        //=BDBDBDBDD606018B6F0B66010D470D00C5E2BA
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // System.out.println("上报时间="+sp.format(System.currentTimeMillis()));
        String raw="";
        try{
          //  System.out.println("接收数据="+StringUtil.byteArrToHex((byte[])msg));
            raw= StringUtil.byteArrToHex((byte[])msg);
        }catch (Exception e){
            System.out.println("异常="+e.getMessage());

        }


        String raws[]=null;
        if(raw.contains("BDBDBDBD")){
            raws=raw.split("BDBDBDBD");
        }

    if(raws!=null){
        System.out.println(raws.length);
        for(String datas:raws){
            System.out.println(datas.length());
            if(datas.length()==0){
                continue;
            }
            byte[] data=StringUtil.hexToByteArr("BDBDBDBD"+datas);
            byte cmd[]=null;
            if(data!=null&&data.length>=5){
                switch (data[4]&0xff){
                    case 0xF0:
                      //  System.out.println("初次TCP连接");
                        cmd=StringUtil.hexToByteArr("BDBDBDBDF1BDBDBDBDCC");
                        long time=System.currentTimeMillis()/1000;
                        byte[] times= StringUtil.intTo4ByteArray(time);
                         cmd[0]=times[3];
                        cmd[1]=times[2];
                        cmd[2]=times[1];
                        cmd[3]=times[0];
                        byte[] imei=new byte[7];
                        imei[0]=data[11];
                        imei[1]=data[10];
                        imei[2]=data[9];
                        imei[3]=data[8];
                        imei[4]=data[7];
                        imei[5]=data[6];
                        imei[6]=data[5];
                        long imeia=StringUtil.eByteToLong(imei);
                      //  System.out.println("Imei="+imeia);

                        byte crc= getCRC(cmd);
                        cmd[cmd.length-1]=crc;
                        System.out.println(crc);

                        FWordcard wordcard=null;
                        FWordcard_Sql wordcard_sql=new FWordcard_Sql();
                        wordcard=  wordcard_sql.getOne(fWordcardMapper,imeia+"");
                        if(wordcard==null){
                            return;
                        }else{
                            wordCardhashMap.put(channelHandlerContext,imeia+"");
                            redisUtil.setnoTimeOut(Constant.fwordcard+imeia+"",wordcard);
                            System.out.println("原始下发="+StringUtil.byteArrToHex(cmd));
                            channelHandlerContext.writeAndFlush(cmd).syncUninterruptibly();
                        }


                        break;
                    case 0xD6:
                        System.out.println("单组原始数据="+"BDBDBDBD"+datas);
                         System.out.println("刚才是蓝牙定位包的数据");
                        System.out.println("类型="+data[5]);
                        System.out.println("信标组数量是="+data[6]);
                        int d=6;

                        for(int i=1;i<=data[6];i++){
                            byte[] time1=new byte[4];
                            time1[0]=data[d+4];
                            time1[1]=data[d+3];
                            time1[2]=data[d+2];
                            time1[3]=data[d+1];
                           // System.out.println("时间="+sp.format(StringUtil.ByteToLong(time1)));
                            //System.out.println("此组收到信标数量为=="+data[d+5]);
                            int sum=data[d+5];  d=d+5;
                            for(int o=1;o<=sum;o++){
                             //   System.out.println("信标"+j);
                                int major1=((data[d+2]&0xff)*256);
                                int major2=data[d+1]&0xff;
                                int major=major2+major1;
                                int minor1=((data[d+4]&0xff)*256);
                                int minor2=(data[d+3]&0xff);
                                int minor=minor1+minor2;
                                int rssi=data[d+5];
                                int rssiAtoOne=data[d+6];
                                d=d+6;
                                System.out.println("major="+major+"  Minor="+minor+" rssi="+rssi+"   Rssi @1M="+rssiAtoOne);
                             /*   Btag_Sql btag_sql=new Btag_Sql();
                                btag_sql.selectPageTag()
                                Beacon_tag beacon_tag=new Beacon_tag();
*/
                            }
                        }
                        break;
                    case 0xF6:
                        System.out.println("88888888888888888 88888888888888888电量="+data[5]);
                        System.out.println("信号强度="+data[11]);
                        channelHandlerContext.writeAndFlush(new byte[]{0x01,0x02}).syncUninterruptibly();
                        break;
                    case 0x02:
                        System.out.println("kssssssssssssssssssss");
                        if(data[5]==0x02){
                            System.out.println("SOS按键报警");
                        }else if(data[5]==0x04){
                            System.out.println("关机报警");
                    }
                        else if((data[5]&0xff)==0x80){
                            System.out.println("SOS取消报警");
                        }else{
                            System.out.println("其他报警");
                        }
                        break;
                        default:
                            System.out.println("------------------------------单组原始数据="+"BDBDBDBD"+datas);
                            System.out.println("其他的上报数据");
                            break;
                }
            }
        }
    }



    }


    private byte getCRC(byte[] cmd){
        byte ck_sum = 0;
        for(int i=0; i<cmd.length-1; i++)
        {
        //    System.out.println(i);
            ck_sum =(byte) (ck_sum + cmd[i]);
            ck_sum = (byte)(ck_sum % 0x100);
        }
        ck_sum = (byte)((0xFF)-ck_sum);
        return ck_sum;
    }
    /**
     *
     */
    private void handleChkinCmd(ChannelHandlerContext chc) {
        Date curTime = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String strCurTime = df.format(curTime);
        String strRechk = "HTD02:RECHK:17:" + strCurTime + ":00;";
//        System.out.println(strRechk);

        chc.writeAndFlush(strRechk).syncUninterruptibly();
    }


    /**
     * 活跃的、有效的通道
     * 第一次连接成功后进入的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.info("tcp client "+getRemoteAddress(ctx)+" connect success");
        NettyTcpServer.map.put(getIPString(ctx),ctx.channel());
    }

    /**
     * 不活动的通道
     * 连接丢失后执行的方法（client端可据此实现断线重连）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 删除Channel Map中失效的Client
        NettyTcpServer.map.remove(getIPString(ctx));
        ctx.close();
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 发生异常 关闭连接
        LOGGER.error("引擎{}的通道发生异常，断开连接",getRemoteAddress(ctx));
        ctx.close();
    }

    /**
     * 心跳机制 超时处理
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()== IdleState.READER_IDLE) {
                LOGGER.info("Client: "+socketString+" READER_IDLE读超时");
                ctx.disconnect();
            }else if (event.state()== IdleState.WRITER_IDLE){
                LOGGER.info("Client: "+socketString+" WRITER_IDLE写超时");
                ctx.disconnect();
            }else if (event.state()== IdleState.ALL_IDLE){
                LOGGER.info("Client: "+socketString+" ALL_IDLE总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * 获取client对象：ip+port
     * @param channelHandlerContext
     * @return
     */
    public String getRemoteAddress(ChannelHandlerContext channelHandlerContext){
        String socketString = "";
        socketString = channelHandlerContext.channel().remoteAddress().toString();
        return socketString;
    }

    /**
     * 获取client的ip
     * @param channelHandlerContext
     * @return
     */
    public String getIPString(ChannelHandlerContext channelHandlerContext){
        String ipString = "";
        String socketString = channelHandlerContext.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1,colonAt);
        return ipString;
    }
}
