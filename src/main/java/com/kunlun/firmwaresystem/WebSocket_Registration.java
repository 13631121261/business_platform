package com.kunlun.firmwaresystem;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class WebSocket_Registration extends WebSocketServer {
    private static WebSocket_Registration webSocket;
    private Map<String, WebSocket> connectlist;

    public static WebSocket_Registration getWebSocket() {
        if (webSocket == null) {
            webSocket = new WebSocket_Registration(8087);
        }
        myPrintln("网页链接"+webSocket);
        return webSocket;
    }

    private WebSocket_Registration(int port) {
        super(new InetSocketAddress(port));
        connectlist = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        myPrintln("WebSocket_RegistrationonOpen" + webSocket.toString());

    }

    @Override
    protected boolean onConnect(SelectionKey key) {
        myPrintln("WebSocket_Registration连接" + key.toString());
        return super.onConnect(key);

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        myPrintln("WebSocket_Registration断开连接" + webSocket.getResourceDescriptor());
        connectlist.remove(s);

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        myPrintln("WebSocket_Registration接收消息" + s);
      //  webSocket.send("收到了");
        connectlist.put(s, webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void sendData(String key, String msg) {
        myPrintln("触发发送MyWebSocket");
        WebSocket webSocket = connectlist.get(key);
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(msg);
            myPrintln("发送和");
        }

    }
}