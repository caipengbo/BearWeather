package cn.bearweather;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import butterknife.Bind;
import cn.bearweather.activity.SelectAreaActivity;
import cn.bearweather.bean.weatherbean.Weather;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Button mSelectButton;
    private TextView mShowText;
    private SwipeRefreshLayout mSwipeLayout;

    //天气控件

    private TextView mNowTemperature;

    private TextView mNowWind;

    private TextView mNowHumidy;

    private TextView mNowPressure;

    private TextView mNowAqi;
    private String currentCityCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);
        mShowText = (TextView) findViewById(R.id.show_text);
        mSelectButton = (Button) findViewById(R.id.select_button);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectAreaActivity.class);
                startActivityForResult(intent,100);
            }
        });
        // 获取天气控件
        //天气控件
        mNowTemperature = (TextView)findViewById(R.id.now_temparature_text);
        mNowWind= (TextView)findViewById(R.id.now_wind_text);
        mNowHumidy= (TextView)findViewById(R.id.now_hum_text);
        mNowPressure= (TextView)findViewById(R.id.now_press_text);
        mNowAqi= (TextView)findViewById(R.id.now_aqi_text);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String cityName = data.getStringExtra("cityName");
            String cityCode = data.getStringExtra("cityCode");
            mShowText.setText(cityName);
            currentCityCode = cityCode;
            // 网络访问天气
            getWeatherFromWeb(cityCode);
        }

    }

    private void updateDisplay(Weather weather) {
        mNowTemperature.setText(weather.getNow().getTmp() + " ℃");
        mNowWind.setText(weather.getNow().getWind().getSc());
        mNowHumidy.setText(weather.getNow().getHum());
        mNowPressure.setText(weather.getNow().getPres());
        if (weather.getAqi() != null) {
            mNowAqi.setText(weather.getAqi().getCity().getQlty());
        } else {
            mNowAqi.setText("无数据");
        }
    }

    private void getWeatherFromWeb(String cityCode) {
        // 网络查询天气
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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

                        Toast.makeText(MainActivity.this, "更新天气失败", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);

    }


    @Override
    public void onRefresh() {
        getWeatherFromWeb(currentCityCode);
        mSwipeLayout.setRefreshing(false);
    }
}
