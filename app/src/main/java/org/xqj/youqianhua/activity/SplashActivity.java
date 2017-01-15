package org.xqj.youqianhua.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.analytics.MobclickAgent;

import org.xqj.youqianhua.MainActivity;
import org.xqj.youqianhua.MyAppliction;
import org.xqj.youqianhua.R;
import org.xqj.youqianhua.utils.DoubleClickExit;
import org.xqj.youqianhua.utils.SPUtils;
import org.xqj.youqianhua.utils.SharedPreferencesUtil;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {
    private SwitchHandler mHandler = new SwitchHandler(this);
    private static  final String URL="http://www.shoujiweidai.com/android/app19.html";
    private  static boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        boolean url1 = SPUtils.contains(this, "url");
        if(!url1){
            setUrl();
            mHandler.sendEmptyMessageDelayed(1, 1000);
            //  setWelcome();
        }else {
            flag=true;
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }
    private void setWelcome(){
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(SplashActivity.this, SharedPreferencesUtil.FIRST_OPEN, true);
        if (isFirstOpen) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }else {
                mHandler.sendEmptyMessageDelayed(1, 1000);
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
                    if(flag){
                        Main2Activity.launch(activity);
                        activity.finish();
                    }else {
                        MainActivity.launch(activity);
                        activity.finish();
                    }
                }

        }
    }
    private void setUrl() {
        StringRequest request=new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SPUtils.put(SplashActivity.this,"url",URL);
                flag=true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MyAppliction.getVolleyRequestQueue().add(request);
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
