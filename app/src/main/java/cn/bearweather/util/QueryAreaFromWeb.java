package cn.bearweather.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import cn.bearweather.bean.Province;

/**
 * Created by Myth on 2017/8/21.
 */

public class QueryAreaFromWeb {

    private static final String TAG = "Class QueryAreaFromWeb";
    // 查询的上下文
    private Context context;
    private RequestQueue requestQueue;
    private String baseUrl;
    List<Integer> list = new ArrayList<>();
    List<Province> provinceList = new ArrayList<>();


    public QueryAreaFromWeb(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        baseUrl = "http://caipengbo.cn/api/china.json";
    }

    public List<Province> queryProvice(Context context) {
        provinceList = null;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl,
                new Response.Listener<String>() {
                    int a =0;
                    @Override
                    public void onResponse(String s) {
                        a = 2;
                        list.add(a);
//                        adapter.notifyDataSetChanged();
//                        listView.setSelection(0);
                        Province province = new Province();
                        province.setEnglishName("sss");
                        province.setName("nihao");
                        // 放到缓存中
                        provinceList.add(province);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        requestQueue.add(stringRequest);
        return provinceList;
    }
}
