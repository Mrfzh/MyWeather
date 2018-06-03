package com.example.a15521.myweatherforecast.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.db.WeatherOperation;
import com.example.a15521.myweatherforecast.db.WeatherSQLiteOpenHelper;
import com.example.a15521.myweatherforecast.entity.FifteenDaysWeatherDate;
import com.example.a15521.myweatherforecast.entity.LifeSuggestionDate;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;
import com.example.a15521.myweatherforecast.https.SendHttpsRequest;
import com.example.a15521.myweatherforecast.utils.ParseJson;

import java.net.URLEncoder;

public class Login extends AppCompatActivity implements View.OnClickListener{

    //天气实况UI控件
    private TextView mTemperature;  //温度
    private TextView mCity;     //城市
    private TextView mWeather;  //天气
    private ImageView mWeatherPicture;  //天气图片
    private TextView mFeelsLike;    //体感温度
    private TextView mHumidity;     //相对湿度
    private TextView mPressure;     //气压
    private TextView mVisibility;   //能见度
    private TextView mWindDirection;    //风向
    private TextView mWindSpeed;    //风速
    private TextView mWindScale;    //风力等级
    private TextView mLastUpdate;   //数据最后更新时间
    //3天天气预报控件
    //今天的天气、图片、最高温度、最低温度
    private TextView mTodayText;
    private TextView mTodayWeather;
    private ImageView mTodayWeatherPicture;
    private TextView mTodayHighTemperature;
    private TextView mTodayLowTemperature;
    //明天的天气、图片、最高温度、最低温度
    private TextView mTomorrowText;
    private TextView mTomorrowWeather;
    private ImageView mTomorrowWeatherPicture;
    private TextView mTomorrowHighTemperature;
    private TextView mTomorrowLowTemperature;
    //后天的天气、图片、最高温度、最低温度
    private TextView mPostnatalText;
    private TextView mPostnatalWeather;
    private ImageView mPostnatalWeatherPicture;
    private TextView mPostnatalHighTemperature;
    private TextView mPostnatalLowTemperature;
    //15天天气预报
    private TextView mFifteenDaysWeatherText;
    private LinearLayout mFifteenDaysWeatherLayout;
    //生活指数
    private TextView mAirCondition;
    private TextView mUv;
    private TextView mUmbrella;
    private TextView mSport;
    private TextView mTraffic;
    private TextView mTravel;
    private ImageView mAirConditionImage;
    private ImageView mUvImage;
    private ImageView mUmbrellaImage;
    private ImageView mSportImage;
    private ImageView mTrafficImage;
    private ImageView mTravelImage;
    private LinearLayout mAirConditionLayout;
    private LinearLayout mUvLayout;
    private LinearLayout mUmbrellaLayout;
    private LinearLayout mSportLayout;
    private LinearLayout mTrafficLayout;
    private LinearLayout mTravelLayout;

    //分享按钮
    private ImageView mShare;
    //添加按钮
    private ImageView mAddCity;

    //下拉刷新控件
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //发送给handler的信息
    public static final int REFRESH_UI = 1;

    //存放天气实况数据
    private RealTimeWeather mRealTimeWeather;
    //存放15天天气数据
    private FifteenDaysWeatherDate mFifteenDaysWeatherDate;
    //存放生活指数数据
    private LifeSuggestionDate mLifeSuggestionDate;

    //定位城市
    private String mIpCity;

    //用于存储定位城市
    private SharedPreferences mSharedPreferences;
    //用于写入数据
    private SharedPreferences.Editor mEditor;

    //是否是中国城市
    private String isChineseCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_login);

        preparation();  //控件初始化和绑定事件

        //获得SharedPreferences和Editor实例
        mSharedPreferences = getSharedPreferences("IpCity", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        //获取上一个活动传来的要查询的城市和是否是中国城市
        Intent intent = getIntent();
        String query = intent.getStringExtra("word");
        isChineseCity = intent.getStringExtra("isChineseCity");

        //有网络则获取数据，没有网络提示用户连接网络
        if (hasInternet()) {
            if (query != null) {
                getData(query);
                //根据SharedPreferences中的数据获得定位城市
                mIpCity = mSharedPreferences.getString("ipCity", null);
            } else {
                //如果没有传来数据，则根据ip地址来确定所在城市
                getData(null);
                //将定位城市存入SharedPreferences中
                mEditor.putString("ipCity", mRealTimeWeather.getName());
                mEditor.commit();
                //设置定位城市
                mIpCity = mRealTimeWeather.getName();
            }
            setData();  //设置数据
            addDataToWeatherTable();    //将数据存到数据库的天气表中
        } else {
            startActivity(new Intent(Login.this, NoInternet.class));
        }

        //初始化下拉刷新控件和设置刷新事件
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_login_scroll);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //延迟两秒发送信息，该信息为刷新页面
                mHandler.sendEmptyMessageDelayed(REFRESH_UI, 2000);
            }
        });

    }

    //将数据存到数据库的天气表中
    private void addDataToWeatherTable() {
        //获得数据库操作类实例
        WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(Login.this, "Weather.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        WeatherOperation weatherOperation = new WeatherOperation(db, mRealTimeWeather);
        //添加数据
        weatherOperation.addDataToWeather();
    }

    //判断是否有网络
    private boolean hasInternet() {
        //获取一个用于管理网络连接的系统服务类
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo实例
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //判断网络连接状态，有网则返回真
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }
    //网络提示警告框
    private void internetAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
        dialog.setMessage("您的网络已断开，无法获取到天气信息，请确认网络连接后再重试。");
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    //处理下拉刷新时发送的消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_UI:
                    //重新获取数据和设置数据
                    getData(mRealTimeWeather.getName());
                    setData();
                    //如果还在刷新中则关闭刷新动画
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(Login.this, "刷新完成", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //从api中获取到数据并添加到集合中
    private void getData(String city) {
        try {
            //天气实况api
            String realTimeWeatherAddress = null;
            //15天天气api
            String fifteenDaysWeatherAddress = null;
            //生活指数api
            String lifeSuggestionAddress = null;

            //如果输入null，则根据ip来确定所在城市
            if (city == null) {
                realTimeWeatherAddress = "https://api.seniverse.com/v3/weather/now.json?key=moy8j7i44mvz6xw6&location=ip&language=zh-Hans&unit=c";
                fifteenDaysWeatherAddress = "https://api.seniverse.com/v3/weather/daily.json?key=moy8j7i44mvz6xw6&location=ip&language=zh-Hans&unit=c&start=-1&days=15";
                lifeSuggestionAddress = "https://api.seniverse.com/v3/life/suggestion.json?key=moy8j7i44mvz6xw6&location=ip&language=zh-Hans";
            } else {
                //这里要先将输入的词语进行utf-8编码，不然如果输入的是中文程序就会崩溃
                String cityURLEncode = URLEncoder.encode(city, "utf-8");
                realTimeWeatherAddress = "https://api.seniverse.com/v3/weather/now.json?key=moy8j7i44mvz6xw6&location=" + cityURLEncode + "&language=zh-Hans&unit=c";
                fifteenDaysWeatherAddress = "https://api.seniverse.com/v3/weather/daily.json?key=moy8j7i44mvz6xw6&location=" + cityURLEncode + "&language=zh-Hans&unit=c&start=-1&days=15";
                //是中国城市才有生活指数api
                if (isChineseCity.equals("true")) {
                    lifeSuggestionAddress = "https://api.seniverse.com/v3/life/suggestion.json?key=moy8j7i44mvz6xw6&location=" + cityURLEncode + "&language=zh-Hans";
                }
            }

            //发送网络请求返回JSON数据
            SendHttpsRequest request = new SendHttpsRequest();
            String jsonDate = null;
            jsonDate = request.sendHttpsRequest(realTimeWeatherAddress);
            //解析JSON数据并把解析后的数据保存到实体类中
            ParseJson parseJson = new ParseJson();
            parseJson.parseToRealTimeWeather(jsonDate);
            mRealTimeWeather = parseJson.getRealTimeWeather();

            jsonDate = request.sendHttpsRequest(fifteenDaysWeatherAddress);
            parseJson.parseTOFifteenDaysWeather(jsonDate);
            mFifteenDaysWeatherDate = parseJson.getFifteenDaysWeatherDate();

            //是中国城市才有生活指数json数据
            if (isChineseCity.equals("true")) {
                jsonDate = request.sendHttpsRequest(lifeSuggestionAddress);
                parseJson.parseToLifeSuggestionDate(jsonDate);
                mLifeSuggestionDate = parseJson.getLifeSuggestionDate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //控件初始化和绑定事件
    private void preparation() {
        //控件初始化
        //天气实况控件
        mTemperature = (TextView) findViewById(R.id.tv_login_temperature);
        mCity = (TextView) findViewById(R.id.tv_login_city);
        mWeather = (TextView) findViewById(R.id.tv_login_weather);
        mWeatherPicture = (ImageView) findViewById(R.id.iv_login_weather_picture);
        mFeelsLike = (TextView) findViewById(R.id.tv_login_feels_like);
        mHumidity = (TextView) findViewById(R.id.tv_login_humidity);
        mPressure = (TextView) findViewById(R.id.tv_login_pressure);
        mVisibility = (TextView) findViewById(R.id.tv_login_visibility);
        mWindDirection = (TextView) findViewById(R.id.tv_login_wind_direction);
        mWindSpeed = (TextView) findViewById(R.id.tv_login_wind_speed);
        mWindScale = (TextView) findViewById(R.id.tv_login_wind_scale);
        mLastUpdate = (TextView) findViewById(R.id.tv_login_last_update);
        //未来3天天气控件
        mTodayText = (TextView) findViewById(R.id.tv_login_today_text);
        mTodayWeather = (TextView) findViewById(R.id.tv_login_today_weather);
        mTodayWeatherPicture = (ImageView) findViewById(R.id.iv_login_today_weather_picture);
        mTodayHighTemperature = (TextView) findViewById(R.id.tv_login_today_high);
        mTodayLowTemperature = (TextView) findViewById(R.id.tv_login_today_low);
        mTomorrowText = (TextView) findViewById(R.id.tv_login_tomorrow_text);
        mTomorrowWeather = (TextView) findViewById(R.id.tv_login_tomorrow_weather);
        mTomorrowWeatherPicture = (ImageView) findViewById(R.id.iv_login_tomorrow_weather_picture);
        mTomorrowHighTemperature = (TextView) findViewById(R.id.tv_login_tomorrow_high);
        mTomorrowLowTemperature = (TextView) findViewById(R.id.tv_login_tomorrow_low);
        mPostnatalText = (TextView) findViewById(R.id.tv_login_postnatal_text);
        mPostnatalWeather = (TextView) findViewById(R.id.tv_login_postnatal_weather);
        mPostnatalWeatherPicture = (ImageView) findViewById(R.id.iv_login_postnatal_weather_picture);
        mPostnatalHighTemperature = (TextView) findViewById(R.id.tv_login_postnatal_high);
        mPostnatalLowTemperature = (TextView) findViewById(R.id.tv_login_postnatal_low);
        //15天天气控件
        mFifteenDaysWeatherText = (TextView) findViewById(R.id.tv_login_fifteen_days_weather);
        mFifteenDaysWeatherLayout = (LinearLayout) findViewById(R.id.ll_login_fifteen_days_weather);
        //生活指数控件
        mAirCondition = (TextView) findViewById(R.id.tv_login_life_suggestion_air);
        mUv = (TextView) findViewById(R.id.tv_login_life_suggestion_uv);
        mUmbrella = (TextView) findViewById(R.id.tv_login_life_suggestion_umbrella);
        mSport = (TextView) findViewById(R.id.tv_login_life_suggestion_sport);
        mTraffic = (TextView) findViewById(R.id.tv_login_life_suggestion_traffic);
        mTravel = (TextView) findViewById(R.id.tv_login_life_suggestion_travel);
        mAirConditionImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_air);
        mUvImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_uv);
        mUmbrellaImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_umbrella);
        mSportImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_sport);
        mTrafficImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_traffic);
        mTravelImage = (ImageView) findViewById(R.id.iv_login_life_suggestion_travel);
        mAirConditionLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_air);
        mUvLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_uv);
        mUmbrellaLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_umbrella);
        mSportLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_sport);
        mTrafficLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_traffic);
        mTravelLayout = (LinearLayout) findViewById(R.id.ll_login_life_suggestion_travel);
        //底部按钮
        mShare = (ImageView) findViewById(R.id.iv_login_share);
        mAddCity = (ImageView) findViewById(R.id.iv_login_add_city);

        //绑定事件
        mTodayText.setOnClickListener(this);
        mTomorrowText.setOnClickListener(this);
        mPostnatalText.setOnClickListener(this);

        mFifteenDaysWeatherText.setOnClickListener(this);
        mFifteenDaysWeatherLayout.setOnClickListener(this);

        mAirCondition.setOnClickListener(this);
        mUv.setOnClickListener(this);
        mUmbrella.setOnClickListener(this);
        mSport.setOnClickListener(this);
        mTraffic.setOnClickListener(this);
        mTravel.setOnClickListener(this);
        mAirConditionImage.setOnClickListener(this);
        mUvImage.setOnClickListener(this);
        mUmbrellaImage.setOnClickListener(this);
        mSportImage.setOnClickListener(this);
        mTrafficImage.setOnClickListener(this);
        mTravelImage.setOnClickListener(this);
        mAirConditionLayout.setOnClickListener(this);
        mUvLayout.setOnClickListener(this);
        mUmbrellaLayout.setOnClickListener(this);
        mSportLayout.setOnClickListener(this);
        mTrafficLayout.setOnClickListener(this);
        mTravelLayout.setOnClickListener(this);

        mShare.setOnClickListener(this);
        mAddCity.setOnClickListener(this);
    }

    //设置文本和图片
    private void setData() {
        mTemperature.setText(mRealTimeWeather.getTemperature());
        mCity.setText(mRealTimeWeather.getName());
        mWeather.setText(mRealTimeWeather.getText());
        mWeatherPicture.setImageBitmap(getBitmapByName("weather_" + mRealTimeWeather.getCode()));
        mFeelsLike.setText(mRealTimeWeather.getFeelsLike() + "℃");
        mHumidity.setText(mRealTimeWeather.getHumidity() + "%");
        mPressure.setText(mRealTimeWeather.getPressure() + "mb");
        mVisibility.setText(mRealTimeWeather.getVisibility() + "km");
        mWindDirection.setText(mRealTimeWeather.getWindDirection());
        mWindSpeed.setText(mRealTimeWeather.getWindSpeed() + "km/h");
        mWindScale.setText(mRealTimeWeather.getWindScale());
        mLastUpdate.setText(mRealTimeWeather.getLastUpdate());

        mTodayWeather.setText(mFifteenDaysWeatherDate.getWeatherDay().get(1));
        mTodayWeatherPicture.setImageBitmap(getBitmapByName("weather_" + mFifteenDaysWeatherDate.getCodeDay().get(1)));
        mTodayHighTemperature.setText(mFifteenDaysWeatherDate.getHigh().get(1));
        mTodayLowTemperature.setText(mFifteenDaysWeatherDate.getLow().get(1));
        mTomorrowWeather.setText(mFifteenDaysWeatherDate.getWeatherDay().get(2));
        mTomorrowWeatherPicture.setImageBitmap(getBitmapByName("weather_" + mFifteenDaysWeatherDate.getCodeDay().get(2)));
        mTomorrowHighTemperature.setText(mFifteenDaysWeatherDate.getHigh().get(2));
        mTomorrowLowTemperature.setText(mFifteenDaysWeatherDate.getLow().get(2));
        mPostnatalWeather.setText(mFifteenDaysWeatherDate.getWeatherDay().get(3));
        mPostnatalWeatherPicture.setImageBitmap(getBitmapByName("weather_" + mFifteenDaysWeatherDate.getCodeDay().get(3)));
        mPostnatalHighTemperature.setText(mFifteenDaysWeatherDate.getHigh().get(3));
        mPostnatalLowTemperature.setText(mFifteenDaysWeatherDate.getLow().get(3));

        //判断是否为中国城市，设置不同的文本
        if (isChineseCity.equals("true")) {
            mAirCondition.setText("空气质量" + mLifeSuggestionDate.getAirCondition());
            mUv.setText("紫外线" + mLifeSuggestionDate.getUv());
            mUmbrella.setText(mLifeSuggestionDate.getUmbrella());
            mSport.setText("运动" + mLifeSuggestionDate.getSport());
            mTraffic.setText("交通" + mLifeSuggestionDate.getTraffic());
            mTravel.setText("旅游" + mLifeSuggestionDate.getTravel());
        } else {
            mAirCondition.setText("");
            mUv.setText("");
            mUmbrella.setText("");
            mSport.setText("");
            mTraffic.setText("");
            mTravel.setText("");
        }

    }

    //通过图片名返回图片的Bitmap
    private Bitmap getBitmapByName(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击3天预报，可进入15天天气预报对应日期
            case R.id.tv_login_today_text:
                Intent todayIntent = new Intent(Login.this, FifteenDaysWeather.class);
                todayIntent.putExtra("word", mRealTimeWeather.getName());
                todayIntent.putExtra("date", "today");
                startActivity(todayIntent);
                break;
            case R.id.tv_login_tomorrow_text:
                Intent tomorrowIntent = new Intent(Login.this, FifteenDaysWeather.class);
                tomorrowIntent.putExtra("word", mRealTimeWeather.getName());
                tomorrowIntent.putExtra("date", "tomorrow");
                startActivity(tomorrowIntent);
                break;
            case R.id.tv_login_postnatal_text:
                Intent postnatalIntent = new Intent(Login.this, FifteenDaysWeather.class);
                postnatalIntent.putExtra("word", mRealTimeWeather.getName());
                postnatalIntent.putExtra("date", "postnatal");
                startActivity(postnatalIntent);
                break;

            //点击进入15天天气预报
            case R.id.tv_login_fifteen_days_weather:
            case R.id.ll_login_fifteen_days_weather:
                if (hasInternet()) {
                    Intent intent = new Intent(Login.this, FifteenDaysWeather.class);
                    intent.putExtra("word", mRealTimeWeather.getName());
                    intent.putExtra("date", "today");
                    startActivity(intent);
                } else {
                    internetAlertDialog();
                }
                break;

            //点击生活指数各文本
            //点击空气情况
            case R.id.ll_login_life_suggestion_air:
            case R.id.iv_login_life_suggestion_air:
            case R.id.tv_login_life_suggestion_air:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getAirConditionDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;
            //点击紫外线情况
            case R.id.ll_login_life_suggestion_uv:
            case R.id.iv_login_life_suggestion_uv:
            case R.id.tv_login_life_suggestion_uv:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getUvDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;
            //点击雨伞情况
            case R.id.ll_login_life_suggestion_umbrella:
            case R.id.iv_login_life_suggestion_umbrella:
            case R.id.tv_login_life_suggestion_umbrella:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getUmbrellaDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;
            //点击运动情况
            case R.id.ll_login_life_suggestion_sport:
            case R.id.iv_login_life_suggestion_sport:
            case R.id.tv_login_life_suggestion_sport:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getSportDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;
            //点击交通情况
            case R.id.ll_login_life_suggestion_traffic:
            case R.id.iv_login_life_suggestion_traffic:
            case R.id.tv_login_life_suggestion_traffic:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getTrafficDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;
            //点击旅游情况
            case R.id.ll_login_life_suggestion_travel:
            case R.id.iv_login_life_suggestion_travel:
            case R.id.tv_login_life_suggestion_travel:
                if (isChineseCity.equals("true")) {
                    lifeSuggestionDialog(mLifeSuggestionDate.getTravelDetails());
                }else {
                    lifeSuggestionDialog("很抱歉，该城市没有生活指数数据。");
                }
                break;

            //点击分享按钮
            case R.id.iv_login_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");   //原样输出文本
                shareIntent.putExtra(Intent.EXTRA_TEXT, getShareContent());    //传入内容
                startActivity(shareIntent);
                break;
            //点击添加按钮
            case R.id.iv_login_add_city:
                Intent intent = new Intent(Login.this, CityManagement.class);
                intent.putExtra("city", mIpCity);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //点击生活指数文本后弹出的消息框
    private void lifeSuggestionDialog(String tip) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
        dialog.setMessage(tip);
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    //生成分享内容
    private String getShareContent() {
        StringBuilder builder = new StringBuilder();
        builder.append("[分享] 天气实况：" + '\n');
        builder.append("您好，" + mRealTimeWeather.getName() + "现在的温度为" + mRealTimeWeather.getTemperature() + "℃，" + mRealTimeWeather.getText() + "。" +'\n');
        builder.append("体感温度为" + mRealTimeWeather.getFeelsLike() + "℃，" + "相对湿度为" + mRealTimeWeather.getHumidity() + "%。");
        return  builder.toString();
    }
}
