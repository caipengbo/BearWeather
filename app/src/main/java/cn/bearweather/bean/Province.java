package cn.bearweather.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Myth on 2017/8/21.
 */

public class Province extends AreaBase {
    // 缓存专用
    List<City> cityList;

    public Province() {
        super.level = 1;
        cityList = new ArrayList<>();
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
