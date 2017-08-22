package cn.bearweather;


import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bearweather.activity.SelectAreaActivity;
import cn.bearweather.adapter.WeatherAdapter;
import cn.bearweather.bean.weatherbean.Basic;
import cn.bearweather.bean.weatherbean.Now;
import cn.bearweather.bean.weatherbean.Weather;
import cn.bearweather.fragment.DisplayFragment;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DisplayFragment.OnFragmentInteractionListener {

    private Button mSelectButton;
    private TextView mShowText;

    LinearLayout mDrawerLinearLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mCityListView;
    private List<Weather> dataList;
    private WeatherAdapter weatherAdapter;

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
        mCityListView.setOnCreateContextMenuListener(this);

        mShowText = (TextView) findViewById(R.id.show_text);
        mSelectButton = (Button) findViewById(R.id.select_button);

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectAreaActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String cityName = data.getStringExtra("cityName");
            String cityCode = data.getStringExtra("cityCode");
            mShowText.setText(cityName);
            // 将选择的数据插入datalist
            Weather weather = new Weather();
            weather.setBasic(new Basic());
            weather.setNow(new Now());
            weather.getBasic().setId(cityCode);
            dataList.add(weather);
            createFragment(dataList.size()-1);
        }

    }
    //
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "删除" );
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //info.id获得listview中点击的哪一项  下标从0开始
        int position=(int)info.id;
        switch (item.getItemId()) {
            case 1: {
                // Toast.makeText(MainActivity.this, "删除" + position, Toast.LENGTH_LONG).show();
                dataList.remove(position);
                weatherAdapter.notifyDataSetChanged();
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
        Weather weather1 = dataList.get(position);
        weather1.setBasic(weather.getBasic());
        weather1.setNow(weather.getNow());
        weatherAdapter.notifyDataSetChanged();
    }
}
