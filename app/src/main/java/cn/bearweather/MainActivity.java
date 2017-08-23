package cn.bearweather;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bearweather.activity.SelectAreaActivity;
import cn.bearweather.activity.WelcomeActivity;
import cn.bearweather.adapter.WeatherAdapter;
import cn.bearweather.bean.weatherbean.Basic;
import cn.bearweather.bean.weatherbean.Now;
import cn.bearweather.bean.weatherbean.Weather;
import cn.bearweather.fragment.DisplayFragment;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DisplayFragment.OnFragmentInteractionListener, AdapterView.OnItemLongClickListener {

    private LocationManager locationManager;
    private String provider;

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

//        openGPSSettings();
//        Location location = getLocation();
//
//        double longitude = location.getLongitude(); // 经度
//        double latitude = location.getLatitude(); // 维度

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


    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
    }

    private Location getLocation() {
        Location location = null;
        // 获取位置管理服务
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("", "getLocation: 未授权-----");
            // 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            /*返回值：
            如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
            如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
            如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
            弹窗需要解释为何需要该权限，再次请求授权*/
                Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();
                // 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                // 不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // 获取location对象
            location = getBestLocation(locationManager);
            // 通过GPS获取位置
            updateToNewLocation(location);
            locationManager.requestLocationUpdates(provider, 100 * 1000, 500, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e("", "纬度：" + location.getLatitude());
                    Log.e("", "经度：" + location.getLongitude());
                    Log.e("", "海拔：" + location.getAltitude());
                    Log.e("", "时间：" + location.getTime());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        return location;
    }

    private void updateToNewLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Toast.makeText(MainActivity.this, "维度：" + latitude + " 经度" + longitude, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "无法获取地理信息", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
                        updateToNewLocation(location);
                        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
                        locationManager.requestLocationUpdates(provider, 100 * 1000, 500, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.e("", "纬度：" + location.getLatitude());
                                Log.e("", "经度：" + location.getLongitude());
                                Log.e("", "海拔：" + location.getAltitude());
                                Log.e("", "时间：" + location.getTime());
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    }
                }
                break;
            }
        }
    }

    private Location getBestLocation(LocationManager locationManager) {
        Location result = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            result = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (result != null) {
                return result;
            } else {
                result = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                return result;
            }
        }
        return result;
    }

}
