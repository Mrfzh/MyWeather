package com.example.a15521.myweatherforecast.https;

/**
 * Created by asus on 2018/4/22.
 */

//在子线程中发送网络请求获取数据
public class SendHttpsRequest {
    public String sendHttpsRequest(String address) throws InterruptedException{
        HttpsUtil httpsUtil = new HttpsUtil(address);
        httpsUtil.start();
        httpsUtil.join();
        //返回获取到的数据
        return httpsUtil.getReturnHttpsDate();
    }
}
