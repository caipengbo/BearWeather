package cn.bearweather.bean.weatherbean;

/**
 * Title: 基础信息
 * Created by Myth on 2017/8/22.
 */
public class Basic {


    /**
     * city : 沈阳
     * cnty : 中国
     * id : CN101070101
     * lat : 41.79676819
     * lon : 123.42909241
     * update : {"loc":"2017-08-21 09:52","utc":"2017-08-21 01:52"}
     */

    private String city;
    private String cnty;
    private String id;
    private String lat;
    private String lon;
    /**
     * loc : 2017-08-21 09:52
     * utc : 2017-08-21 01:52
     */

    private UpdateBean update;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public UpdateBean getUpdate() {
        return update;
    }

    public void setUpdate(UpdateBean update) {
        this.update = update;
    }

    public static class UpdateBean {
        private String loc;
        private String utc;

        public String getLoc() {
            return loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public String getUtc() {
            return utc;
        }

        public void setUtc(String utc) {
            this.utc = utc;
        }
    }
}
