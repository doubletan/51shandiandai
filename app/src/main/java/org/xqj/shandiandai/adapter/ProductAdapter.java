package org.xqj.shandiandai.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.xqj.shandiandai.model.Product;
import org.xqj.shandiandai.utils.Constants;

import java.util.List;



/**
 * Created by apple on 2017/4/11.
 */

public class ProductAdapter extends BaseQuickAdapter<Product.PrdListBean,BaseViewHolder> {

    public ProductAdapter(List<Product.PrdListBean> data) {
        super(org.xqj.shandiandai.R.layout.product_item, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, Product.PrdListBean item) {
                helper.setText(org.xqj.shandiandai.R.id.tv_ProductName,item.getName());
                helper.setText(org.xqj.shandiandai.R.id.tv_count,item.getLines());
                helper.setText(org.xqj.shandiandai.R.id.tv_time,item.getTimeLimit());
                helper.setText(org.xqj.shandiandai.R.id.tv_detail,item.getSummary());

        Glide.with(mContext).load(Constants.piURL+item.getLogo()).skipMemoryCache(true).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) helper.getView(org.xqj.shandiandai.R.id.head));
    }
}
