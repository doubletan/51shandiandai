package org.xqj.shandiandai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;
import org.xqj.shandiandai.MyAppliction;
import org.xqj.shandiandai.R;
import org.xqj.shandiandai.adapter.ProductAdapter;
import org.xqj.shandiandai.model.ImagerBean;
import org.xqj.shandiandai.task.BrowsingHistory;
import org.xqj.shandiandai.utils.Constants;
import org.xqj.shandiandai.utils.SPUtils;
import org.xqj.shandiandai.utils.TinyDB;
import org.xqj.shandiandai.utils.ToastUtils;

import org.xqj.shandiandai.model.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;

public class HomeActivity extends AppCompatActivity {

    BGABanner bannerFrescoDemoContent;
    @Bind(R.id.recylerview)
    RecyclerView recylerview;
    TextView btntv1;
    private ProductAdapter adapter;
    private MyListener listener;
    private TextView btntv2;
    private TextView btntv3;
    private TextView btntv4;
    private View view;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setHeader();
        setListener();
        setRecycler();

        //诸葛
        setZhuge();
    }

    private void setZhuge() {
        //定义用户识别码
        String userid = (String) SPUtils.get(this, "uid", "");

//定义用户属性
        JSONObject personObject = new JSONObject();

        try {
            personObject.put("avatar", "");
            personObject.put("name", "");
            personObject.put("gender", "");
            personObject.put("等级", 90);
            personObject.put("APP", "先花花");
            personObject.put("渠道", "信和");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//标识用户
        ZhugeSDK.getInstance().identify(getApplicationContext(), userid,
                personObject);
    }

    private void setHeader() {
        view = getLayoutInflater().inflate(R.layout.list_head, null);
        bannerFrescoDemoContent = (BGABanner) view.findViewById(R.id.banner_fresco_demo_content);
        btntv1 = (TextView) view.findViewById(R.id.btn_tv1);
        btntv2 = (TextView) view.findViewById(R.id.btn_tv2);
        btntv3 = (TextView) view.findViewById(R.id.btn_tv3);
        btntv4 = (TextView) view.findViewById(R.id.btn_tv4);
    }


    private void setRecycler() {
        if (MyAppliction.getProduct() == null) {
            setProduct((Product) new TinyDB(this).getObject("Product", Product.class));
        } else {
            setProduct(MyAppliction.getProduct());
        }

    }

    private void setProduct(final Product product) {

        if (product != null) {
            adapter = new ProductAdapter(product.getPrdList());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recylerview.setLayoutManager(layoutManager);

            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recylerview.setAdapter(adapter);
            adapter.addHeaderView(view);
            recylerview.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("product", product.getPrdList().get(position));
//                    startActivity(new Intent(HomeActivity.this, ProductActivity.class).putExtras(bundle));
                    startActivity(new Intent(HomeActivity.this, HtmlActivity.class).putExtra("url",product.getPrdList().get(position).getLink()));
                    new BrowsingHistory().execute(product.getPrdList().get(position).getUid(),"");
                }
            });

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    private void setListener() {
        listener = new MyListener();
        btntv1.setOnClickListener(listener);
        btntv2.setOnClickListener(listener);
        btntv3.setOnClickListener(listener);
        btntv4.setOnClickListener(listener);


        if (MyAppliction.getUser() == null) {
            setBunder((ImagerBean) new TinyDB(this).getObject("ImagerBean", ImagerBean.class));
        } else {
            setBunder(MyAppliction.getUser());
        }
    }

    private void setBunder(ImagerBean bean) {
        if (bean != null) {
            final ArrayList<String> arr = new ArrayList<>();
            final List<ImagerBean.DaohangBean> daohang = bean.getDaohang();
            for (ImagerBean.DaohangBean s : daohang) {
                arr.add(Constants.piURL + s.getAdvpath());
            }
            bannerFrescoDemoContent.setDelegate(new BGABanner.Delegate<ImageView, String>() {
                @Override
                public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                    startActivity(new Intent(HomeActivity.this, HtmlActivity.class).putExtra("url", daohang.get(position).getLink()));
                }
            });
            bannerFrescoDemoContent.setAdapter(new BGABanner.Adapter<ImageView, String>() {
                @Override
                public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                    Glide.with(HomeActivity.this)
                            .load(model)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .dontAnimate()
                            .into(itemView);
                }
            });
            bannerFrescoDemoContent.setData(arr, null);
        }

    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tv1:
                    Intent intent1 = new Intent(HomeActivity.this, ProductListActivity.class);
                    intent1.putExtra("position", 1);
                    startActivity(intent1);
                    break;
                case R.id.btn_tv2:
                    Intent intent2 = new Intent(HomeActivity.this, ProductListActivity.class);
                    intent2.putExtra("position", 2);
                    startActivity(intent2);
                    break;
                case R.id.btn_tv3:
                    Intent intent3 = new Intent(HomeActivity.this, ProductListActivity.class);
                    intent3.putExtra("position", 3);
                    startActivity(intent3);
                    break;
                case R.id.btn_tv4:
                    Intent intent4 = new Intent(HomeActivity.this, ProductListActivity.class);
                    intent4.putExtra("position", 4);
                    startActivity(intent4);
                    break;
            }
        }
    }

    private int backPressCount = 0;

    @Override
    public void onBackPressed() {

        backPressCount++;
        if (2 == backPressCount) {
            this.finish();
        } else {
            ToastUtils.showToast(this, "再按一次退出程序");
        }

    }

    @Override
    protected void onDestroy() {
        ZhugeSDK.getInstance().flush(getApplicationContext());
        super.onDestroy();
    }
}
