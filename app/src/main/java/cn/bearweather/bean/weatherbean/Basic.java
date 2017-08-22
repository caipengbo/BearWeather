package cn.bearweather.bean.weatherbean;

import com.google.gson.annotations.SerializedName;

/**
 * Title: 基础信息
 * Created by Myth on 2017/8/22.
 */
public class Basic {
    @SerializedName("city")
    private String city;
    @SerializedName("update")
    private Update update;

    private class Update {
        @SerializedName("loc")
        public String updateTime;
        @SerializedName("utc")
        public String utcTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
