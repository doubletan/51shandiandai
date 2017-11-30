package org.xqj.shandiandai.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xqj.shandiandai.MyAppliction;
import org.xqj.shandiandai.R;
import org.xqj.shandiandai.utils.CaptchaTimeCount;
import org.xqj.shandiandai.utils.CheckUtil;
import org.xqj.shandiandai.utils.CodeUtils;
import org.xqj.shandiandai.utils.Constants;
import org.xqj.shandiandai.utils.DeviceUtil;
import org.xqj.shandiandai.utils.GetMyKey;
import org.xqj.shandiandai.utils.SPUtils;
import org.xqj.shandiandai.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login2Activity extends AppCompatActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.bt_getCode)
    Button btGetCode;
    @Bind(R.id.et_yanzheng)
    EditText etYanzheng;
    @Bind(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @Bind(R.id.layout_yanzheng)
    RelativeLayout layoutYanzheng;
    @Bind(R.id.layout_code)
    RelativeLayout layoutCode;

    private CaptchaTimeCount captchaTimeCount;
    private String phone;
    private CodeUtils codeUtils;
    private String yanZhengCode;
    private String yanZhengResult;
    private String etYanZhengCode;
    private String getCode;
    private String savePhone;
    private String code;


    public static void launch(Context context) {
        context.startActivity(new Intent(context, Login2Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);
        captchaTimeCount = new CaptchaTimeCount(Constants.Times.MILLIS_IN_TOTAL, Constants.Times.COUNT_DOWN_INTERVAL, btGetCode, this);
        initYanzheng();
    }

    @OnClick({R.id.bt_getCode, R.id.bt_Login, R.id.iv_yanzheng})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getCode:
                getCodeMessage();
                break;
            case R.id.bt_Login:
                if (TextUtils.isEmpty(getCode)) {
                    getCodeMessage();
                } else {
                    setLogin();
                }
                break;
            case R.id.iv_yanzheng:
                initYanzheng();
                break;

        }
    }

    /**
     * 获取短信验证码
     */
    private void getCodeMessage() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(yanZhengResult)) {
            initYanzheng();
            layoutYanzheng.setVisibility(View.VISIBLE);
            return;
        }
        etYanZhengCode = etYanzheng.getText().toString().trim();

        if (TextUtils.isEmpty(etYanZhengCode)) {
            Toast.makeText(this, "请输入图片里的结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (!yanZhengResult.equals(etYanZhengCode)) {
            Toast.makeText(this, "图片结果输入有误", Toast.LENGTH_LONG).show();
            initYanzheng();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", GetMyKey.getKey());
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("登录中.....")
                .setCancellable(true)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgnMsg";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("QuickLgnMsgResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        getCode = result.substring(2);
                        savePhone = phone;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutCode.setVisibility(View.VISIBLE);
                                // 开始计时
                                captchaTimeCount.start();
                                Toast.makeText(Login2Activity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                                hud.dismiss();
                            }
                        });
                    } else if (!TextUtils.isEmpty(result) && result.startsWith("1,")) {
                        MyAppliction.userId = result.substring(2);
                        SPUtils.put(Login2Activity.this, "userId", MyAppliction.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login2Activity.this, "登录成功", Toast.LENGTH_LONG).show();
                                HomeActivity.launch(Login2Activity.this);
                                finish();
                                hud.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                                hud.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                            hud.dismiss();
                        }
                    });
                }
            }
        }).start();

    }

    private void initYanzheng() {
        codeUtils = CodeUtils.getInstance();
        Bitmap bitmap = codeUtils.createBitmap();
        ivYanzheng.setImageBitmap(bitmap);
        yanZhengCode = codeUtils.getCode();
        yanZhengResult = codeUtils.getResult() + "";
    }

    /**
     * 登陆
     */

    private void setLogin() {

        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(getCode)) {
            Toast.makeText(this, "请获取手机验证码", Toast.LENGTH_LONG).show();
            return;
        }

        code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!code.equals(getCode)) {
            Toast.makeText(this, "验证码输入错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (!phone.equals(savePhone)) {
            Toast.makeText(this, "手机号与验证码不匹配", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", "");
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("登录中.....")
                .setCancellable(true)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgn";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String result = object.getProperty("QuickLgnResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        MyAppliction.userId = result.substring(2);
                        SPUtils.put(Login2Activity.this, "userId", MyAppliction.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HomeActivity.launch(Login2Activity.this);
                                finish();
                                Toast.makeText(Login2Activity.this, "登录成功", Toast.LENGTH_LONG).show();
                                hud.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                                hud.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                            hud.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    private long mLastBackTime = 0;

    @Override
    public void onBackPressed() {
        // finish while click back key 2 times during 1s.
        if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
            finish();
        } else {
            mLastBackTime = System.currentTimeMillis();
            ToastUtils.showToast(this, "再点一次 ");
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
