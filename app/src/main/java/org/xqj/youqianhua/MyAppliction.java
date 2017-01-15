package org.xqj.youqianhua;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by apple on 17/1/15.
 */

public class MyAppliction extends Application {
    public static RequestQueue requestQueue;
    public static RequestQueue getVolleyRequestQueue(){
        return requestQueue;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue= Volley.newRequestQueue(getApplicationContext());
    }
}
