package com.example.a15521.myweatherforecast.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.adapter.CityEditAdapter;
import com.example.a15521.myweatherforecast.db.WeatherOperation;
import com.example.a15521.myweatherforecast.db.WeatherSQLiteOpenHelper;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//城市编辑页面
public class CityEdit extends AppCompatActivity implements View.OnClickListener{

    //控件
    private ImageView mEnsureImage;
    private LinearLayout mEnsureLayout;
    private ListView mCityEditListView;

    //数据库操作类
    private WeatherOperation mWeatherOperation;

    //存储城市数据
    private List<String> mCityList;

    //定位城市
    private String mIpCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_city_edit);

        //获取定位城市
        Intent intent = getIntent();
        mIpCity = intent.getStringExtra("city");

        preparation();  //控件初始化和绑定事件

        //获得数据库操作类实例
        WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(CityEdit.this, "Weather.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mWeatherOperation = new WeatherOperation(db, new RealTimeWeather());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();   //更新列表
    }

    //更新列表
    private void updateList() {
        //初始化集合
        mCityList = new ArrayList<>();
        //将数据库中的城市数据存到集合中
        mWeatherOperation.addCityDataToList(mCityList);
        //初始化和设置适配器
        CityEditAdapter cityEditAdapter = new CityEditAdapter(CityEdit.this, mCityList, mIpCity);
        mCityEditListView.setAdapter(cityEditAdapter);
    }

    //控件初始化和绑定事件
    private void preparation() {
        mEnsureImage = (ImageView) findViewById(R.id.iv_city_edit_ensure);
        mEnsureLayout = (LinearLayout) findViewById(R.id.ll_city_edit_ensure);
        mCityEditListView = (ListView) findViewById(R.id.lv_city_edit_list);

        mEnsureImage.setOnClickListener(this);
        mEnsureLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击确认
            case R.id.ll_city_edit_ensure:
            case R.id.iv_city_edit_ensure:
                finish();
                break;

            default:
                break;
        }
    }
}
