package com.example.a15521.myweatherforecast.utils;

import com.example.a15521.myweatherforecast.entity.FifteenDaysWeatherDate;
import com.example.a15521.myweatherforecast.entity.LifeSuggestionDate;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15521 on 2018/5/17.
 */

//JSON数据解析
public class ParseJson {

    //天气实况数据
    RealTimeWeather realTimeWeather;
    //15天天气数据
    FifteenDaysWeatherDate fifteenDaysWeatherDate;
    //生活指数数据
    LifeSuggestionDate lifeSuggestionDate;

    //返回解析后的数据
    public RealTimeWeather getRealTimeWeather() {
        return realTimeWeather;
    }
    public FifteenDaysWeatherDate getFifteenDaysWeatherDate() {
        return fifteenDaysWeatherDate;
    }
    public LifeSuggestionDate getLifeSuggestionDate() {
        return lifeSuggestionDate;
    }

    //解析天气实况的数据
    public void parseToRealTimeWeather(String jsonDate) throws Exception{
        realTimeWeather = new RealTimeWeather();
        JSONObject jsonObject = new JSONObject(jsonDate);
        JSONArray results = jsonObject.getJSONArray("results");
        JSONObject date = results.getJSONObject(0);
        realTimeWeather.setLastUpdate(date.getString("last_update"));
        JSONObject location = date.getJSONObject("location");
        realTimeWeather.setName(location.getString("name"));
        JSONObject now = date.getJSONObject("now");
        realTimeWeather.setText(now.getString("text"));
        realTimeWeather.setCode(now.getString("code"));
        realTimeWeather.setTemperature(now.getString("temperature"));
        realTimeWeather.setFeelsLike(now.getString("feels_like"));
        realTimeWeather.setPressure(now.getString("pressure"));
        realTimeWeather.setHumidity(now.getString("humidity"));
        realTimeWeather.setVisibility(now.getString("visibility"));
        realTimeWeather.setWindDirection(now.getString("wind_direction"));
        realTimeWeather.setWindSpeed(now.getString("wind_speed"));
        realTimeWeather.setWindScale(now.getString("wind_scale"));
    }

    //解析15天天气的数据
    public void parseTOFifteenDaysWeather(String jsonDate) throws Exception {
        fifteenDaysWeatherDate = new FifteenDaysWeatherDate();
        JSONObject jsonObject = new JSONObject(jsonDate);
        JSONArray results = jsonObject.getJSONArray("results");
        JSONObject objectDate = results.getJSONObject(0);
        JSONArray daily = objectDate.getJSONArray("daily");
        //创建集合用于存储15天的数据
        List<String> date = new ArrayList<>();
        List<String> weatherDay = new ArrayList<>();
        List<String> codeDay = new ArrayList<>();
        List<String> weatherNight = new ArrayList<>();
        List<String> codeNight = new ArrayList<>();
        List<String> high = new ArrayList<>();
        List<String> low = new ArrayList<>();
        List<String> windDirection = new ArrayList<>();
        List<String> windScale = new ArrayList<>();
        //添加数据到集合中
        JSONObject object;
        for (int i = 0; i < 15; i++) {
            object = daily.getJSONObject(i);
            date.add(object.getString("date"));
            weatherDay.add(object.getString("text_day"));
            codeDay.add(object.getString("code_day"));
            weatherNight.add(object.getString("text_night"));
            codeNight.add(object.getString("code_night"));
            high.add(object.getString("high"));
            low.add(object.getString("low"));
            windDirection.add(object.getString("wind_direction"));
            windScale.add(object.getString("wind_scale"));
        }
        //设置实体类
        fifteenDaysWeatherDate.setDate(date);
        fifteenDaysWeatherDate.setWeatherDay(weatherDay);
        fifteenDaysWeatherDate.setCodeDay(codeDay);
        fifteenDaysWeatherDate.setWeatherNight(weatherNight);
        fifteenDaysWeatherDate.setCodeNight(codeNight);
        fifteenDaysWeatherDate.setHigh(high);
        fifteenDaysWeatherDate.setLow(low);
        fifteenDaysWeatherDate.setWindDirection(windDirection);
        fifteenDaysWeatherDate.setWindScale(windScale);
    }

    //解析生活指数的数据
    public void parseToLifeSuggestionDate(String jsonData) throws Exception {
        lifeSuggestionDate = new LifeSuggestionDate();
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray results = jsonObject.getJSONArray("results");
        JSONObject data = results.getJSONObject(0);
        JSONObject suggestion = data.getJSONObject("suggestion");
        JSONObject airPollution = suggestion.getJSONObject("air_pollution");
        lifeSuggestionDate.setAirCondition(airPollution.getString("brief"));
        lifeSuggestionDate.setAirConditionDetails(airPollution.getString("details"));
        JSONObject sport = suggestion.getJSONObject("sport");
        lifeSuggestionDate.setSport(sport.getString("brief"));
        lifeSuggestionDate.setSportDetails(sport.getString("details"));
        JSONObject traffic = suggestion.getJSONObject("traffic");
        lifeSuggestionDate.setTraffic(traffic.getString("brief"));
        lifeSuggestionDate.setTrafficDetails(traffic.getString("details"));
        JSONObject travel = suggestion.getJSONObject("travel");
        lifeSuggestionDate.setTravel(travel.getString("brief"));
        lifeSuggestionDate.setTravelDetails(travel.getString("details"));
        JSONObject umbrella = suggestion.getJSONObject("umbrella");
        lifeSuggestionDate.setUmbrella(umbrella.getString("brief"));
        lifeSuggestionDate.setUmbrellaDetails(umbrella.getString("details"));
        JSONObject uv = suggestion.getJSONObject("uv");
        lifeSuggestionDate.setUv(uv.getString("brief"));
        lifeSuggestionDate.setUvDetails(uv.getString("details"));
    }
}
