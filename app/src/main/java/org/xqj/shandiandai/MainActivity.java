package org.xqj.shandiandai;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import org.xqj.shandiandai.model.BillItem;

import org.xqj.shandiandai.utils.DoubleClickExit;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener,
        DialogCreatable, DatePickerDialog.OnDateSetListener, BillDetailsFragment.OnBillDeletedListener {

    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_EXCEEDING_LIMIT = 2;

    private static final float RELATIVE_SIZE = 1.3f;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tabs) TabLayout mTabLayout;
    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.date) TextView mDateTextView;
    @Bind(R.id.income_btn) TextView mIncomeTextView;
    @Bind(R.id.expense_btn) TextView mExpenseTextView;

    private BillDetailsFragment mBillDetailsFragment;
    private BillPieChartFragment mBillPieChartFragment;

    private SharedPreferences mDefaultPreferences;

    private DialogFragment mDialogFragment;

    private int mYear;
    private int mMonth;
    private int mDay;

    private Realm mRealm;

    private float mMonthExpense = 0;
    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ("粉红".equals(PreferenceManager.getDefaultSharedPreferences(this).getString("default_theme", "粉红"))) {
            setTheme(R.style.AppTheme_Pink_NoActionBar);
        }
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        mDateTextView.setOnClickListener(this);

        mRealm = Realm.getInstance(this);

        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDefaultPreferences.registerOnSharedPreferenceChangeListener(this);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        updateDisplayInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDefaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateDisplayInfo() {
        String viewMode = mDefaultPreferences.getString(PreferenceKeys.KEY_VIEW_MODE, "月");
        Calendar calendar = Calendar.getInstance();
        Calendar greaterCal = Calendar.getInstance();
        Calendar lessCal = Calendar.getInstance();
        greaterCal.clear();
        lessCal.clear();
        calendar.set(mYear, mMonth, mDay);
        String yearStr = DateFormat.format("yyyy年", calendar).toString();
        switch (viewMode) {
            case "年":
                setDateString(getString(R.string.title_year), yearStr);
                greaterCal.set(Calendar.YEAR, mYear);
                lessCal.set(Calendar.YEAR, mYear + 1);
                break;
            case "日":
                setDateString(yearStr, DateFormat.format("MM月dd日", calendar).toString());
                greaterCal.set(mYear, mMonth, mDay);
                lessCal.set(mYear, mMonth, mDay + 1);
                break;
            case "周":
                setDateString(yearStr, DateFormat.format("MM月dd日", calendar).toString());
                greaterCal.set(mYear, mMonth, mDay);
                int dayOfWeek = greaterCal.get(Calendar.DAY_OF_WEEK) - 1;
                greaterCal.set(Calendar.DAY_OF_MONTH, mDay - dayOfWeek);
                lessCal.set(mYear, mMonth, mDay + 7 - dayOfWeek);
                break;
            default:
                setDateString(yearStr, DateFormat.format("MM月", calendar).toString());
                greaterCal.set(Calendar.YEAR, mYear);
                greaterCal.set(Calendar.MONTH, mMonth);
                lessCal.set(Calendar.YEAR, mYear);
                lessCal.set(Calendar.MONTH, mMonth + 1);
                break;
        }

        long greaterTime = greaterCal.getTimeInMillis();
        long lessTime = lessCal.getTimeInMillis();

        mDefaultPreferences.edit()
                .putLong(PreferenceKeys.KEY_GREATER_TIME, greaterTime)
                .putLong(PreferenceKeys.KEY_LESS_TIME, lessTime)
                .apply();

        Number income = mRealm.where(BillItem.class)
                .equalTo("income", true)
                .greaterThanOrEqualTo("dateTime", greaterTime)
                .lessThan("dateTime", lessTime)
                .findAll().sum("sum");
        setIncome(income == null ? 0 : income.floatValue());

        Number expense = mRealm.where(BillItem.class)
                .equalTo("income", false)
                .greaterThanOrEqualTo("dateTime", greaterTime)
                .lessThan("dateTime", lessTime)
                .findAll().sum("sum");
        float expenseValue = expense == null ? 0 : expense.floatValue();
        setExpense(expenseValue);

        if ("月".equals(viewMode)) {
            mMonthExpense = expenseValue;
        }

        notifyPageUpdate();
    }

    private void setDateString(String title, String content) {
        String dateString = String.format(getString(R.string.date_format), title, content);
        int start = dateString.indexOf(content);
        int end = dateString.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(dateString);
        builder.setSpan(new RelativeSizeSpan(RELATIVE_SIZE), start, end, 0);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.primary_text_dark)), start, end, 0);
        mDateTextView.setText(builder);
    }

    private void setIncome(float income) {
        String incomeStr = Float.toString(income);
        String incomeString = String.format(getString(R.string.income_format), incomeStr);
        int start = incomeString.indexOf(incomeStr);
        int end = incomeString.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(incomeString);
        builder.setSpan(new RelativeSizeSpan(RELATIVE_SIZE), start, end, 0);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.primary_text_dark)), start, end, 0);
        mIncomeTextView.setText(builder);
    }

    private void setExpense(float expense) {
        String expenseStr = Float.toString(expense);
        String expenseString = String.format(getString(R.string.expense_format), expenseStr);
        int start = expenseString.indexOf(expenseStr);
        int end = expenseString.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(expenseString);
        builder.setSpan(new RelativeSizeSpan(RELATIVE_SIZE), start, end, 0);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.primary_text_dark)), start, end, 0);
        mExpenseTextView.setText(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_add_bill) {
            AddBillActivity.start(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateDisplayInfo();

        showExpenseDialogIfNeed();
    }

    private void notifyPageUpdate() {
        if (mBillDetailsFragment != null) {
            mBillDetailsFragment.onUpgrade();
        }
        if (mBillPieChartFragment != null) {
            mBillPieChartFragment.onUpgrade();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.KEY_VIEW_MODE.equals(key) || PreferenceKeys.KEY_LAST_RESTORE_TIME.equals(key)) {
            updateDisplayInfo();
        } else if (PreferenceKeys.KEY_DEFAULT_THEME.equals(key)) {
            recreate();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mDateTextView) {
            showFragmentDialog(DIALOG_DATE, "DatePicker");
        }
    }

    private void removeFragmentDialog() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        }
    }

    private void showFragmentDialog(int dialogId, String tag) {
        removeFragmentDialog();
        mDialogFragment = new BillDialogFragment(this, dialogId);
        mDialogFragment.show(getFragmentManager(), tag);
    }

    @Override
    public Dialog onCreateCustomDialog(int dialogId) {
        if (dialogId == DIALOG_DATE) {
            return new DatePickerDialog(this, this, mYear, mMonth, mDay);
        } else {

            float limit = getMonthExpenseLimit();

            return new AlertDialog.Builder(this)
                    .setTitle(R.string.exceeding_limit_title)
                    .setMessage(mMonthExpense > limit ? getString(R.string.more_that_exceeding_limit, limit, mMonthExpense - limit)
                            : mMonthExpense < limit ? getString(R.string.less_that_exceeding_limit, limit, limit - mMonthExpense)
                            : String.format(getString(R.string.equals_to_exceeding_limit), limit))
                    .setNeutralButton(R.string.never_show_this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDefaultPreferences.edit()
                                    .putBoolean(PreferenceKeys.KEY_ENABLE_REMIND_EXCEEDING, false).apply();
                        }
                    })
                    .setPositiveButton(R.string.confirm, null)
                    .create();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        updateDisplayInfo();
    }

    private float getMonthExpenseLimit() {
        // 获取当前月支出上限
        return Float.parseFloat(mDefaultPreferences.getString(
                PreferenceKeys.KEY_REMIND_EXCEEDING, getString(R.string.remind_exceeding_default_summary)));
    }

    private float getMonthExpense() {
        // 设置月初时间
        Calendar greaterCal = Calendar.getInstance();
        greaterCal.clear();
        greaterCal.set(Calendar.YEAR, mYear);
        greaterCal.set(Calendar.MONTH, mMonth);

        // 设置月尾时间
        Calendar lessCal = Calendar.getInstance();
        lessCal.clear();
        lessCal.set(Calendar.YEAR, mYear);
        lessCal.set(Calendar.MONTH, mMonth + 1);

        // 获取月支出
        Number number = mRealm.where(BillItem.class)
                .equalTo("income", false)
                .greaterThanOrEqualTo("dateTime", greaterCal.getTimeInMillis())
                .lessThan("dateTime", lessCal.getTimeInMillis())
                .findAll().sum("sum");
        return number == null ? 0 : number.floatValue();
    }

    private void showExpenseDialogIfNeed() {
        if (mDefaultPreferences.getBoolean(PreferenceKeys.KEY_ENABLE_REMIND_EXCEEDING, false)) {
            if (!"月".equals(mDefaultPreferences.getString(PreferenceKeys.KEY_VIEW_MODE, "月"))) {
                mMonthExpense = getMonthExpense();
            }

            float limit = getMonthExpenseLimit();
            if (limit * 0.8 < mMonthExpense) {// 超出上限 80% 则提示
                showFragmentDialog(DIALOG_EXCEEDING_LIMIT, "ExpenseTips");
            }
        }
    }

    @Override
    public void onBillDeleted(int itemId) {
        updateDisplayInfo();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mBillDetailsFragment == null) {
                    mBillDetailsFragment = new BillDetailsFragment();
                }
                return mBillDetailsFragment;
            } else {
                if (mBillPieChartFragment == null) {
                    mBillPieChartFragment = new BillPieChartFragment();
                }
                return mBillPieChartFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "明细";
                case 1:
                    return "类别报表";
            }
            return null;
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
    @Override
    public void onBackPressed() {
        if (!DoubleClickExit.check()) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
