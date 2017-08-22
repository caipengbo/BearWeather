package cn.bearweather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bearweather.R;
import cn.bearweather.bean.weatherbean.Weather;

/**
 * Created by Myth on 2017/8/22.
 */

public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private List<Weather> weatherList;
    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
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
        // TODO 将图片弄个映射https://www.heweather.com/documents
        // icon.setImageResource();
        city.setText(weather.getBasic().getCity());
        temperature.setText(weather.getNow().getTmp() + "℃");

        return view;
    }
}
