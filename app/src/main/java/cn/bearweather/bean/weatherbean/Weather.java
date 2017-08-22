package cn.bearweather.bean.weatherbean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Title: "HeWeather5"
 * Created by Myth on 2017/8/22.
 */
public class Weather {
    private Aqi aqi;
    private Basic basic;
    @SerializedName("daily_forecast")
    private List<DailyForecast> dailyForecastList;
    @SerializedName("hourly_forecast")
    private List<HourlyForecast> hourlyForecastList;
    private Now now;
    private String status;
    private Suggestion suggestion;

    public Aqi getAqi() {
        return aqi;
    }

    public void setAqi(Aqi aqi) {
        this.aqi = aqi;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public List<DailyForecast> getDailyForecastList() {
        return dailyForecastList;
    }

    public void setDailyForecastList(List<DailyForecast> dailyForecastList) {
        this.dailyForecastList = dailyForecastList;
    }

    public List<HourlyForecast> getHourlyForecastList() {
        return hourlyForecastList;
    }

    public void setHourlyForecastList(List<HourlyForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
    }

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
