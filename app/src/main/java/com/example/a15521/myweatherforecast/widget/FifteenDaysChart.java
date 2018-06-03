package com.example.a15521.myweatherforecast.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 15521 on 2018/5/18.
 */

//自定义控件：未来15天天气图表
public class FifteenDaysChart extends View {

    //继承父类的方法和进行初始化操作
    public FifteenDaysChart(Context context) {
        super(context);
        init();
    }

    public FifteenDaysChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FifteenDaysChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FifteenDaysChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //图表的具体高度，这里的数据可传给自定义ViewGroup中
    public static final int CHART_HEIGHT = 150;
    //具体绘制图表的时候每个图表（分上下两个图表，分别表示最高和最低温度）所占的实际高度（就是用于画点的高度）
    private float eachChartHeight;
    //图表数据的个数
    private int chartNumber;
    //图表的高度
    private float chartHeight;
    //绘制图表留出的上下padding值(给文字留出的绘制空间)
    private float padding;
    //图表圆点的半径
    private float pointRaidus;
    //两个图表的单位值所占的像素高度
    private float perHeightTop, perHeightBottom;
    //白天温度和夜晚温度的最小最大值（注意：这里是两根曲线，所以有两套最大最小值）
    private float minHigh, minLow, maxHigh, maxLow;
    //图表的线的paint
    private Paint linePaint = new Paint();
    //图表的坐标点的paint
    private Paint pointPaint = new Paint();
    //图表的坐标点的值的paint
    private TextPaint labelPaint=new TextPaint();
    // 绘制曲线的路径
    private Path path = new Path();
    //传入的数据集合（每日最高、最低气温）
    private List<String> highTemperatureList;
    private List<String> lowTemperatureList;

    //一些初始化操作
    private void init(){
        //这里TypedValue.applyDimension()的作用是转换尺寸，例如这里把3转换成3dp，下同
        pointRaidus = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());  //设置圆点半径

        //设置线条画笔
        linePaint.setAntiAlias(true);	//设置抗锯齿
        linePaint.setStyle(Paint.Style.STROKE);		//设置画笔的样式为描边
        linePaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        linePaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        //linePaint.setDither(true);//防抖动
        //linePaint.setShader(null);
        linePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics()));	//设置画笔宽度
        linePaint.setColor(Color.GRAY); //设置颜色

        //设置圆点画笔
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointPaint.setColor(Color.GRAY);

        //设置字体画笔
        labelPaint.setAntiAlias(true);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));

        //字体的高度
        float labelHeight = labelPaint.getFontMetrics().bottom - labelPaint.getFontMetrics().top;
        //给字体流出的空间
        padding = labelHeight * 3;

        //设置整个图表的高度
        chartHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CHART_HEIGHT, getResources().getDisplayMetrics());
        //每一个图表实际用于画点的高度
        eachChartHeight = chartHeight/2 - padding;
    }

    //设置数据
    public void setDatas(List<String> highTemperatureList, List<String> lowTemperatureList) {

        //设置集合
        this.highTemperatureList = highTemperatureList;
        this.lowTemperatureList = lowTemperatureList;

        //设置图表数
        chartNumber = highTemperatureList.size();

        //用循环来找出集合中最高、最低温度的最大值和最小值
        minHigh = Float.parseFloat(highTemperatureList.get(0));
        maxHigh = Float.parseFloat(highTemperatureList.get(0));
        minLow = Float.parseFloat(lowTemperatureList.get(0));
        maxLow = Float.parseFloat(lowTemperatureList.get(0));
        for (int i = 1; i < highTemperatureList.size(); i++) {
            float fh = Float.parseFloat(highTemperatureList.get(i));
            if(minHigh > fh){
                minHigh = fh;
            }
            if(maxHigh < fh){
                maxHigh = fh;
            }
            float fl = Float.parseFloat(lowTemperatureList.get(i));
            if(minLow > fl){
                minLow = fl;
            }
            if(maxLow < fl){
                maxLow = fl;
            }
        }

        //转换比例，求出最大和最小值的差，然后进行换算得出每个梯度所占像素
        perHeightTop = eachChartHeight/(maxHigh - minHigh);
        perHeightBottom = eachChartHeight/(maxLow - minLow);

        //更新界面
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //设置每一天图表的宽度
        float eachWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ScrollFifteenDaysWeather.ITEM_WIDTH, getContext().getResources().getDisplayMetrics());
        //重置路线
        path.reset();

        //画上面的
        for (int i = 0; i < chartNumber; i++) {

            //先画圆
            float x = eachWidth/2 + eachWidth * i;
            float y = (chartHeight - padding)/2 - (Float.parseFloat(highTemperatureList.get(i)) - minHigh) * perHeightTop;
            canvas.drawCircle(x, y, pointRaidus, pointPaint);   //几个参数分别为横坐标，纵坐标，半径，画笔


            //接下来画直线
            if (0 == i) {
                path.moveTo(x, y);  //移动到该点，该点为下一次操作的起始点
            } else {
                path.lineTo(x, y);  //在上一个点和该点之间添加直线
            }

            //接下来写上温度
            String temperature = highTemperatureList.get(i) + "℃";
            //如果是昨天的天气，那么文字为灰色，其他的文字为黑色
            if (0 == i) {
                labelPaint.setColor(Color.GRAY);
                canvas.drawText(temperature, x - labelPaint.measureText(temperature)/2, y - 4 * pointRaidus, labelPaint);
                labelPaint.setColor(Color.BLACK);
            } else {
                canvas.drawText(temperature, x - labelPaint.measureText(temperature)/2, y - 4 * pointRaidus, labelPaint);
            }
        }
        //画完整的直线图
        canvas.drawPath(path, linePaint);

        //重置路线
        path.reset();

        //画下面的
        for (int i = 0; i < chartNumber; i++) {

            //先画圆
            float x = eachWidth/2 + eachWidth * i;
            float y = chartHeight - padding/2 - (Float.parseFloat(lowTemperatureList.get(i)) - minLow) * perHeightBottom;
            canvas.drawCircle(x, y, pointRaidus, pointPaint);   //几个参数分别为横坐标，纵坐标，半径，画笔

            //接下来画直线
            if (0 == i) {
                path.moveTo(x, y);  //移动到该点，该点为下一次操作的起始点
            } else {
                path.lineTo(x, y);  //在上一个点和该点之间添加直线
            }

            //接下来写上温度
            String temperature = lowTemperatureList.get(i) + "℃";
            //如果是昨天的天气，那么文字为灰色，其他的文字为黑色
            if (0 == i) {
                labelPaint.setColor(Color.GRAY);
                canvas.drawText(temperature, x - labelPaint.measureText(temperature)/2, y + 8 * pointRaidus, labelPaint);
                labelPaint.setColor(Color.BLACK);
            } else {
                canvas.drawText(temperature, x - labelPaint.measureText(temperature)/2, y + 8 * pointRaidus, labelPaint);
            }
        }
        //画完整的直线图
        canvas.drawPath(path, linePaint);
    }
}
