package com.example.a15521.myweatherforecast.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.a15521.myweatherforecast.R;

//add by test

//没有网络情况下调至该页面
public class NoInternet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_no_internet);

        internetAlertDialog();  //弹出警告框
    }

    //网络提示警告框
    private void internetAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(NoInternet.this);
        dialog.setMessage("您的网络已断开，无法获取到天气信息，请确认网络连接后再打开。");
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
}
