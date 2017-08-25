package cn.bearweather.bean.dbbean;

import org.litepal.crud.DataSupport;

/**
 * Created by Myth on 2017/8/23.
 */

public class Item extends DataSupport {
    private String icon;
    private String name;
    private String code;
    private String temp;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return (name + temp);
    }
}
