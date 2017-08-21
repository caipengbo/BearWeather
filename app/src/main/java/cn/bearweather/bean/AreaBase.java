package cn.bearweather.bean;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Myth on 2017/8/21.
 */

public class AreaBase {
    protected int level;
    @SerializedName("name")
    protected String name;
    @SerializedName("english_name")
    protected String englishName;

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaBase areaBase = (AreaBase) o;

        return name.equals(areaBase.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
