package cn.bearweather.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bearweather.R;
import cn.bearweather.bean.City;
import cn.bearweather.bean.District;
import cn.bearweather.bean.Province;
import cn.bearweather.fragment.SelectAreaFragment;

/**
 *  选择地区， 主要用来：1.缓存fragment获取的数据  2.控制fragment的创建  3. 控制fragment
 */
public class SelectAreaActivity extends AppCompatActivity implements SelectAreaFragment.OnFragmentInteractionListener {
    private Fragment mSelectProvinceFragment;
    private Fragment mSelectCityFragment;
    private Fragment mSelectDistrictFragment;
    private  FragmentManager fragmentManager;
    // 用来控制fragment
    private List<Province> provinceCacheList = new ArrayList<>();
    private int currentLevel;
    private static final int PROVINCE_LEVEL = 1;
    private static final int CITY_LEVEL = 2;
    private static final int DISTRICT_LEVEL = 3;
    private Province selectedProvince;
    private City selectedCity;
    private District selectedDistrict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        mSelectProvinceFragment = new SelectAreaFragment();
        currentLevel = PROVINCE_LEVEL;
        fragmentManager = getSupportFragmentManager();
        // 动态改变fragment，在content区域改变fragment
        fragmentManager.beginTransaction().replace(R.id.select_area_content, mSelectProvinceFragment).commit();
    }



    @Override
    public void updateProvinceCache(List<Province> provinceList) {
        Log.d("", "updateProvinceCache: 已经更新缓存了");
        provinceCacheList = provinceList;
        currentLevel = PROVINCE_LEVEL;
    }

    @Override
    public void updateCityCache(List<City> cityList) {
        selectedProvince.setCityList(cityList);
        currentLevel = CITY_LEVEL;
    }

    @Override
    public void updateDistrictCache(List<District> districtList) {
        selectedCity.setDistrictList(districtList);
        currentLevel = DISTRICT_LEVEL;
    }

    @Override
    public void onClickFragmentItem(int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (currentLevel) {
            case PROVINCE_LEVEL: { // province 页面 点击 市item 新建 cityfragment
                // 查看缓存
                selectedProvince = provinceCacheList.get(position);
                List<City> cityList = selectedProvince.getCityList();
                // 添加新的fragment
                transaction.hide(mSelectProvinceFragment);
                transaction.add(R.id.select_area_content,mSelectCityFragment=new SelectAreaFragment(CITY_LEVEL, selectedProvince, null,null, cityList, null))
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case CITY_LEVEL: { // city 页面 点击 district item
                selectedCity = selectedProvince.getCityList().get(position);
                List<District> districtList = selectedCity.getDistrictList();
                transaction.hide(mSelectCityFragment);
                transaction.add(R.id.select_area_content,mSelectDistrictFragment=new SelectAreaFragment(DISTRICT_LEVEL, selectedProvince, selectedCity,null, null, districtList))
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case DISTRICT_LEVEL: {  // district页面 点击 直接返回MainActivity
                selectedDistrict = selectedCity.getDistrictList().get(position);
                Intent intent = new Intent();
                intent.putExtra("info", selectedProvince.getName() + selectedCity.getName() + selectedDistrict.getName());
                setResult(RESULT_OK,intent);
                finish();
                break;
            }
        }
    }

    @Override
    public void changeLevel(int level) {
        currentLevel = level;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentLevel >= 1 && fragmentManager.getBackStackEntryCount() > 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                fragmentManager.popBackStack();
                currentLevel = currentLevel - 1;
                Log.d("", "onKeyDown: 按下返回键");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
