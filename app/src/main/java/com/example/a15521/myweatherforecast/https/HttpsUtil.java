package com.example.a15521.myweatherforecast.https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

//该线程用于发送网络请求获得api数据
public class HttpsUtil extends Thread{

    private String address; //网络地址
    private String returnHttpsDate;  //返回的数据

    public HttpsUtil(String address) {
        this.address = address;
    }

    public String getReturnHttpsDate() {
        return returnHttpsDate;
    }

    @Override
    public void run() {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        try {
            //获得URL，
            URL url = new URL(address);
            //获得连接对象
            connection = (HttpsURLConnection) url.openConnection();
            //设置请求方法、连接超时、读取超时
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            //获得请求的数据流
            InputStream in = null;
            //当响应码超过400时，可以通过getErrorStream()接收服务器发送过来的错误信息
            if ( connection.getResponseCode() >= 400) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            //获得BufferedReader对象，用于读取数据
            reader = new BufferedReader(new InputStreamReader(in));
            //将读取的数据存入StringBuilder
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            //获得返回的数据
            returnHttpsDate = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
