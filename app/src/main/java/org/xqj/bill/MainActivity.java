package org.xqj.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final float RELATIVE_SIZE = 1.3f;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tabs) TabLayout mTabLayout;
    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.date) TextView mDateTextView;
    @Bind(R.id.income_btn) TextView mIncomeTextView;
    @Bind(R.id.expense_btn) TextView mExpenseTextView;

    private BillDetailsFragment mBillDetailsFragment;
    private BillPieChartFragment mBillPieChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBillActivity.start(MainActivity.this);
            }
        });

        setDateString(2015, "3月14日");
        setIncome(13456.78f);
        setExpense(1345.78f);
    }

    private void setDateString(int year, String subStr) {
        String yearStr = Integer.toString(year);
        String dateString = String.format(getString(R.string.date_format), yearStr, subStr);
        int start = dateString.indexOf(subStr);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && (requestCode == AddBillActivity.REQUEST_EDIT_BILL || requestCode == AddBillActivity.REQUEST_NEW_BILL)) {
            notifyPageUpdate();
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
