package cn.bearweather.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.bearweather.R;
import cn.bearweather.bean.weatherbean.Weather;


/**
 * Created by Myth on 2017/8/22.
 */

public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private List<Weather> weatherList;
    // 天气状况代码与天气状况图标资源之间的映射
    private Map<String, Integer> iconMap;
    private void initMap() {
        iconMap = new HashMap<>();
        iconMap.put("100", new Integer(R.drawable.weather_icon_100));
        iconMap.put("101", new Integer(R.drawable.weather_icon_101));
        iconMap.put("102", new Integer(R.drawable.weather_icon_102));
        iconMap.put("103", new Integer(R.drawable.weather_icon_103));
        iconMap.put("104", new Integer(R.drawable.weather_icon_104));
        iconMap.put("200", new Integer(R.drawable.weather_icon_200));
        iconMap.put("201", new Integer(R.drawable.weather_icon_201));
        iconMap.put("202", new Integer(R.drawable.weather_icon_202));
        iconMap.put("203", new Integer(R.drawable.weather_icon_203));
        iconMap.put("204", new Integer(R.drawable.weather_icon_204));
        iconMap.put("205", new Integer(R.drawable.weather_icon_205));
        iconMap.put("206", new Integer(R.drawable.weather_icon_206));
        iconMap.put("207", new Integer(R.drawable.weather_icon_207));
        iconMap.put("208", new Integer(R.drawable.weather_icon_208));
        iconMap.put("209", new Integer(R.drawable.weather_icon_209));
        iconMap.put("210", new Integer(R.drawable.weather_icon_210));
        iconMap.put("211", new Integer(R.drawable.weather_icon_211));
        iconMap.put("212", new Integer(R.drawable.weather_icon_212));
        iconMap.put("213", new Integer(R.drawable.weather_icon_213));
        iconMap.put("300", new Integer(R.drawable.weather_icon_300));
        iconMap.put("301", new Integer(R.drawable.weather_icon_301));
        iconMap.put("302", new Integer(R.drawable.weather_icon_302));
        iconMap.put("303", new Integer(R.drawable.weather_icon_303));
        iconMap.put("304", new Integer(R.drawable.weather_icon_304));
        iconMap.put("305", new Integer(R.drawable.weather_icon_305));
        iconMap.put("306", new Integer(R.drawable.weather_icon_306));
        iconMap.put("307", new Integer(R.drawable.weather_icon_307));
        iconMap.put("308", new Integer(R.drawable.weather_icon_308));
        iconMap.put("309", new Integer(R.drawable.weather_icon_309));
        iconMap.put("310", new Integer(R.drawable.weather_icon_310));
        iconMap.put("311", new Integer(R.drawable.weather_icon_311));
        iconMap.put("312", new Integer(R.drawable.weather_icon_312));
        iconMap.put("313", new Integer(R.drawable.weather_icon_313));
        iconMap.put("400", new Integer(R.drawable.weather_icon_400));
        iconMap.put("401", new Integer(R.drawable.weather_icon_401));
        iconMap.put("402", new Integer(R.drawable.weather_icon_402));
        iconMap.put("403", new Integer(R.drawable.weather_icon_403));
        iconMap.put("404", new Integer(R.drawable.weather_icon_404));
        iconMap.put("405", new Integer(R.drawable.weather_icon_405));
        iconMap.put("406", new Integer(R.drawable.weather_icon_406));
        iconMap.put("407", new Integer(R.drawable.weather_icon_407));
        iconMap.put("500", new Integer(R.drawable.weather_icon_500));
        iconMap.put("501", new Integer(R.drawable.weather_icon_501));
        iconMap.put("502", new Integer(R.drawable.weather_icon_502));
        iconMap.put("503", new Integer(R.drawable.weather_icon_503));
        iconMap.put("504", new Integer(R.drawable.weather_icon_504));
        iconMap.put("507", new Integer(R.drawable.weather_icon_507));
        iconMap.put("508", new Integer(R.drawable.weather_icon_508));
        iconMap.put("900", new Integer(R.drawable.weather_icon_900));
        iconMap.put("901", new Integer(R.drawable.weather_icon_901));
        iconMap.put("999", new Integer(R.drawable.weather_icon_999));

    }

    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
        initMap();
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        // 简单优化，提升ListView的效率
        if (convertView == null) {
            // 注意获得的View 对象是从 Item布局文件中获取的
            view = View.inflate(context, R.layout.listview_city_item, null);
        } else {
            view = convertView;
        }
        // 获取每个控件
        ImageView icon = (ImageView)view.findViewById(R.id.item_condition_icon);
        TextView city = (TextView)view.findViewById(R.id.item_city_text);
        TextView temperature = (TextView)view.findViewById(R.id.item_temperature_text);

        Weather weather = weatherList.get(position);
        // 在item中填入值
        icon.setImageResource(iconMap.get(weather.getNow().getCond().getCode()));
        city.setText(weather.getBasic().getCity());
        int color = ContextCompat.getColor(context, R.color.colorRed);
        temperature.setText(weather.getNow().getTmp() + "℃");

        if (position == 0) {
            city.setTextColor(color);
            temperature.setTextColor(color);
        }

        return view;
    }
}
