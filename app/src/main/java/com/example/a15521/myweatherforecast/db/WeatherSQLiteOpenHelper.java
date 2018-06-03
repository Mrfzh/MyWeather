package com.example.a15521.myweatherforecast.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 15521 on 2018/5/20.
 */

//建立天气数据库
public class WeatherSQLiteOpenHelper extends SQLiteOpenHelper {

    //建表语句
    //天气表
    public static final String CREATE_WEATHER = "create table Weather (id integer primary key autoincrement, city text," +
            "weather text, picture text, temperature text)";

    //历史记录表
    public static final String CREATE_RECORD = "create table Record (id integer primary key autoincrement, city text)";

    public WeatherSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHER);
        db.execSQL(CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
