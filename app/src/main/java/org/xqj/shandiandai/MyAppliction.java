package org.xqj.shandiandai;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.xqj.shandiandai.model.ImagerBean;

import org.xqj.shandiandai.model.Product;

/**
 * Created by apple on 17/1/15.
 */

public class MyAppliction extends Application {
    public static RequestQueue requestQueue;
    public static String uid="";
    public static String userId;

    public static RequestQueue getVolleyRequestQueue(){
        return requestQueue;
    }

    public static SharedPreferences sp;

    private static ImagerBean user;
    private static Product product;
    private static Product product1;
    private static Product product2;
    private static Product product3;
    private static Product product4;

    public static Product getProduct() {
        return product;
    }

    public static void setProduct(Product product) {
        MyAppliction.product = product;
    }

    public static Product getProduct1() {
        return product1;
    }

    public static void setProduct1(Product product) {
        MyAppliction.product1 = product;
    }
    public static Product getProduct2() {
        return product2;
    }

    public static void setProduct2(Product product) {
        MyAppliction.product2 = product;
    }
    public static Product getProduct3() {
        return product3;
    }

    public static void setProduct3(Product product) {
        MyAppliction.product3 = product;
    }
    public static Product getProduct4() {
        return product4;
    }

    public static void setProduct4(Product product) {
        MyAppliction.product4 = product;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue= Volley.newRequestQueue(getApplicationContext());
    }

    public static ImagerBean getUser() {
        return user;
    }
    public static void setUser(ImagerBean user) {
        MyAppliction.user = user;
    }
}
