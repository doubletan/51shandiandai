package org.xqj.bill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

import org.xqj.bill.model.BillItem;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener,
        DialogCreatable, DatePickerDialog.OnDateSetListener, BillDetailsFragment.OnBillDeletedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Set up the ViewPager with the sections adapter.
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
        setExpense(expense == null ? 0 : expense.floatValue());

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
    protected void onStop() {
        super.onStop();
        removeFragmentDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && (requestCode == AddBillActivity.REQUEST_EDIT_BILL || requestCode == AddBillActivity.REQUEST_NEW_BILL)) {
            updateDisplayInfo();
        }
    }

    private void notifyPageUpdate() {
        if (mBillDetailsFragment != null) {
            mBillDetailsFragment.onUpdate();
        }
        if (mBillPieChartFragment != null) {
            mBillPieChartFragment.onUpdate();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.KEY_VIEW_MODE.equals(key) || PreferenceKeys.KEY_LAST_RESTORE_TIME.equals(key)) {
            updateDisplayInfo();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mDateTextView) {
            showFragmentDialog(0);
        }
    }

    private void removeFragmentDialog() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        }
    }

    private void showFragmentDialog(int dialogId) {
        mDialogFragment = new BillDialogFragment(this, dialogId);
        mDialogFragment.show(getFragmentManager(), "DatePicker");
    }

    @Override
    public Dialog onCreateCustomDialog(int dialogId) {
        return new DatePickerDialog(this, this, mYear, mMonth, mDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        updateDisplayInfo();
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
}
