package com.kunlun.firmwaresystem.util;

import java.util.Scanner;

public class Test {
    public static void main(String[] arg0) {
       /* Scanner sr = new Scanner(System.in);

        int n = sr.nextInt();
        //初始值为n依次倒叙判断
        for (int i = n; i >= 2; i--) {
            System.out.println("输出i=" + i);
            for (int j = 2; j < i; j++) {
                System.out.println("输出j=" + j);
                //余数为零不是质数直接跳过
                if (i % j == 0) {
                    break;
                }
                //继续判断直到	j 等于当前的数(i)-1
                if (j == i - 1) {
                    System.out.println(i);
                    //因为是求不大于n的最大质数，
                    //所有找到不大于n的第一个质数就可以用return结束程序
                    return;
                }
            }
        }*/
        String msg="AAFE00EC010203040506070809120304050607080000";
        String url="AAFE10EC01010203040506070809120304050607080000";
     String tlm_no="AAFE2000123456781122334455667788";
     String tlm_on="AAFE200112345678112233445566778811112222";
        String eid="AAFE30EC1234567811223344";
    String a=msg.substring(8,28);
        String b=msg.substring(28,40);
        double nn=Math.pow(2, -1)+Math.pow(2, -2)+Math.pow(2, -3)+Math.pow(2, -4)+Math.pow(2, -5)+Math.pow(2, -6)+Math.pow(2, -7)+Math.pow(2, -8);
        System.out.println(nn);
        System.out.println(Math.pow(2, -1));
        System.out.println(Math.pow(2, -2));
        System.out.println(Math.pow(2, -3));
        System.out.println(Math.pow(2, -4));
        System.out.println(Math.pow(2, -5));
        System.out.println(Math.pow(2, -6));
        System.out.println(Math.pow(2, -7));
        System.out.println(Math.pow(2, -8));
    byte[] data=msg.getBytes();
      System.out.println(a);
        System.out.println(b);

      /*  31
                0.25*/
        double aa=0.0976566;
        char[] bit="00000000".toCharArray();
        if(aa-0.5>=0){
            bit[0]='1';
            aa=aa-0.5;
        }
        if(aa-0.25>=0){
            bit[1]='1';
            aa=aa-0.25;
        }
        if(aa-0.125>=0){
            bit[2]='1';
            aa=aa-0.125;
        }
        if(aa-0.0625>=0){
            bit[3]='1';
            aa=aa-0.0625;
        }
        if(aa-0.03125>=0){
            bit[4]='1';
            aa=aa-0.03125;
        }
        if(aa-0.015625>=0){
            bit[5]='1';
            aa=aa-0.015625;
        }
        if(aa-0.0078125>=0){
            bit[6]='1';
            aa=aa-0.0078125;
        }
        if(aa-0.00390625>=0){
            bit[7]='1';
            aa=aa-0.00390625;
        }
      /*  if(aa-0.03125>=0){
            bit[4]='1';
            aa=aa-0.03125;
        }*/
        System.out.println(new String(bit));

       /* aa=aa-0.5;
        aa=aa-0.25;
        aa=aa-0.125;
        aa=aa-0.0625;
        aa=aa-0.03125;
        aa=aa-0.015625;
        aa=aa-0.0078125;
        aa=aa-0.00390625;*/

     /*   030701E4BB8AE5A4A9E68891E683B3E78EA9
        030702E4BDA0E5A5BDE59097EFBC9FE5ADA9
        030703E4BDA0E5A5BDE59097EFBC9FE5ADA9
        030704E4BDA0E5A5BDE59097EFBC9FE5ADA9
        030705E4BDA0E5A5BDE59097EFBC9FE5ADA9
        030706E4BDA0E5A5BDE59097EFBC9FE5ADA9
        030707E4BDA0E5A5BDE59097EFBC9FE5ADA9*/
        byte v=(byte)0xC8;
      //  v=(byte)(v|(byte)0x01);
        System.out.println(v);
    }
    //zeng929620555

    //0.09765625
    //0x1F19





}
