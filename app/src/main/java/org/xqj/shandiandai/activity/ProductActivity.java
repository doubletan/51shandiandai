package org.xqj.shandiandai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itheima.roundedimageview.RoundedImageView;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.xqj.shandiandai.MyAppliction;
import org.xqj.shandiandai.model.Product;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xqj.shandiandai.utils.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProductActivity extends AppCompatActivity {


    @Bind(org.xqj.shandiandai.R.id.desc_head)
    RoundedImageView descHead;
    @Bind(org.xqj.shandiandai.R.id.tv_demand1)
    TextView tvDemand1;
    @Bind(org.xqj.shandiandai.R.id.tv_demand2)
    TextView tvDemand2;
    @Bind(org.xqj.shandiandai.R.id.tv_tips1)
    TextView tvTips1;
    @Bind(org.xqj.shandiandai.R.id.tv_tips2)
    TextView tvTips2;
    @Bind(org.xqj.shandiandai.R.id.apply)
    Button apply;
    @Bind(org.xqj.shandiandai.R.id.toolbar)
    Toolbar toolbar;

    private static String str = "http://www.shoujiweidai.com";
    @Bind(org.xqj.shandiandai.R.id.lines)
    TextView lines;
    @Bind(org.xqj.shandiandai.R.id.timeLimit)
    TextView timeLimit;
    @Bind(org.xqj.shandiandai.R.id.cost)
    TextView cost;
    @Bind(org.xqj.shandiandai.R.id.rate)
    TextView rate;
    @Bind(org.xqj.shandiandai.R.id.level)
    TextView level;
    @Bind(org.xqj.shandiandai.R.id.difficulty)
    TextView difficulty;

    private Product.PrdListBean product = new Product.PrdListBean();

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ProductActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.xqj.shandiandai.R.layout.activity_product);
        ButterKnife.bind(this);
        getDate();
    }

    private void getDate() {
        product = (Product.PrdListBean) getIntent().getSerializableExtra("product");
        Log.e("product", product.toString() + "-----------");
        if(product!=null){
            Glide.with(this).load(str + product.getLogo()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(descHead);
            lines.setText(product.getLines());
            timeLimit.setText(product.getTimeLimit());
            rate.setText(product.getRate());
            difficulty.setText(product.getDifficulty());
            tvDemand1.setText("1、"+product.getDemand1());
            tvDemand2.setText("2、"+product.getDemand2());
            tvTips1.setText("1、"+product.getTips1());
            tvTips2.setText("2、"+product.getTips2());
        }

    }

    private JSONObject json=null;
    @OnClick({org.xqj.shandiandai.R.id.iv_back, org.xqj.shandiandai.R.id.apply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case org.xqj.shandiandai.R.id.iv_back:
                finish();
                break;
            case org.xqj.shandiandai.R.id.apply:
                //诸葛
                setZhuge();
                try {
                    JSONObject json1 = new JSONObject();
                    json1.put("userId", MyAppliction.uid);
                    json1.put("prdId", product.getUid());
                    json1.put("channel", "Andriod-先花花");
                    json = new JSONObject();
                    json.put("Record", json1);
                } catch (JSONException e1) {
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String nameSpace = "http://chachaxy.com/";
                        String methodName = "SetRecord";
                        String URL = "http://www.shoujiweidai.com/Service/WSForAPP3.asmx";
                        String SOAP_ACTION = nameSpace + methodName;
                        SoapObject rpc = new SoapObject(nameSpace, methodName);
                        rpc.addProperty("strJson", json.toString());
                        HttpTransportSE transport = new HttpTransportSE(URL);
                        transport.debug = true;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = rpc;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(rpc);
                        try {
                            transport.call(SOAP_ACTION, envelope);
                        } catch (Exception e) {
                        }
                        SoapObject object = (SoapObject) envelope.bodyIn;
                    }
                }).start();
                startActivity(new Intent(this, HtmlActivity.class).putExtra("html", product.getLink()));
                break;
        }
    }

    private void setZhuge() {
        //定义与事件相关的属性信息
        JSONObject eventObject = new JSONObject();
        String userId= (String) SPUtils.get(this,"uid","");
        try {
            eventObject.put("用户ID", userId);
            eventObject.put("产品ID", product.getUid());
            eventObject.put("产品名称", product.getName());
            eventObject.put("渠道", "信和");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //记录事件
        ZhugeSDK.getInstance().track(getApplicationContext(), "先花花",
                eventObject);
    }
}
