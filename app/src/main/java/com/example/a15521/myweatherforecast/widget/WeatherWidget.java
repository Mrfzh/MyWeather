package com.example.a15521.myweatherforecast.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;
import com.example.a15521.myweatherforecast.https.SendHttpsRequest;
import com.example.a15521.myweatherforecast.ui.Login;
import com.example.a15521.myweatherforecast.utils.ParseJson;

//天气桌面小部件
public class WeatherWidget extends AppWidgetProvider {

    //存放天气实况数据
    private RealTimeWeather mRealTimeWeather;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //构建RemoteViews对象，作用是给widget控件设置文本和点击事件
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        //有网络才能获取数据
        if (hasInternet(context)) {
            try {
                getData();  //获取天气数据
            } catch (Exception e) {
                e.printStackTrace();
            }

            //设置文字和图片
            views.setTextViewText(R.id.tv_weather_widget_city, mRealTimeWeather.getName());
            views.setTextViewText(R.id.tv_weather_widget_temperature, mRealTimeWeather.getTemperature());
            views.setImageViewBitmap(R.id.iv_weather_widget_picture, getBitmapByName("weather_" + mRealTimeWeather.getCode(), context));

            //构建PendingIntent对象，启动一个新活动用于显示城市天气
            Intent intent = new Intent(context, Login.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            //设置点击事件
            views.setOnClickPendingIntent(R.id.tv_weather_widget_city, pendingIntent);
            views.setOnClickPendingIntent(R.id.tv_weather_widget_temperature, pendingIntent);
            views.setOnClickPendingIntent(R.id.tv_weather_widget_temperature_text, pendingIntent);
            views.setOnClickPendingIntent(R.id.iv_weather_widget_picture, pendingIntent);
        }


        //更新每一个小部件
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    //发送网络请求获得定位城市的天气数据
    private void getData() throws Exception {
        SendHttpsRequest request = new SendHttpsRequest();
        String jsonData = request.sendHttpsRequest("https://api.seniverse.com/v3/weather/now.json?key=moy8j7i44mvz6xw6&location=ip&language=zh-Hans&unit=c");
        ParseJson parseJson = new ParseJson();
        parseJson.parseToRealTimeWeather(jsonData);
        mRealTimeWeather = parseJson.getRealTimeWeather();
    }

    //通过图片名返回图片的Bitmap
    private Bitmap getBitmapByName(String name, Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }

    //判断是否有网络
    private boolean hasInternet(Context context) {
        //获取一个用于管理网络连接的系统服务类
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo实例
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //判断网络连接状态，有网则返回真
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}

