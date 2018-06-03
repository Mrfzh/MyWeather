package com.example.a15521.myweatherforecast.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a15521.myweatherforecast.R;

import java.util.List;

/**
 * Created by 15521 on 2018/5/21.
 */

//城市天气列表适配器
public class CityWeatherAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mCityList;
    private List<String> mPictureList;
    private List<String> mTemperatureList;
    private String mIpCity;

    public CityWeatherAdapter(Context mContext, List<String> mCityList, List<String> mPictureList, List<String> mTemperatureList, String mIpCity) {
        this.mCityList = mCityList;
        this.mContext = mContext;
        this.mPictureList = mPictureList;
        this.mTemperatureList = mTemperatureList;
        this.mIpCity = mIpCity;
    }

    @Override
    public int getCount() {
        return mCityList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_city_weather, null);
            holder.city = (TextView) convertView.findViewById(R.id.tv_lv_city_weather_city);
            holder.fixedPosition = (ImageView) convertView.findViewById(R.id.iv_lv_city_weather_fixed_position);
            holder.picture = (ImageView) convertView.findViewById(R.id.iv_lv_city_weather_picture);
            holder.temperature = (TextView) convertView.findViewById(R.id.tv_lv_city_weather_temperature);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置文本和图片
        holder.city.setText(mCityList.get(position));
        holder.picture.setImageBitmap(getBitmapByName("weather_" + mPictureList.get(position)));
        holder.temperature.setText(mTemperatureList.get(position));
        //如果不是定位城市，则隐藏定位图标
        if (!mCityList.get(position).equals(mIpCity)) {
            holder.fixedPosition.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //该类用于缓存控件
    private class ViewHolder {
        TextView city;
        ImageView fixedPosition;    //定位图标
        ImageView picture;
        TextView temperature;
    }

    //通过图片名返回图片的Bitmap
    private Bitmap getBitmapByName(String name) {
        ApplicationInfo appInfo = mContext.getApplicationInfo();
        int resID = mContext.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(mContext.getResources(), resID);
    }

}
