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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final float RELATIVE_SIZE = 1.3f;

    @Bind(R.id.date) TextView mDateTextView;
    @Bind(R.id.income) TextView mIncomeTextView;
    @Bind(R.id.expense) TextView mExpenseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddBillActivity.class));
            }
        });

        ButterKnife.bind(this);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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
