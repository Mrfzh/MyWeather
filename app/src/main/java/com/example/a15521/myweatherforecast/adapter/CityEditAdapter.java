package com.example.a15521.myweatherforecast.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a15521.myweatherforecast.R;
import com.example.a15521.myweatherforecast.db.WeatherOperation;
import com.example.a15521.myweatherforecast.db.WeatherSQLiteOpenHelper;
import com.example.a15521.myweatherforecast.entity.RealTimeWeather;
import com.example.a15521.myweatherforecast.ui.Login;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15521 on 2018/5/21.
 */

//城市编辑列表适配器
public class CityEditAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mCityList;
    private String mIpCity;

    public CityEditAdapter(Context mContext, List<String> mCityList, String mIpCity) {
        this.mCityList = mCityList;
        this.mContext = mContext;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_city_edit, null);
            holder.deleteCity = (ImageView) convertView.findViewById(R.id.iv_lv_city_edit_delete_city);
            holder.city = (TextView) convertView.findViewById(R.id.tv_lv_city_edit_city);
            holder.fixedPosition = (ImageView) convertView.findViewById(R.id.iv_lv_city_edit_fixed_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置文本
        holder.city.setText(mCityList.get(position));
        //如果不是定位城市，则隐藏定位图标
        if (!mCityList.get(position).equals(mIpCity)) {
            holder.fixedPosition.setVisibility(View.INVISIBLE);
        }
        //设置图标点击事件
        holder.deleteCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle(mCityList.get(position));
                dialog.setMessage("是否要删除这个城市");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获得数据库操作类实例
                        WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(mContext, "Weather.db", null, 1);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        WeatherOperation weatherOperation = new WeatherOperation(db, new RealTimeWeather());
                        //从数据库中删除该数据
                        weatherOperation.deleteCity(mCityList.get(position));
                        //更新list集合和适配器
                        mCityList = new ArrayList<>();
                        weatherOperation.addCityDataToList(mCityList);
                        CityEditAdapter.this.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView deleteCity;
        TextView city;
        ImageView fixedPosition;    //定位图标
    }
}
