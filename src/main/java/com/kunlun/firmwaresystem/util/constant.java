package com.kunlun.firmwaresystem.util;


import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class constant {
    public static final int code_ok = 0;
    public static final int code_error = -1;
    public static final String wifi_type = "wifi";
    public static final String ble_type = "ble";
    private static int msgid = 1;


    public static int getMsgId() {
        return msgid++;
    }
    public static void main(String[] a){
        byte[] ab=new byte[]{0x33,0x32,0x31};
        myPrintln(new String(ab));
    }
}
