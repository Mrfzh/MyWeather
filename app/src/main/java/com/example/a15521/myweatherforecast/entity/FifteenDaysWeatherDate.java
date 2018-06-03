package com.example.a15521.myweatherforecast.entity;

import java.util.List;

/**
 * Created by 15521 on 2018/5/17.
 */

//15天天气预报数据
public class FifteenDaysWeatherDate {

    private List<String> date;  //日期
    private List<String> weatherDay;    //白天天气现象文字
    private List<String> codeDay;   //白天天气现象代码
    private List<String> weatherNight;  //晚间天气现象文字
    private List<String> codeNight;     //晚间天气现象代码
    private List<String> high;  //当天最高气温
    private List<String> low;   //当天最低气温
    private List<String> windDirection;     //风向
    private List<String> windScale;     //风力等级

    public List<String> getCodeDay() {
        return codeDay;
    }

    public void setCodeDay(List<String> codeDay) {
        this.codeDay = codeDay;
    }

    public List<String> getCodeNight() {
        return codeNight;
    }

    public void setCodeNight(List<String> codeNight) {
        this.codeNight = codeNight;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getHigh() {
        return high;
    }

    public void setHigh(List<String> high) {
        this.high = high;
    }

    public List<String> getLow() {
        return low;
    }

    public void setLow(List<String> low) {
        this.low = low;
    }

    public List<String> getWeatherDay() {
        return weatherDay;
    }

    public void setWeatherDay(List<String> weatherDay) {
        this.weatherDay = weatherDay;
    }

    public List<String> getWeatherNight() {
        return weatherNight;
    }

    public void setWeatherNight(List<String> weatherNight) {
        this.weatherNight = weatherNight;
    }

    public List<String> getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(List<String> windDirection) {
        this.windDirection = windDirection;
    }

    public List<String> getWindScale() {
        return windScale;
    }

    public void setWindScale(List<String> windScale) {
        this.windScale = windScale;
    }
}
