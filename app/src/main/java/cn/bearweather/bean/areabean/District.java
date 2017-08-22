package cn.bearweather.bean.areabean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Myth on 2017/8/21.
 */

public class District extends AreaBase {
    @SerializedName("code")
    private String code;

    public District() {
        super.level = 3;
    }

    public String getCode() {
        return code;
    }
}
