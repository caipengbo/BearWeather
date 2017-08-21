package cn.bearweather.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Myth on 2017/8/21.
 */

public class City extends AreaBase {

    // 缓存专用
    List<District> districtList;
    public City() {
        super.level = 2;
        districtList = new ArrayList<>();
    }


    public List<District> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<District> districtList) {
        this.districtList = districtList;
    }
}
