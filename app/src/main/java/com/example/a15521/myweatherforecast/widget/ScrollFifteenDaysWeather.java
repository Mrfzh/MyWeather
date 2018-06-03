package com.example.a15521.myweatherforecast.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a15521.myweatherforecast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15521 on 2018/5/18.
 */

//自定义ViewGroup：15天天气滑动控件
public class ScrollFifteenDaysWeather extends ViewGroup {

    public ScrollFifteenDaysWeather(Context context) {
        super(context);
        init(context);
    }

    public ScrollFifteenDaysWeather(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollFifteenDaysWeather(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //每个item的宽度
    private int mFifteenDaysItemWidth;
    //温度图表的高度
    private int mFifteenDaysChartHeight;
    //未来若干天天气控件总宽度（ViewGroup的宽度）
    private int mFifteenDaysTotalWidth;
    //每个item的具体宽度，该宽度可传入FifteenDaysChart类中
    public static final int ITEM_WIDTH = 80;
    //图表
    private FifteenDaysChart mFifteenDaysChart;
    //未来若干天天气View的集合（每个item都是一样的）
    private List<View> contents = new ArrayList<>();

    //一些初始化操作
    private void init(Context context) {
        //这里TypedValue.applyDimension()的作用是转换尺寸，例如这里把80转换成80dp，下同
        mFifteenDaysItemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ITEM_WIDTH, getResources().getDisplayMetrics());
        mFifteenDaysChartHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FifteenDaysChart.CHART_HEIGHT, getResources().getDisplayMetrics());
        mFifteenDaysTotalWidth = mFifteenDaysItemWidth * 15;

        for (int i = 0; i < 15; i++) {
            //每一天的view
            View view = LayoutInflater.from(context).inflate(R.layout.item_fifteen_days_weather, null, false);
            //给这个折线图控件设置高度
            view.findViewById(R.id.view_fifteen_days_weather_chart).getLayoutParams().height = mFifteenDaysChartHeight;
            //将这个view添加到集合中
            contents.add(view);
            //添加这个view到容器中并给这个view设置宽度和高度
            addView(view, new LayoutParams(mFifteenDaysItemWidth, LayoutParams.WRAP_CONTENT));
        }
        //初始化折线图控件并把这个控件添加到容器中
        mFifteenDaysChart = new FifteenDaysChart(context);
        addView(mFifteenDaysChart, new LayoutParams(mFifteenDaysTotalWidth, LayoutParams.WRAP_CONTENT));
    }

    //返回图表和view集合
    public FifteenDaysChart getFifteenDaysChart(){
        return mFifteenDaysChart;
    }
    public List<View> getViews(){
        return contents;
    }

    //对子View的大小进行测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight = 0;
        //将一个view中的所有可见控件的高度加起来
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if(childView.getVisibility() != View.GONE){
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                totalHeight += childView.getMeasuredHeight();
            }
        }
        //为ViewGroup设置宽高
        setMeasuredDimension(mFifteenDaysTotalWidth, totalHeight);
    }

    //ViewGroup通过onLayout方法来确定View在容器中的位置
    //View通过layout方法来确认自己在父容器中的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        //确认15天预报在容器中的位置
        //这里的getChildCount()为16，因为ViewGroup中除了15个item还有一个折线图控件，所以这里要减一
        for (int j = 0; j < getChildCount()-1; j++) {
            View child = getChildAt(j);
            if(child.getVisibility() != View.GONE){
				/*
				layout里面的四个参数分别为View左边界距离父容器的左边界的距离，View上边界距离父容器上边界的距离
				View右边界距离父容器左边界的距离，View下边界距离父容器上边界的距离
				*/
                child.layout(left, 0, left + child.getMeasuredWidth(), child.getMeasuredHeight());
                left += mFifteenDaysItemWidth;
            }
        }
        //确认折线图在容器中的位置
        //int top = contents.get(0).findViewById(R.id.view_fifteen_days_weather_chart).getTop();		//view距离容器顶部的距离
        //getChildAt(getChildCount() - 1).layout(0, top, getMeasuredWidth(), top + mFifteenDaysChartHeight);
        View emptyView=contents.get(0).findViewById(R.id.view_fifteen_days_weather_chart);
        int top=emptyView.getTop();		//view距离容器顶部的距离
        View last=getChildAt(getChildCount()-1);
        last.layout(0,top,getMeasuredWidth(),top+mFifteenDaysChartHeight);
    }
}
