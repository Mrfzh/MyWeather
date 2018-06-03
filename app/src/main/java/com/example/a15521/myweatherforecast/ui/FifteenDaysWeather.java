package com.example.a15521.myweatherforecast.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.entity.FifteenDaysWeatherDate;
import com.example.a15521.myweatherforecast.https.SendHttpsRequest;
import com.example.a15521.myweatherforecast.utils.ParseJson;
import com.example.a15521.myweatherforecast.widget.FifteenDaysChart;
import com.example.a15521.myweatherforecast.widget.ScrollFifteenDaysWeather;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FifteenDaysWeather extends AppCompatActivity implements View.OnClickListener{

    //自定义控件
    private ScrollFifteenDaysWeather mScrollFifteenDaysWeather;
    private FifteenDaysChart mFifteenDaysChart;

    //返回按钮
    private ImageView mBack;

    //15天天气数据
    private FifteenDaysWeatherDate mFifteenDaysWeatherDate;

    //view集合
    private List<View> mViewList;

    //获取上一个活动传来的日期
    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_fifteen_days_weather);

        //初始化控件和绑定事件
        preparation();

        try {
            //获取上一个活动传来的要查询的城市和对应的日期
            Intent intent = getIntent();
            String query = intent.getStringExtra("word");
            mDate = intent.getStringExtra("date");

            //将中文进行编码
            String queryURLEncode = URLEncoder.encode(query, "utf-8");

            //15天天气api
            String fifteenDaysWeatherAddress = "https://api.seniverse.com/v3/weather/daily.json?key=moy8j7i44mvz6xw6&location=" + queryURLEncode + "&language=zh-Hans&unit=c&start=-1&days=15";
            //发送网络请求返回JSON数据
            SendHttpsRequest request = new SendHttpsRequest();
            String jsonDate = request.sendHttpsRequest(fifteenDaysWeatherAddress);
            //解析JSON数据并把解析后的数据保存到实体类中
            ParseJson parseJson = new ParseJson();
            parseJson.parseTOFifteenDaysWeather(jsonDate);
            mFifteenDaysWeatherDate = parseJson.getFifteenDaysWeatherDate();

            //给图表传数据
            mFifteenDaysChart.setDatas(mFifteenDaysWeatherDate.getHigh(), mFifteenDaysWeatherDate.getLow());

            //设置文本和图片
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化控件和绑定事件
    private void preparation() {
        mScrollFifteenDaysWeather = (ScrollFifteenDaysWeather) findViewById(R.id.scroll_fifteen_days_weather);
        mFifteenDaysChart = mScrollFifteenDaysWeather.getFifteenDaysChart();
        mBack = (ImageView) findViewById(R.id.iv_fifteen_days_weather_back);

        mBack.setOnClickListener(this);
    }

    //设置文本和图片
    private void setData() throws Exception{
        mViewList = mScrollFifteenDaysWeather.getViews();
        //给每一个view设置文本和图片
        for (int i = 0; i < mViewList.size(); i++) {
            View view = mViewList.get(i);

            TextView weekday = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_weekday);
            TextView date = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_date);
            ImageView pictureTop = (ImageView) view.findViewById(R.id.iv_fifteen_days_weather_chart_top_picture);
            TextView weatherTop = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_top_weather);
            ImageView pictureBottom = (ImageView) view.findViewById(R.id.iv_fifteen_days_weather_chart_bottom_picture);
            TextView weatherBottom = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_bottom_weather);
            TextView windDirection = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_wind_direction);
            TextView windScale = (TextView) view.findViewById(R.id.tv_fifteen_days_weather_chart_wind_scale);

            weekday.setText(getWeekday(mFifteenDaysWeatherDate.getDate().get(i)));
            date.setText(mFifteenDaysWeatherDate.getDate().get(i));
            pictureTop.setImageBitmap(getBitmapByName("weather_" + mFifteenDaysWeatherDate.getCodeDay().get(i)));
            weatherTop.setText(mFifteenDaysWeatherDate.getWeatherDay().get(i));
            pictureBottom.setImageBitmap(getBitmapByName("weather_" + mFifteenDaysWeatherDate.getCodeNight().get(i)));
            weatherBottom.setText(mFifteenDaysWeatherDate.getWeatherNight().get(i));
            windDirection.setText(mFifteenDaysWeatherDate.getWindDirection().get(i));
            windScale.setText(mFifteenDaysWeatherDate.getWindScale().get(i) + "级");

            //对昨天的设置
            if (0 == i) {
                weekday.setText("昨天");
                weekday.setTextColor(Color.GRAY);
                weatherTop.setTextColor(Color.GRAY);
                weatherBottom.setTextColor(Color.GRAY);
                windDirection.setTextColor(Color.GRAY);
                windScale.setTextColor(Color.GRAY);
            }
            //对今天的设置
            if (1 == i) {
                weekday.setText("今天");
                if (mDate.equals("today")) {
                    weekday.setTextColor(getResources().getColor(R.color.color_blue));
                    view.setBackgroundColor(getResources().getColor(R.color.today_color));
                }
            }
            //对明天的设置
            if (2 == i) {
                weekday.setText("明天");
                if (mDate.equals("tomorrow")) {
                    weekday.setTextColor(getResources().getColor(R.color.color_blue));
                    view.setBackgroundColor(getResources().getColor(R.color.today_color));
                }
            }
            //对后天的设置
            if (3 == i) {
                weekday.setText("后天");
                if (mDate.equals("postnatal")) {
                    weekday.setTextColor(getResources().getColor(R.color.color_blue));
                    view.setBackgroundColor(getResources().getColor(R.color.today_color));
                }
            }
        }
    }

    //通过图片名返回图片的Bitmap
    private Bitmap getBitmapByName(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

    //通过日期返回星期几
    public static String getWeekday(String date) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatWeekday = new SimpleDateFormat("E");
        Date d = simpleDateFormat.parse(date);
        return simpleDateFormatWeekday.format(d);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_fifteen_days_weather_back:
                finish();
                break;
            default:
                break;
        }
    }
}
