package cn.bearweather.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.bearweather.bean.weatherbean.DailyForecast;
import cn.bearweather.bean.weatherbean.HourlyForecast;
import cn.sharesdk.onekeyshare.OnekeyShare;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bearweather.MainActivity;
import cn.bearweather.R;
import cn.bearweather.bean.weatherbean.Weather;


public class DisplayFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout mSwipeLayout;
    // 透明背景
    private RelativeLayout fiveHoursInformationRelativeLayout;
    private RelativeLayout threeDaysInformationRelativeLayout;
    private RelativeLayout livingIndexInformationRelativeLayout;
    // 切换背景
    private ScrollView backgroundScrollView;//最外层 背景切换专用
    //按钮
    private Button btnOperateCity;//添加 删除 修改城市
    private Button btnShare;//分享
    //private Button btnNightPattern;//夜间模式
    private Button btnWeatherInfo;//天气百科
    //显示
    private TextView txtNowTemperature;//实时温度
    private TextView txtNowWind;//实时风力
    private TextView txtNowWet;//实时相对湿度
    private TextView txtNowAirPress;//实时气压

    private TextView txtFirstHourTime;//第一小时
    private TextView txtFirstHourTemperature;//第一小时温度
    private TextView txtSecondHourTime;//第二小时
    private TextView txtSecondHourTemperature;//第二小时温度
    private TextView txtThirdHourTime;//第三小时
    private TextView txtThirdHourTemperature;//第三小时温度
    private TextView txtFourthHourTime;//第四小时
    private TextView txtFourthHourTemperature;//第四小时温度
    private TextView txtFifthtHourTime;//第五小时
    private TextView txtFifthHourTemperature;//第五小时温度


    private TextView txtFirstDayMaxTemperature;//第一天最高温
    private TextView txtFirstDayMinTemperature;//第一天最低温

    private TextView txtSecondDayMaxTemperature;//第二天最高温
    private TextView txtSecondDayMinTemperature;//第二天最低温

    private TextView txtThirdDayMaxTemperature;//第三天最高温
    private TextView txtThirdDayMinTemperature;//第三天最低温

    private TextView txtTravelIndex;//旅游指数
    private TextView txtFluIndex;//感冒指数
    private TextView txtSportIndex;//运动指数
    private TextView txtComfortIndex;//舒适度
    private TextView txtWearIndex;//穿衣指数
    private TextView txtUVIndex;//紫外线指数
    // 与Activity交互使用
    private String currentCityCode = null;
    private DisplayFragment.OnFragmentInteractionListener mListener;


    public DisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_display, container, false);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);

        // 天气控件实例化
        btnOperateCity = (Button) view.findViewById(R.id.city_button);
        btnShare = (Button) view.findViewById(R.id.share_button);
        btnShare.setOnClickListener(this); // 分享
        //btnNightPattern = (Button) view.findViewById(R.id.night_pattern_button);
        btnWeatherInfo = (Button) view.findViewById(R.id.weather_button);
        btnWeatherInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String buttonText = btnWeatherInfo.getText().toString();

                Uri uri=Uri.parse("https://baike.baidu.com/item/"+buttonText);

                Intent intent=new Intent(Intent.ACTION_VIEW,uri);

                startActivity(intent);

            }
        });

        txtNowTemperature = (TextView) view.findViewById(R.id.now_temperature_text);
        txtNowWind = (TextView) view.findViewById(R.id.wind_show_text);
        txtNowWet = (TextView) view.findViewById(R.id.wet_show_text);
        txtNowAirPress = (TextView) view.findViewById(R.id.airpress_show_text);

        txtFirstHourTime = (TextView) view.findViewById(R.id.firsthour_time_text);
        txtFirstHourTemperature = (TextView) view.findViewById(R.id.firsthour_temperature_text);
        txtSecondHourTime = (TextView) view.findViewById(R.id.secondhour_time_text);
        txtSecondHourTemperature = (TextView) view.findViewById(R.id.secondhour_temperature_text);
        txtThirdHourTime = (TextView) view.findViewById(R.id.thirdhour_time_text);
        txtThirdHourTemperature = (TextView) view.findViewById(R.id.thirdhour_temperature_text);
        txtFourthHourTime = (TextView) view.findViewById(R.id.fourthhour_time_text);
        txtFourthHourTemperature = (TextView) view.findViewById(R.id.fourthhour_temperature_text);
        txtFifthtHourTime = (TextView) view.findViewById(R.id.fifthhour_time_text);
        txtFifthHourTemperature = (TextView) view.findViewById(R.id.fifthhour_temperature_text);


        txtFirstDayMaxTemperature = (TextView) view.findViewById(R.id.firstday_maxtemperature_text);
        txtFirstDayMinTemperature = (TextView) view.findViewById(R.id.firstday_mintemperature_text);

        txtSecondDayMaxTemperature = (TextView) view.findViewById(R.id.secondday_maxtemperature_text);
        txtSecondDayMinTemperature = (TextView) view.findViewById(R.id.secondday_mintemperature_text);

        txtThirdDayMaxTemperature = (TextView) view.findViewById(R.id.thirdday_maxtemperature_text);
        txtThirdDayMinTemperature = (TextView) view.findViewById(R.id.thirdday_mintemperature_text);

        txtTravelIndex = (TextView) view.findViewById(R.id.travel_index_text);
        txtFluIndex = (TextView) view.findViewById(R.id.flu_index_text);
        txtSportIndex = (TextView) view.findViewById(R.id.sport_index_text);
        txtComfortIndex = (TextView) view.findViewById(R.id.comfort_index_text);
        txtWearIndex = (TextView) view.findViewById(R.id.wear_index_text);
        txtUVIndex = (TextView) view.findViewById(R.id.uv_index_text);


        //设置透明背景
        fiveHoursInformationRelativeLayout = (RelativeLayout) view.findViewById(R.id.fivehours_information_relativelayout);
        fiveHoursInformationRelativeLayout.getBackground().setAlpha(100);
        threeDaysInformationRelativeLayout = (RelativeLayout) view.findViewById(R.id.threedays_information_relativelayout);
        threeDaysInformationRelativeLayout.getBackground().setAlpha(100);
        livingIndexInformationRelativeLayout = (RelativeLayout) view.findViewById(R.id.livingindex_information_relativelayout);
        livingIndexInformationRelativeLayout.getBackground().setAlpha(100);

        //时间
        backgroundScrollView = (ScrollView) view.findViewById(R.id.background_scrollview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            /*设置未来五小时时间*/
//            txtFirstHourTime.setText((hour + 1) + ":00");
//            txtSecondHourTime.setText((hour + 2) + ":00");
//            txtThirdHourTime.setText((hour + 3) + ":00");
//            txtFourthHourTime.setText((hour + 4) + ":00");
//            txtFifthtHourTime.setText((hour + 5) + ":00");

            /*根据时间设置背景*/
            if(hour > 5 && hour <= 13){
                backgroundScrollView.setBackgroundResource(R.drawable.morning4);
            } else if(hour > 13 && hour <= 18){
                backgroundScrollView.setBackgroundResource(R.drawable.noon4);
            } else {
                backgroundScrollView.setBackgroundResource(R.drawable.night5);
            }
        }

        // 天气显示控件结束
        currentCityCode = getArguments().getString("cityCode");
        // 网络访问天气
        if (currentCityCode != null) {
            getWeatherFromWeb(currentCityCode);
        } else {
            double longitude = getArguments().getDouble("longitude");
            double latitude = getArguments().getDouble("latitude");
            if (longitude != 0.0 && latitude != 0.0) {
                getWeatherFromWeb(longitude, latitude);
            }
        }

        //曲线图
        LineChart fiveHoursChart = (LineChart) view.findViewById(R.id.fivehours_chart);
        LineChart threeDaysMaxTempChart = (LineChart) view.findViewById(R.id.threedays_maxtemperature_chart);
        LineChart threeDaysMinTempChart = (LineChart) view.findViewById(R.id.threedays_mintemperaure_chart);


        // 制作7个数据点（沿x坐标轴）
        LineData mFiveHoursLineData = makeLineData(5);
        setChartStyle(fiveHoursChart, mFiveHoursLineData, Color.TRANSPARENT);

        LineData mThreeDaysMaxTempLineData = makeLineData(3);
        setChartStyle(threeDaysMaxTempChart, mThreeDaysMaxTempLineData, Color.TRANSPARENT);

        LineData mThreeDaysMinTempLineData = makeLineData(3);
        setChartStyle(threeDaysMinTempChart, mThreeDaysMinTempLineData, Color.TRANSPARENT);

        return view;
    }



    // 设置曲线 开始
// 设置chart显示的样式
    private void setChartStyle(LineChart mLineChart, LineData lineData,
                               int color) {
        // 是否在折线图上添加边框
        // mLineChart.setDrawBorders(false);

        mLineChart.setDescription("");// 数据描述

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        // mLineChart
        //       .setNoDataTextDescription("如果传给MPAndroidChart的数据为空，那么你将看到这段文字。@Zhang Phil");

        // 是否绘制背景颜色。
        // 如果mLineChart.setDrawGridBackground(false)，
        // 那么mLineChart.setGridBackgroundColor(Color.CYAN)将失效;
        mLineChart.setDrawGridBackground(true);
        //mLineChart.setGridBackgroundColor(Color.CYAN);
        mLineChart.setGridBackgroundColor(Color.TRANSPARENT);

        // 触摸
        mLineChart.setTouchEnabled(true);

        // 拖拽
        mLineChart.setDragEnabled(true);

        // 缩放
        mLineChart.setScaleEnabled(true);

        mLineChart.setPinchZoom(false);
        // 隐藏右边 的坐标轴
        mLineChart.getAxisRight().setEnabled(false);
        // 让x轴在下面
        //mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // // 隐藏左边坐标轴横网格线
        mLineChart.getAxisLeft().setDrawGridLines(false);
        // // 隐藏右边坐标轴横网格线
        mLineChart.getAxisRight().setDrawGridLines(false);
        // // 隐藏X轴竖网格线
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴(true不隐藏)
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // 让x轴在下面
        // 设置背景
        //mLineChart.setBackgroundColor(color);

        // 设置x,y轴的数据
        mLineChart.setData(lineData);

        // 设置比例图标示，就是那个一组y的value的
        Legend mLegend = mLineChart.getLegend();
//
//        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLegend.setForm(Legend.LegendForm.LINE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.TRANSPARENT);// 颜色

        // 沿x轴动画，时间2000毫秒。
        mLineChart.animateX(2000);
    }

    /**
     * @param count 数据点的数量。
     * @return
     */
    private LineData makeLineData(int count) {
        ArrayList<String> x = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据
            x.add("x:" + i);
        }

        // y轴的数据
        ArrayList<Entry> y = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * 100);
            Entry entry = new Entry(val, i);
            y.add(entry);
        }

        // y轴数据集
        LineDataSet mLineDataSet = new LineDataSet(y, null);

        // 用y轴的集合来设置参数
        // 线宽
        mLineDataSet.setLineWidth(3.0f);

        // 显示的圆形大小
        mLineDataSet.setCircleSize(5.0f);

        // 折线的颜色
        mLineDataSet.setColor(Color.WHITE);

        // 圆球的颜色
        mLineDataSet.setCircleColor(Color.BLUE);

        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
        // Highlight的十字交叉的纵横线将不会显示，
        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(true);

        // 按击后，十字交叉线的颜色
        mLineDataSet.setHighLightColor(Color.CYAN);

        // 设置这项上显示的数据点的字体大小。
        mLineDataSet.setValueTextSize(10.0f);

        // mLineDataSet.setDrawCircleHole(true);

        // 改变折线样式，用曲线。
        // mLineDataSet.setDrawCubic(true);
        // 默认是直线
        // 曲线的平滑度，值越大越平滑。
        // mLineDataSet.setCubicIntensity(0.2f);

        // 填充曲线下方的区域，红色，半透明。
        // mLineDataSet.setDrawFilled(true);
        // mLineDataSet.setFillAlpha(128);
        // mLineDataSet.setFillColor(Color.RED);

        // 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
        // mLineDataSet.setCircleColorHole(Color.YELLOW);

        // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
        mLineDataSet.setValueFormatter(new ValueFormatter() {

//          @Override
//          public String getFormattedValue(float value) {
//              int n = (int) value;
//              String s = "y:" + n;
//              return s;
//          }

            @Override
            public String getFormattedValue(float value, Entry entry,
                                            int dataSetIndex, ViewPortHandler viewPortHandler) {
                // TODO Auto-generated method stub
                int n = (int) value;
                String s = "y:" + n;
                return s;
            }
        });

        ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();
        mLineDataSets.add(mLineDataSet);

        LineData mLineData = new LineData(x, mLineDataSets);

        return mLineData;
    }

    // 设置曲线相关方法 结束

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DisplayFragment.OnFragmentInteractionListener) {
            mListener = (DisplayFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    // 与Activity交互的接口
    public interface OnFragmentInteractionListener {
        // 回调函数
        void updateDrawerItem(int position, Weather weather);
    }

    // 提取未来五个小时预报 串的后部分
    private String processHour(String time) {
        String[] res = time.split(" ");
        return res[1];
    }
    private void updateDisplay(Weather weather) {

        //显示
        btnOperateCity.setText("＋ " + weather.getBasic().getCity());
        txtNowTemperature.setText(weather.getNow().getTmp()+"℃");//实时温度
        txtNowWind.setText(weather.getNow().getWind().getSc());//实时风力
        txtNowWet.setText(weather.getNow().getHum() + "%");//实时相对湿度
        txtNowAirPress.setText(weather.getNow().getPres() + "hpa");//实时气压
        List<HourlyForecast> hourlyForecasts = weather.getHourlyForecastList();
        int size = hourlyForecasts.size();
        if (size > 0) {
            txtFirstHourTime.setText(processHour(hourlyForecasts.get(0).getDate()));//第一小时
            txtFirstHourTemperature.setText(hourlyForecasts.get(0).getTmp());
        }
        if (size > 1) {
            txtSecondHourTime.setText(processHour(hourlyForecasts.get(1).getDate()));//第二小时
            txtSecondHourTemperature.setText(hourlyForecasts.get(1).getTmp());//第二小时温度
        }
        if (size > 2) {
            txtThirdHourTime.setText(processHour(hourlyForecasts.get(2).getDate()));//第三小时
            txtThirdHourTemperature.setText(hourlyForecasts.get(2).getTmp());//第三小时温度
        }

        if (size > 3) {
            txtFourthHourTime.setText(processHour(hourlyForecasts.get(3).getDate()));//第四小时
            txtFourthHourTemperature.setText(hourlyForecasts.get(3).getTmp());//第四小时温度
        }

        if (size > 4) {
            txtFifthtHourTime.setText(processHour(hourlyForecasts.get(4).getDate()));//第五小时
            txtFifthHourTemperature.setText(hourlyForecasts.get(4).getTmp());;//第五小时温度
        }


        List<DailyForecast> dailyForecasts = weather.getDailyForecastList();


        txtFirstDayMaxTemperature.setText(dailyForecasts.get(0).getTmp().getMax());//第一天最高温
        txtFirstDayMinTemperature.setText(dailyForecasts.get(0).getTmp().getMin());//第一天最低温

        txtSecondDayMaxTemperature.setText(dailyForecasts.get(1).getTmp().getMax());//第二天最高温
        txtSecondDayMinTemperature.setText(dailyForecasts.get(1).getTmp().getMin());//第二天最低温

        txtThirdDayMaxTemperature.setText(dailyForecasts.get(2).getTmp().getMax());;//第三天最高温
        txtThirdDayMinTemperature.setText(dailyForecasts.get(2).getTmp().getMin());;//第三天最低温

        txtTravelIndex.setText(weather.getSuggestion().getTrav().getBrf());//旅游指数
        txtFluIndex.setText(weather.getSuggestion().getFlu().getBrf());//感冒指数
        txtSportIndex.setText(weather.getSuggestion().getSport().getBrf());//运动指数
        txtComfortIndex.setText(weather.getSuggestion().getComf().getBrf());//舒适度
        txtWearIndex.setText(weather.getSuggestion().getDrsg().getBrf());//穿衣指数
        txtUVIndex.setText(weather.getSuggestion().getUv().getBrf());//紫外线指数

        mListener.updateDrawerItem(Integer.parseInt(getArguments().getString("position")), weather);
    }
    private void getWeatherFromWeb(String cityCode) {
        // 网络查询天气
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://free-api.heweather.com/v5/weather?city=" + cityCode+ "&key=21897700b37945ea82e7cb9716ae2a6f";
        Log.d("", "getWeatherFromWeb：网络查询天气网络查询获得的");
        // 构建Volley请求
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) { //成功
                        // 解析json
                        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("HeWeather5");
                        String weatherContent = jsonArray.get(0).toString();
                        Weather weather = new Gson().fromJson(weatherContent, Weather.class);
                        // 获取天气对象信息，更新控件
                        updateDisplay(weather);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), "更新天气失败", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }


    private void getWeatherFromWeb(double longitude, double latitude) {
        // 网络查询天气
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String param = longitude + "," + latitude;
        String url = "https://free-api.heweather.com/v5/weather?city=" + param+ "&key=21897700b37945ea82e7cb9716ae2a6f";
        Log.d("", "getWeatherFromWeb：网络查询天气网络查询获得的");
        // 构建Volley请求
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) { //成功
                        // 解析json
                        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("HeWeather5");
                        String weatherContent = jsonArray.get(0).toString();
                        Weather weather = new Gson().fromJson(weatherContent, Weather.class);
                        // 获取天气对象信息，更新控件
                        updateDisplay(weather);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), "更新天气失败", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {
        // 网络访问天气
        if (currentCityCode != null) {
            getWeatherFromWeb(currentCityCode);
        } else {
            double longitude = getArguments().getDouble("longitude");
            double latitude = getArguments().getDouble("latitude");

            if (longitude != 0.0 && latitude != 0.0) {
                getWeatherFromWeb(longitude, latitude);
            }
        }

        mSwipeLayout.setRefreshing(false);
        Toast.makeText(getContext(), "天气已更新", Toast.LENGTH_SHORT).show();
    }
    // 分享功能
    @Override
    public void onClick(View v) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("贝尔天气");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://caipengbo.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这是一个小App，欢迎下载哦");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://caipengbo.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://caipengbo.cn");

        // 启动分享GUI
        oks.show(getContext());
    }

}
