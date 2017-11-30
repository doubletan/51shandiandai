package org.xqj.shandiandai.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.xqj.shandiandai.MainActivity;
import org.xqj.shandiandai.MyAppliction;
import org.xqj.shandiandai.task.GetImageBean;
import org.xqj.shandiandai.task.GetProduct;
import org.xqj.shandiandai.task.GetProduct2;
import org.xqj.shandiandai.task.GetProduct4;
import org.xqj.shandiandai.utils.SPUtils;
import org.xqj.shandiandai.utils.SharedPreferencesUtil;

import org.xqj.shandiandai.task.GetProduct1;
import org.xqj.shandiandai.task.GetProduct3;
import org.xqj.shandiandai.utils.DoubleClickExit;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {
    private SwitchHandler mHandler = new SwitchHandler(this);
    private static  final String URL="http://www.shoujijiekuan.com/tantan/app124.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        boolean url1 = SPUtils.contains(this, "url");
        if(!url1){
            setUrl();
        }else {
            TextNet();
            boolean flag = SPUtils.contains(this, "userId");
            if(flag){
                MyAppliction.userId= (String) SPUtils.get(this,"userId","");
                mHandler.sendEmptyMessageDelayed(2, 1000);
                MyAppliction.uid= (String) SPUtils.get(this,"uid","");
            }else {
                mHandler.sendEmptyMessageDelayed(3, 1000);
            }
        }

        //诸葛
        ZhugeSDK.getInstance().init(getApplicationContext());
    }
    private void setWelcome(){
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(SplashActivity.this, SharedPreferencesUtil.FIRST_OPEN, true);
        if (isFirstOpen) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }else {
                mHandler.sendEmptyMessageDelayed(2, 1000);
        }
    }
    private static class SwitchHandler extends Handler {
        private WeakReference<SplashActivity> mWeakReference;

        SwitchHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<SplashActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        MainActivity.launch(activity);
                        break;
                    case 2:
                        HomeActivity.launch(activity);
                        break;
                    case 3:
                        Login2Activity.launch(activity);
                        break;
                }
                activity.finish();
                }

        }
    }
    private void setUrl() {
        StringRequest request=new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new GetImageBean(SplashActivity.this).execute();
                new GetProduct(SplashActivity.this).execute();
                new GetProduct1(SplashActivity.this).execute();
                new GetProduct2(SplashActivity.this).execute();
                new GetProduct3(SplashActivity.this).execute();
                new GetProduct4(SplashActivity.this).execute();
                SPUtils.put(SplashActivity.this,"url",URL);
                setWelcome();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        });
        MyAppliction.getVolleyRequestQueue().add(request);
    }

    private void TextNet() {
        ConnectivityManager con= (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            //执行相关操作
            new GetImageBean(this).execute();
            new GetProduct(this).execute();
            new GetProduct1(this).execute();
            new GetProduct2(this).execute();
            new GetProduct3(this).execute();
            new GetProduct4(this).execute();
        }else{
            Toast.makeText(this,"亲，网络连接失败咯！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public void onBackPressed() {
        if (!DoubleClickExit.check()) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
