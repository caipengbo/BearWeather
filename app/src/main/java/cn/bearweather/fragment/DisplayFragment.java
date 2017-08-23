package cn.bearweather.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Random;

import cn.bearweather.MainActivity;
import cn.bearweather.R;
import cn.bearweather.bean.weatherbean.Weather;


public class DisplayFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeLayout;

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
    private String currentCityCode;
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
        // 获取天气控件
        //天气控件
        mNowDistrict = (Button) view.findViewById(R.id.district_text_button);
        mShareButton = (Button) view.findViewById(R.id.share_button);
        mNowTemperature = (TextView)view.findViewById(R.id.temp_text);
        mNowCondition = (Button) view.findViewById(R.id.cond_text_button);
        mNowWind= (TextView)view.findViewById(R.id.wind_text);
        mNowHumidy= (TextView)view.findViewById(R.id.hum_text);
        mNowPressure= (TextView)view.findViewById(R.id.press_text);
        mNowAqi= (Button)view.findViewById(R.id.air_text_button);

        currentCityCode = getArguments().getString("cityCode");
        // 网络访问天气
        getWeatherFromWeb(currentCityCode);
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

    @Override
    public void onRefresh() {
        getWeatherFromWeb(currentCityCode);
        mSwipeLayout.setRefreshing(false);
        Toast.makeText(getContext(), "天气已更新", Toast.LENGTH_SHORT).show();
    }

}
