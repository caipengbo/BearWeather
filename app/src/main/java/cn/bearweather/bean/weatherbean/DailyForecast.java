package cn.bearweather.bean.weatherbean;

import com.google.gson.annotations.SerializedName;

/**
 *  Title: 一天的天气预报
 *  Created by Myth on 2017/8/22.
 */
public class DailyForecast {
    @SerializedName("astro")
    private Astronomy astronomy;
    @SerializedName("cond")
    private Condition condition;

    private class Astronomy {
        // 日出时间
        @SerializedName("sr")
        public String sunrise;
        // 日落时间
        @SerializedName("ss")
        public String sunset;
    }
    // 天气状况
    private class Condition {
        // 白天天气状况代码
        @SerializedName("code_d")
        public String codeDay;
        @SerializedName("code_n")
        public String codeNight;
        // 白天天气状况文字说明
        @SerializedName("txt_d")
        public String textDay;
        @SerializedName("txt_n")
        public String textNight;
    }
    // 时间
    @SerializedName("date")
    private String date;
    // 湿度
    @SerializedName("hum")
    private String humidity;
    // 降雨量
    @SerializedName("pcpn")
    private String precipitation;
    // 降水概率
    @SerializedName("pop")
    private String probability;
    //气压
    @SerializedName("pres")
    private String pressure;

    @SerializedName("tmp")
    private Temperature temperature;

    private class Temperature {
        public String max;
        public String min;
    }
    @SerializedName("wind")
    private Wind wind;
    private class Wind {
        // 风向
        @SerializedName("dir")
        public String direction;
        // 风力等级
        @SerializedName("sc")
        public String scale;
        // 风速
        @SerializedName("spd")
        public String speed;
    }

    public Astronomy getAstronomy() {
        return astronomy;
    }

    public void setAstronomy(Astronomy astronomy) {
        this.astronomy = astronomy;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}
