package com.example.a15521.myweatherforecast.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.db.WeatherOperation;
import com.example.a15521.myweatherforecast.db.WeatherSQLiteOpenHelper;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;
import com.example.a15521.myweatherforecast.https.SendHttpsRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchCity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener{

    //控件
    private SearchView mSearch;
    private ImageView mClearRecordImage;
    private ImageView mBackImage;
    private LinearLayout mBackLayout;
    private ListView mRecordListView;

    //该集合用于存放历史记录数据
    private List<String> mRecordList;

    //数据库操作类
    private WeatherOperation mWeatherOperation;

    //定位城市
    private String mIpCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_search_city);

        preparation();  //控件初始化和绑定事件

        //获取定位城市
        Intent intent = getIntent();
        mIpCity = intent.getStringExtra("city");

        //获得数据库操作类实例
        WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(SearchCity.this, "Weather.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mWeatherOperation = new WeatherOperation(db, new RealTimeWeather());

        updateList();

        //设置历史记录点击事件
        mRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //进行搜索
                queryWeather(mRecordList.get(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    //更新列表
    private void updateList() {
        //初始化集合
        mRecordList = new ArrayList<>();
        //将数据库中记录表的数据添加到集合中
        mWeatherOperation.addRecordDataToList(mRecordList);
        //将集合中的数据倒置
        Collections.reverse(mRecordList);
        //创建适配器并给ListView设置适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchCity.this, android.R.layout.simple_list_item_1, mRecordList);
        mRecordListView.setAdapter(adapter);
    }

    //控件初始化和绑定事件
    private void preparation() {
        mSearch = (SearchView) findViewById(R.id.sv_search_city_search);
        mClearRecordImage = (ImageView) findViewById(R.id.iv_search_city_clear_record);
        mBackImage = (ImageView) findViewById(R.id.iv_search_city_back);
        mBackLayout = (LinearLayout) findViewById(R.id.ll_search_city_back);
        mRecordListView = (ListView) findViewById(R.id.lv_search_city_record_list);

        mBackImage.setOnClickListener(this);
        mBackLayout.setOnClickListener(this);
        mClearRecordImage.setOnClickListener(this);
        mSearch.setOnQueryTextListener(this);
        mSearch.setSubmitButtonEnabled(true);   //显示搜索按钮
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        //添加搜索数据进入数据库记录表中
        mWeatherOperation.addDataToRecord(query);
        //更新列表
        updateList();
        //进行搜索
        queryWeather(query);

        return true;
    }

    //搜索城市天气
    private void queryWeather(String query) {
        //搜索前先判断是否有网络
        if (hasInternet()) {
            try {
                //再判断输入是否有效,无效则提醒用户
                if (isValid(query)) {
                    Intent intent = new Intent(SearchCity.this, Login.class);
                    //再判断是否是中国城市
                    if (isChineseCity(query)) {
                        intent.putExtra("isChineseCity", "true");
                        //判断是否是定位城市
                        if (query.equals(mIpCity)) {
                            startActivity(intent);
                        } else {
                            intent.putExtra("word", query);
                            startActivity(intent);
                        }
                    } else {
                        intent.putExtra("isChineseCity", "false");
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SearchCity.this, "很遗憾，没有找到您所输入的城市。", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            internetAlertDialog();  //弹出警告框
        }
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchCity.this);
        dialog.setMessage("您的网络已断开，无法获取到天气信息，请确认网络连接后再重试。");
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    //判断输入的城市是否有效
    private boolean isValid(String query) throws Exception{
        //获得json数据
        String jsonData = getJsonDataByCity(query);
        //如果json数据中有results，则输入有效
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.has("results");
    }

    //判断输入的城市是否是中国城市
    private boolean isChineseCity(String query) throws Exception {
        //获得json数据
        String jsonData = getJsonDataByCity(query);
        //如果json数据中"country"为"CN"，则该城市为中国城市
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray results = jsonObject.getJSONArray("results");
        JSONObject data = results.getJSONObject(0);
        JSONObject location = data.getJSONObject("location");

        return location.getString("country").equals("CN");
    }

    //根据城市返回天气实况api数据
    private String getJsonDataByCity(String query) throws Exception {
        //先进行编码
        String queryURLEncode = URLEncoder.encode(query, "utf-8");
        //获得api
        String address = "https://api.seniverse.com/v3/weather/now.json?key=moy8j7i44mvz6xw6&location=" + queryURLEncode + "&language=zh-Hans&unit=c";
        //发送网络请求获得json数据
        SendHttpsRequest request = new SendHttpsRequest();
        return request.sendHttpsRequest(address);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //点击返回按钮
            case R.id.ll_search_city_back:
            case R.id.iv_search_city_back:
                finish();
                break;

            //点击垃圾箱清除历史记录
            case R.id.iv_search_city_clear_record:
                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchCity.this);
                dialog.setMessage("是否要清除历史记录");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWeatherOperation.clearRecord();    //清除数据库中记录表的数据
                        updateList();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

            default:
                break;
        }
    }
}
