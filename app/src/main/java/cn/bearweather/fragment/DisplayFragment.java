package cn.bearweather.fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cn.sharesdk.onekeyshare.OnekeyShare;

import java.util.Random;

import cn.bearweather.MainActivity;
import cn.bearweather.R;
import cn.bearweather.bean.weatherbean.Weather;


public class DisplayFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout mSwipeLayout;
    // 根据时间改变的背景
    private LinearLayout first_show_layout;
    private LinearLayout threehours_ll;
    private LinearLayout threedays_ll;
    private LinearLayout life_ll;

    // 透明的背景
    private Button test_button;
    private LinearLayout threedays_weather_ll;
    private LinearLayout threehours_weather_ll;
    private LinearLayout life1_ll;
    private LinearLayout life2_ll;
    //天气显示 相关控件
    private Button mNowDistrict; //（+ 地区）
    private Button mShareButton;
    private TextView mNowTemperature;
    private Button mNowCondition;  // 天气状况(含百科功能)

    private TextView mNowWind;
    private TextView mNowHumidy;
    private TextView mNowPressure; // (hpa)

    private Button mNowAqi;     //   空气质量/n 指数
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
        // 背景
        first_show_layout = (LinearLayout) view.findViewById(R.id.first_show_layout);
        threehours_ll = (LinearLayout) view.findViewById(R.id.threehours_ll);
        threedays_ll = (LinearLayout) view.findViewById(R.id.threedays_ll);
        life_ll = (LinearLayout)  view.findViewById(R.id.life_ll);


        //天气控件
        mNowDistrict = (Button) view.findViewById(R.id.district_text_button);
        mShareButton = (Button) view.findViewById(R.id.share_button);
        mShareButton.setOnClickListener(this);
        mNowTemperature = (TextView)view.findViewById(R.id.temp_text);
        mNowCondition = (Button) view.findViewById(R.id.cond_text_button);
        mNowCondition.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String buttonText = mNowCondition.getText().toString();

                Uri uri=Uri.parse("https://baike.baidu.com/item/"+buttonText);

                Intent intent=new Intent(Intent.ACTION_VIEW,uri);

                startActivity(intent);

            }
        });
        mNowWind= (TextView)view.findViewById(R.id.wind_text);
        mNowHumidy= (TextView)view.findViewById(R.id.hum_text);
        mNowPressure= (TextView)view.findViewById(R.id.press_text);
        mNowAqi= (Button)view.findViewById(R.id.air_text_button);

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

        //设置背景透明
        threedays_weather_ll = (LinearLayout) view.findViewById(R.id.threedays_weather_ll);
        threehours_weather_ll = (LinearLayout) view.findViewById(R.id.threehours_weather_ll);
        life1_ll = (LinearLayout) view.findViewById(R.id.life1_ll);
        life2_ll = (LinearLayout) view.findViewById(R.id.life2_ll);
        threedays_weather_ll.getBackground().setAlpha(150);
        threehours_weather_ll.getBackground().setAlpha(150);
        life1_ll.getBackground().setAlpha(150);
        life2_ll.getBackground().setAlpha(150);

        // 根据时间修改背景
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour > 5 && hour <= 13) {
                first_show_layout.setBackgroundResource(R.drawable.morning4);
                threehours_ll.setBackgroundResource(R.drawable.morning4);
                threedays_ll.setBackgroundResource(R.drawable.morning4);
                life_ll.setBackgroundResource(R.drawable.morning4);
            } else if (hour > 13 && hour <= 19) {
                first_show_layout.setBackgroundResource(R.drawable.noon4);
                threehours_ll.setBackgroundResource(R.drawable.noon4);
                threedays_ll.setBackgroundResource(R.drawable.noon4);
                life_ll.setBackgroundResource(R.drawable.noon4);
            } else {
                first_show_layout.setBackgroundResource(R.drawable.night5);
                threehours_ll.setBackgroundResource(R.drawable.night5);
                threedays_ll.setBackgroundResource(R.drawable.night5);
                life_ll.setBackgroundResource(R.drawable.night5);
            }
        }

        return view;
    }
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

    private void updateDisplay(Weather weather) {
        mNowDistrict.setText("＋ " + weather.getBasic().getCity());
        mNowTemperature.setText(weather.getNow().getTmp() + "℃");
        mNowCondition.setText(weather.getNow().getCond().getTxt());
        mNowWind.setText(weather.getNow().getWind().getSc());
        mNowHumidy.setText(weather.getNow().getHum() + "%");
        mNowPressure.setText(weather.getNow().getPres() + "hpa");

        if (weather.getAqi() != null) {
            mNowAqi.setText(weather.getAqi().getCity().getQlty() + "\n" + weather.getAqi().getCity().getAqi());
        }
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
