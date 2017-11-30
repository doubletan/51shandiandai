package org.xqj.shandiandai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xqj.shandiandai.model.BillItem;
import org.xqj.shandiandai.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2016/01/13.
 */
public class BillDetailsFragment extends DataFragment {

    @Bind(R.id.details) RecyclerView mDetailsView;
    @Bind(R.id.tips) TextView mNoDataTips;

    private BillAdapter mBillAdapter;
    private int mIdxForDisplayedMenu;

    private OnBillDeletedListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnBillDeletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnBillDeletedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mDetailsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDetailsView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mBillAdapter = new BillAdapter(mDetailsView, new ArrayList<BillItem>());
        mBillAdapter.setOnItemClickListener(new BillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AddBillActivity.start(getActivity(), mBillAdapter.getDataList().get(position).getId());
            }
        });
        mBillAdapter.setOnItemLongClickListener(new BillAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                mIdxForDisplayedMenu = position;
                mDetailsView.showContextMenuForChild(v);
            }
        });
        mDetailsView.setAdapter(mBillAdapter);
        registerForContextMenu(mDetailsView);

        onUpgrade();
    }

    @Override
    public void onUpgrade() {
        mBillAdapter.updateViewMode(getDefaultPreferences().getString(PreferenceKeys.KEY_VIEW_MODE, "月"));
        List<BillItem> originItems = mBillAdapter.getDataList();
        originItems.clear();
        List<BillItem> items = getCurrentBillData();
        originItems.addAll(items);
        if (!originItems.isEmpty()) {
            mBillAdapter.updateDataList(items);
            mNoDataTips.setVisibility(View.GONE);
        } else {
            mBillAdapter.notifyDataSetChanged();
            mNoDataTips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_bill_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            getRealm().beginTransaction();
            BillItem billItem = mBillAdapter.getDataList().get(mIdxForDisplayedMenu);
            int itemId = billItem.getId();
            billItem.removeFromRealm();
            getRealm().commitTransaction();
            mCallback.onBillDeleted(itemId);
        }
        return super.onContextItemSelected(item);
    }

    public interface OnBillDeletedListener {
        void onBillDeleted(int itemId);
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

        private OnItemLongClickListener mOnItemLongClickListener;

        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBindView != null && mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(v, mBindView.getChildAdapterPosition(v));
                }
                return true;
            }
        };

        private String mViewMode;
        private Calendar mCalendar;

        private String[] mWeekString;

        public BillAdapter(RecyclerView recyclerView, List<BillItem> items) {
            mBindView = recyclerView;
            mContext = mBindView.getContext();
            mBillItems = items;
            mCalendar = Calendar.getInstance();
            mViewMode = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(PreferenceKeys.KEY_VIEW_MODE, "月");
            mWeekString = mContext.getResources().getStringArray(R.array.week_str);
        }

        public void updateDataList(List<BillItem> items) {
            mBillItems = items;
            notifyDataSetChanged();
        }

        public void updateViewMode(String mode) {
            mViewMode = mode;
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

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            mOnItemLongClickListener = onItemLongClickListener;
        }

        public void removeOnItemLongClickListener() {
            mOnItemLongClickListener = null;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_bill, parent, false));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnLongClickListener(mOnLongClickListener);
            return holder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BillItem item = mBillItems.get(position);
            String typeName = item.getConsumptionType();
            mCalendar.setTimeInMillis(item.getDateTime());
            holder.date.setVisibility(View.VISIBLE);
            switch (mViewMode) {
                case "年":
                    int month = mCalendar.get(Calendar.MONTH);
                    CharSequence monthStr = DateFormat.format("MM月", mCalendar);
                    holder.date.setText(monthStr);
                    if (position != 0) {
                        mCalendar.setTimeInMillis(mBillItems.get(position - 1).getDateTime());
                        if (month == mCalendar.get(Calendar.MONTH)) {
                            holder.date.setVisibility(View.GONE);
                        }
                    }
                    break;
                case "日":
                    holder.date.setVisibility(View.GONE);
                    break;
                case "周":
                    int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
                    CharSequence dayOfWeekStr = mWeekString[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
                    holder.date.setText(dayOfWeekStr);
                    if (position != 0) {
                        mCalendar.setTimeInMillis(mBillItems.get(position - 1).getDateTime());
                        if (dayOfWeek == mCalendar.get(Calendar.DAY_OF_WEEK)) {
                            holder.date.setVisibility(View.GONE);
                        }
                    }
                    break;
                default:
                    int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                    CharSequence dayOfMonthStr = DateFormat.format("MM月dd日", mCalendar);
                    holder.date.setText(dayOfMonthStr);
                    if (position != 0) {
                        mCalendar.setTimeInMillis(mBillItems.get(position - 1).getDateTime());
                        if (day == mCalendar.get(Calendar.DAY_OF_MONTH)) {
                            holder.date.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
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

            @Bind(R.id.date) TextView date;
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

        public interface OnItemLongClickListener {
            void onItemLongClick(View v, int position);
        }
    }
}
