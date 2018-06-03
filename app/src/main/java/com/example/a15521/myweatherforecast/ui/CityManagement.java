package com.example.a15521.myweatherforecast.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.adapter.CityWeatherAdapter;
import com.example.a15521.myweatherforecast.db.WeatherOperation;
import com.example.a15521.myweatherforecast.db.WeatherSQLiteOpenHelper;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;

import java.util.ArrayList;
import java.util.List;

//城市管理界面
public class CityManagement extends AppCompatActivity implements View.OnClickListener{

    //控件
    private ImageView mBackImage;
    private LinearLayout mBackLayout;
    private ImageView mMenuImage;
    private LinearLayout mMenuLayout;
    private ListView mCityWeatherListView;

    //数据库操作类
    WeatherOperation mWeatherOperation;

    //存放天气数据的集合
    private List<String> mCityList;
    private List<String> mPictureList;
    private List<String> mTemperatureList;

    //定位城市
    private String mIpCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_city_management);

        //获取定位城市
        Intent intent = getIntent();
        mIpCity = intent.getStringExtra("city");

        preparation();  //初始化控件和绑定事件

        //获得数据库操作类实例
        WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(CityManagement.this, "Weather.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mWeatherOperation = new WeatherOperation(db, new RealTimeWeather());

        //列表点击事件
        mCityWeatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //有网的时候才可以跳转到对应的城市天气中
                if (hasInternet()) {
                    Intent intent = new Intent(CityManagement.this, Login.class);
                    intent.putExtra("word", mCityList.get(position));
                    startActivity(intent);
                } else {
                    Toast.makeText(CityManagement.this, "当前没有网络，请确认网络连接后再重试。", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();   //更新列表
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

    //更新列表
    private void updateList() {
        //初始化集合
        mCityList = new ArrayList<>();
        mPictureList = new ArrayList<>();
        mTemperatureList = new ArrayList<>();
        //将数据库的数据存入集合中
        mWeatherOperation.addWeatherDataToList(mCityList, mPictureList, mTemperatureList);
        //初始化适配器
        CityWeatherAdapter cityWeatherAdapter = new CityWeatherAdapter(CityManagement.this, mCityList, mPictureList, mTemperatureList, mIpCity);
        //设置适配器
        mCityWeatherListView.setAdapter(cityWeatherAdapter);
    }

    //初始化控件和绑定事件
    private void preparation() {
        mBackImage = (ImageView) findViewById(R.id.iv_city_management_back);
        mBackLayout = (LinearLayout) findViewById(R.id.ll_city_management_back);
        mMenuImage = (ImageView) findViewById(R.id.iv_city_management_menu);
        mMenuLayout = (LinearLayout) findViewById(R.id.ll_city_management_menu);
        mCityWeatherListView = (ListView) findViewById(R.id.lv_city_management_weather_city);

        mBackImage.setOnClickListener(this);
        mBackLayout.setOnClickListener(this);
        mMenuImage.setOnClickListener(this);
        mMenuLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击返回按钮
            case R.id.ll_city_management_back:
            case R.id.iv_city_management_back:
                finish();
                break;
            //点击菜单栏
            case R.id.ll_city_management_menu:
            case R.id.iv_city_management_menu:
                showPopupMenu(mMenuImage);  //显示菜单
                break;
            default:
                break;
        }
    }

    //显示菜单
    private void showPopupMenu(View view) {
        // 第二个参数为popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(CityManagement.this, view);
        //获取菜单文件
        popupMenu.getMenuInflater().inflate(R.menu.city_management_menu, popupMenu.getMenu());
        //显示菜单
        popupMenu.show();
        //设置点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //点击城市查询
                    case R.id.menu_city_management_search_city:
                        Intent searchIntent = new Intent(CityManagement.this, SearchCity.class);
                        searchIntent.putExtra("city", mIpCity);
                        startActivity(searchIntent);
                        break;
                    //点击编辑城市
                    case R.id.menu_city_management_delete_city:
                        Intent intent = new Intent(CityManagement.this, CityEdit.class);
                        intent.putExtra("city", mIpCity);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
