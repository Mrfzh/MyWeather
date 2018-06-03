package com.example.a15521.myweatherforecast.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a15521.myweatherforecast.entity.RealTimeWeather;

import java.util.List;

/**
 * Created by 15521 on 2018/5/20.
 */

//天气数据库操作类
public class WeatherOperation {

    private SQLiteDatabase db;
    private RealTimeWeather realTimeWeather;

    public WeatherOperation(SQLiteDatabase db, RealTimeWeather realTimeWeather) {
        this.db = db;
        this.realTimeWeather = realTimeWeather;
    }

    //将数据存入天气表中
    public void addDataToWeather() {
        //如果没有该数据则添加进数据库中
        if (!isHasDataInWeather(realTimeWeather.getName())) {
            ContentValues values = new ContentValues();
            values.put("city", realTimeWeather.getName());
            values.put("weather", realTimeWeather.getText());
            values.put("picture", realTimeWeather.getCode());
            values.put("temperature", realTimeWeather.getTemperature());
            db.insert("Weather", null, values);
            values.clear();
        }
    }

    //将天气表中的数据存到集合中
    public void addWeatherDataToList(List<String> cityList, List<String> pictureList, List<String> temperatureList) {
        Cursor cursor = db.query("Weather", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                cityList.add(cursor.getString(cursor.getColumnIndex("city")));
                pictureList.add(cursor.getString(cursor.getColumnIndex("picture")));
                temperatureList.add(cursor.getString(cursor.getColumnIndex("temperature")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //将天气表中的城市数据存到集合中
    public void addCityDataToList(List<String> cityList) {
        Cursor cursor = db.query("Weather", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                cityList.add(cursor.getString(cursor.getColumnIndex("city")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //删除单个数据
    public void deleteCity(String deleteCityName) {
        db.delete("Weather", "city = ?", new String[] {deleteCityName});
    }

    //添加数据进入记录表中
    public void addDataToRecord(String data) {
        ContentValues values = new ContentValues();
        values.put("city", data);
        db.insert("Record", null, values);
    }

    //添加记录表中的数据到集合中
    public void addRecordDataToList(List<String> list) {
        Cursor cursor = db.query("Record", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex("city")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //清除数据库中记录表的数据
    public void clearRecord() {
        db.execSQL("delete from Record");
    }

    //判断天气表中是否已经有了该条数据
    public boolean isHasDataInWeather(String city) {
        boolean isHasCity = false;
        Cursor cursor = db.query("Weather", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (city.equals(cursor.getString(cursor.getColumnIndex("city")))) {
                    isHasCity = true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return isHasCity;
    }
}
