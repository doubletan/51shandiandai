package org.xqj.shandiandai.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import org.xqj.shandiandai.MyAppliction;
import org.xqj.shandiandai.R;
import org.xqj.shandiandai.adapter.ProductAdapter;
import org.xqj.shandiandai.task.BrowsingHistory;
import org.xqj.shandiandai.utils.TinyDB;

import org.xqj.shandiandai.model.Product;

public class ProductListActivity extends AppCompatActivity {

    private Product data;
    private RecyclerView recylerview;
    private ProductAdapter adapter;
    private ImageView ivBack;
    private TextView toolbarTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        initView();
        getData();
        setView();
    }

    private void setView() {

    }

    private void initView() {
        recylerview=(RecyclerView)findViewById(org.xqj.shandiandai.R.id.product_list_rv);
        ivBack=(ImageView)findViewById(org.xqj.shandiandai.R.id.iv_back);
        toolbarTv=(TextView)findViewById(org.xqj.shandiandai.R.id.toolbar_tv);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        Intent intent=getIntent();
        Integer position = intent.getIntExtra("position",-1);
        switch (position){
            case 1:
                data= MyAppliction.getProduct1();
                if(data==null){
                    setProduct((Product) new TinyDB(this).getObject("Product1",Product.class));
                }else {
                    setProduct(data);
                }
                toolbarTv.setText("热门");
                break;
            case 2:
                data= MyAppliction.getProduct2();
                if(data==null){
                    setProduct((Product) new TinyDB(this).getObject("Product2",Product.class));
                }else {
                    setProduct(data);
                }
                toolbarTv.setText("新品");
                break;
            case 3:
                data= MyAppliction.getProduct3();
                if(data==null){
                    setProduct((Product) new TinyDB(this).getObject("Product3",Product.class));
                }else {
                    setProduct(data);
                }
                toolbarTv.setText("小额");
                break;
            case 4:
                data= MyAppliction.getProduct4();
                if(data==null){
                    setProduct((Product) new TinyDB(this).getObject("Product4",Product.class));
                }else {
                    setProduct(data);
                }
                toolbarTv.setText("大额");
                break;
        }
    }

    private void setProduct(final Product product) {

        if(product!=null){
            adapter=new ProductAdapter(product.getPrdList());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this );
            recylerview.setLayoutManager(layoutManager);

            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recylerview.setAdapter(adapter);

            recylerview.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    Bundle bundle=new Bundle();
//                    bundle.putSerializable("product",product.getPrdList().get(position));
//                    startActivity(new Intent(ProductListActivity.this,ProductActivity.class).putExtras(bundle));
                    startActivity(new Intent(ProductListActivity.this,HtmlActivity.class).putExtra("url",product.getPrdList().get(position).getLink()));
                    new BrowsingHistory().execute(product.getPrdList().get(position).getUid(),"");
                }
            });

        }

    }
}
