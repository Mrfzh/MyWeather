package com.example.a15521.myweatherforecast.entity;

/**
 * Created by 15521 on 2018/5/17.
 */

//天气实况数据
public class RealTimeWeather {

    private String name;    //城市名称
    private String text;    //天气现象文字
    private String code;    //天气现象代码，用于设置相应天气图片
    private String temperature;     //温度
    private String feelsLike;   //体感温度
    private String pressure;    //气压
    private String humidity;    //相对湿度
    private String visibility;  //能见度
    private String windDirection;   //风向
    private String windSpeed;   //风速
    private String windScale;   //风力等级
    private String lastUpdate;  //数据更新时间

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
