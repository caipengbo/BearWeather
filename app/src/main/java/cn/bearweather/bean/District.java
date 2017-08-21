package cn.bearweather.bean;

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

}
