package cn.bearweather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.bearweather.R;
import cn.bearweather.bean.areabean.City;
import cn.bearweather.bean.areabean.District;
import cn.bearweather.bean.areabean.Province;


/**
 * A simple {@link Fragment} subclass.
 * 获取网络数据， 显示数据
 */
public class SelectAreaFragment extends Fragment implements AdapterView.OnItemClickListener {
    // 调试专用Tag
    private static final String TAG = "SelectAreaFragment";

    private Button mBackButton;
    private TextView mTitleText;
    private ProgressBar mSelectAreaProgressBar;
    private ListView mAreaListView;
    private ArrayAdapter<String> adapter;
    List<String> areaDataList = new ArrayList<>();
    // SelectActivity 控制Fragment 创建  mode 1->创建省  2 -> 市  3->地区
    private int level;
    private static final int PROVINCE_LEVEL = 1;
    private static final int CITY_LEVEL = 2;
    private static final int DISTRICT_LEVEL = 3;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<District> districtList;
    // 上一级区域
    private Province selectedProvince;
    private City selectedCity;
    //访问网络
    private RequestQueue requestQueue;
    private String baseUrl = "http://caipengbo.cn/api/china.json";


    private OnFragmentInteractionListener mListener;
    public SelectAreaFragment() {
        this.level = PROVINCE_LEVEL;
    }

    public SelectAreaFragment(int level, Province selectedProvince, City selectedCity, List<Province> provinceList, List<City> cityList, List<District> districtList) {
        this.level = level;
        this.selectedProvince = selectedProvince;
        this.selectedCity = selectedCity;
        this.provinceList = provinceList;
        this.cityList = cityList;
        this.districtList = districtList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_select_district, container, false);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                mListener.changeLevel(level - 1);
            }
        });
        mTitleText = (TextView)view.findViewById(R.id.title_text);
        mAreaListView = (ListView) view.findViewById(R.id.area_listview);
        mSelectAreaProgressBar = (ProgressBar) view.findViewById(R.id.select_area_progressbar);
        mSelectAreaProgressBar.setVisibility(View.GONE);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, areaDataList);
        mAreaListView.setAdapter(adapter);
        mAreaListView.setOnItemClickListener(this);

        requestQueue = Volley.newRequestQueue(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (level == PROVINCE_LEVEL) {
            displayProvince();
        } else if (level == CITY_LEVEL) {
            displayCity();
        } else if (level == DISTRICT_LEVEL) {
            displayDistrict();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getAdapter().getItem(position);
        if (name != null) {
            mListener.onClickFragmentItem(position);
        }
    }

    // 与Activity交互的接口
    public interface OnFragmentInteractionListener {
        // 回调函数
        void updateProvinceCache(List<Province> provinceList);
        void updateCityCache(List<City> cityList);
        void updateDistrictCache(List<District> districtList);
        void onClickFragmentItem(int position);
        void changeLevel(int level);
    }

    // 显示 province 数据，如果缓存没有 访问网络(并更新Activity缓存)
    private void displayProvince() {
        // province 不显示后退按钮
        mBackButton.setVisibility(View.GONE);
        if (provinceList != null && provinceList.size() == 34) { //缓存中数据正确，在缓存中查询
            areaDataList.clear();
            for (Province province : provinceList) {
                areaDataList.add(province.getName());
            }
            // 数据更新，控件更新
            adapter.notifyDataSetChanged();
            mAreaListView.setSelection(0);
            Log.d(TAG, "queryProvince: ");
        } else { // 网络查询
            mSelectAreaProgressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "queryProvince: 网络查询获得的");
            // 构建Volley请求
            StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) { //成功
                            Gson gson = new Gson();
                            List<Province> provinces = gson.fromJson(s,
                                    new TypeToken<List<Province>>() {}.getType());
                            mListener.updateProvinceCache(provinces);
                            for (Province province : provinces) {
                                areaDataList.add(province.getName());
                            }
                            // 数据更新，控件更新
                            adapter.notifyDataSetChanged();
                            mAreaListView.setSelection(0);
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "获取省级列表失败", Toast.LENGTH_LONG).show();
                        }
                    });
            requestQueue.add(stringRequest);
        }
    }

    private void displayCity() {
        // city 显示后退按钮
        mBackButton.setVisibility(View.VISIBLE);
        if (cityList != null && cityList.size() > 0) { //缓存中数据正确，在缓存中查询
            Log.d(TAG, "queryCity: 使用缓存里面的城市列表");
            areaDataList.clear();
            for (City city : cityList) {
                areaDataList.add(city.getName());
            }
            // 数据更新，控件更新
            adapter.notifyDataSetChanged();
            mAreaListView.setSelection(0);
        } else { // 网络查询
            mSelectAreaProgressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "queryCity: 网络查询获得城市列表");
            String url = baseUrl + "?province=" + selectedProvince.getEnglishName();
            // 构建Volley请求
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) { //成功
                            Gson gson = new Gson();
                            List<City> cityList = gson.fromJson(s,
                                    new TypeToken<List<City>>() {}.getType());
                            mListener.updateCityCache(cityList);
                            for (City city : cityList) {
                                areaDataList.add(city.getName());
                            }
                            // 数据更新，控件更新
                            adapter.notifyDataSetChanged();
                            mAreaListView.setSelection(0);
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "获取城市列表失败", Toast.LENGTH_LONG).show();
                        }
                    });
            requestQueue.add(stringRequest);
        }

    }

    private void displayDistrict() {
        // city 显示后退按钮
        mBackButton.setVisibility(View.VISIBLE);
        if (districtList != null && districtList.size() > 0) { //缓存中数据正确，在缓存中查询
            areaDataList.clear();
            for (District district : districtList) {
                areaDataList.add(district.getName());
            }
            // 数据更新，控件更新
            adapter.notifyDataSetChanged();
            mAreaListView.setSelection(0);
            Log.d(TAG, "queryCity: 使用缓存里面的地区列表");
        } else { // 网络查询
            //TODO 网络查询加入缓存
            Log.d(TAG, "queryCity: 网络查询获得地区列表");
            mSelectAreaProgressBar.setVisibility(View.VISIBLE);
            String url = baseUrl + "?province=" + selectedProvince.getEnglishName() + "&city=" + selectedCity.getEnglishName();
            // 构建Volley请求
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) { //成功
                            Gson gson = new Gson();
                            List<District> districtList = gson.fromJson(s,
                                    new TypeToken<List<District>>() {}.getType());
                            mListener.updateDistrictCache(districtList);
                            for (District district : districtList) {
                                areaDataList.add(district.getName());
                            }
                            // 数据更新，控件更新
                            adapter.notifyDataSetChanged();
                            mAreaListView.setSelection(0);
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            mSelectAreaProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "获取地区列表失败", Toast.LENGTH_LONG).show();
                        }
                    });
            requestQueue.add(stringRequest);
        }
    }


}
