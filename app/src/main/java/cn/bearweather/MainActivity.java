package cn.bearweather;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


import cn.bearweather.activity.SelectAreaActivity;

import cn.bearweather.adapter.WeatherAdapter;
import cn.bearweather.bean.dbbean.Item;
import cn.bearweather.bean.weatherbean.Basic;
import cn.bearweather.bean.weatherbean.Now;
import cn.bearweather.bean.weatherbean.Weather;
import cn.bearweather.fragment.DisplayFragment;
import cn.bearweather.widget.UpdateWidgetService;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DisplayFragment.OnFragmentInteractionListener, AdapterView.OnItemLongClickListener {

    private Button mSelectButton;

    LinearLayout mDrawerLinearLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mCityListView;
    private List<Weather> dataList;
    private WeatherAdapter weatherAdapter;
    int longClickPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_linearlayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCityListView = (ListView) findViewById(R.id.city_listview);
        dataList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, dataList);
        mCityListView.setAdapter(weatherAdapter);

        mCityListView.setOnItemClickListener(this);
        mCityListView.setOnItemLongClickListener(this);
        mCityListView.setOnCreateContextMenuListener(this);
        mSelectButton = (Button) findViewById(R.id.select_button);

        // TODO 初次打开，定位当前位置
        createFragment(123.433, 41.7017);

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectAreaActivity.class);
                startActivityForResult(intent, 100);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String cityName = data.getStringExtra("cityName");
            String cityCode = data.getStringExtra("cityCode");
            // 将选择的数据插入datalist
            Weather weather = new Weather();
            weather.setBasic(new Basic());
            weather.setNow(new Now());
            weather.getBasic().setId(cityCode);
            dataList.add(weather);
            createFragment(dataList.size() - 1);
        }

    }

    // 根据drawer listview 中的item 创建(更新) fragment
    private void createFragment(int dataListIndex) {
        DisplayFragment displayFragment = new DisplayFragment();
        Bundle args = new Bundle();
        // 将cityCode 发送给displayfragment
        args.putString("position", String.valueOf(dataListIndex));
        args.putString("cityCode", dataList.get(dataListIndex).getBasic().getId());
        displayFragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.display_framelayout, displayFragment).commit();
        mDrawerLayout.closeDrawer(mDrawerLinearLayout);
    }

    // 根据经纬度 创建(更新) fragment ， 用来更新drawer listview 的第一条item
    private void createFragment(double longitude, double latitude) {
        DisplayFragment displayFragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putString("position", String.valueOf(0)); // 定位城市 是 item的第一条
        args.putDouble("longitude", longitude);
        args.putDouble("latitude", latitude);
        displayFragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.display_framelayout, displayFragment).commit();
        mDrawerLayout.closeDrawer(mDrawerLinearLayout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        createFragment(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        longClickPosition = position;
        Log.d("", "onItemLongClick: " + longClickPosition);
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: {
                // Toast.makeText(MainActivity.this, "删除" + longClickPosition, Toast.LENGTH_LONG).show();
                if (longClickPosition != 0) {
                    Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    dataList.remove(longClickPosition);
                    weatherAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "定位城市无法删除", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: {
                break;
            }
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void updateDrawerItem(int position, Weather weather) {
        Weather weather1;
        int size = dataList.size();
        if (size == 0) {
            dataList.add(weather);
            // 在dataList中插入一条item之后，就从数据库中查询并原来保存的信息
            for (Item item : DataSupport.findAll(Item.class)) {
                Weather w = new Weather();
                Basic b = new Basic();
                b.setId(item.getCode());
                b.setCity(item.getName());
                w.setBasic(b);
                Now n = new Now();
                n.setTmp(item.getTemp());
                Now.CondBean cond = new Now.CondBean();
                cond.setCode(item.getIcon());
                n.setCond(cond);
                w.setNow(n);
                Log.d("", "读取数据库:" + w);
                // 与widget 服务交互
                dataList.add(w);
                // 与Widget交互的服务
                Intent intent = new Intent(MainActivity.this, UpdateWidgetService.class);
                intent.putExtra("city", dataList.get(0).getBasic().getCity()); //
                intent.putExtra("tmp", dataList.get(0).getNow().getTmp()); //
                startService(intent);


            }
        } else if (size <= position) {
            Toast.makeText(MainActivity.this, "该城市已从列表删除，无法更新", Toast.LENGTH_SHORT).show();
            return;
        } else {
            weather1 = dataList.get(position);
            weather1.setBasic(weather.getBasic());
            weather1.setNow(weather.getNow());
        }
        weatherAdapter.notifyDataSetChanged();
    }

    // 每次退出的时候，都清空原来的数据库内容
    // 将收藏列表[从下标1(如果有)开始,因为第一个是定位的城市，是更新和改变的] 保存在数据库之中(更新数据库)
    @Override
    protected void onDestroy() {
        // 清空原数据库内容
        DataSupport.deleteAll(Item.class);
        // 更新
        int size = dataList.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                Item item = new Item();
                item.setCode(dataList.get(i).getBasic().getId());
                item.setIcon(dataList.get(i).getNow().getCond().getCode());
                item.setTemp(dataList.get(i).getNow().getTmp());
                item.setName(dataList.get(i).getBasic().getCity());
                Log.d("", "存数据库--------: " + item);
                item.save();
            }
        }
        super.onDestroy();
    }
}
