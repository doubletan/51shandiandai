package org.xqj.bill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xqj.bill.model.BillItem;
import org.xqj.bill.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author Chaos
 *         2016/01/13.
 */
public class BillDetailsFragment extends Fragment implements Updateable {

    @Bind(R.id.details) RecyclerView mDetailsView;
    @Bind(R.id.tips) TextView mNoDataTips;

    private BillAdapter mBillAdapter;

    private SharedPreferences mDefaultPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mDetailsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDetailsView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mBillAdapter = new BillAdapter(mDetailsView, new ArrayList<BillItem>());
        mBillAdapter.setOnItemClickListener(new BillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AddBillActivity.start(getActivity(), mBillAdapter.getDataList().get(position).getId());
            }
        });
        mDetailsView.setAdapter(mBillAdapter);

        onUpdate();
    }

    private List<BillItem> getBillData() {
        Realm realm = Realm.getInstance(getActivity());
        List<BillItem> items = new ArrayList<>();
        RealmResults<BillItem> results = realm.where(BillItem.class)
                .greaterThanOrEqualTo("dateTime", mDefaultPreferences.getLong(PreferenceKeys.KEY_GREATER_TIME, 0))
                .lessThan("dateTime", mDefaultPreferences.getLong(PreferenceKeys.KEY_LESS_TIME, 0))
                .findAll();
        results.sort("dateTime", Sort.DESCENDING);
        for (BillItem item : results) {
            items.add(item);
        }
        return items;
    }

    @Override
    public void onUpdate() {
        List<BillItem> originItems = mBillAdapter.getDataList();
        originItems.clear();
        List<BillItem> items = getBillData();
        originItems.addAll(items);
        if (!originItems.isEmpty()) {
            mBillAdapter.updateDataList(items);
            mNoDataTips.setVisibility(View.GONE);
        } else {
            mNoDataTips.setVisibility(View.VISIBLE);
        }
    }

    public static class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

        private RecyclerView mBindView;
        private Context mContext;
        private List<BillItem> mBillItems;

        private OnItemClickListener mOnItemClickListener;

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBindView != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, mBindView.getChildAdapterPosition(v));
                }
            }
        };

        public BillAdapter(RecyclerView recyclerView, List<BillItem> items) {
            mBindView = recyclerView;
            mContext = mBindView.getContext();
            mBillItems = items;
        }

        public void updateDataList(List<BillItem> items) {
            mBillItems = items;
            notifyDataSetChanged();
        }

        public List<BillItem> getDataList() {
            return mBillItems;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        public void removeOnItemClickListener() {
            mOnItemClickListener = null;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_bill, parent, false));
            holder.itemView.setOnClickListener(mOnClickListener);
            return holder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BillItem item = mBillItems.get(position);
            String typeName = item.getConsumptionType().getTypeName();
            holder.msg.setText(
                    TextUtils.isEmpty(item.getNote())
                            ? typeName : String.format("%1$s-%2$s", typeName, item.getNote()));
            holder.sum.setText((item.isIncome() ? "+" : "-") + item.getSum());
            holder.typeImg.setImageResource(item.isIncome() ? R.drawable.income : R.drawable.expend);
        }

        @Override
        public int getItemCount() {
            return mBillItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.msg) TextView msg;
            @Bind(R.id.sum) TextView sum;
            @Bind(R.id.type_img) ImageView typeImg;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View v, int position);
        }
    }
}
