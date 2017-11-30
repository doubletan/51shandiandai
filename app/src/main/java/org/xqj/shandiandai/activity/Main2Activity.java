package org.xqj.shandiandai.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.umeng.analytics.MobclickAgent;

import org.xqj.shandiandai.R;
import org.xqj.shandiandai.fragment.AboutFragment;
import org.xqj.shandiandai.fragment.FristFragment;
import org.xqj.shandiandai.utils.DoubleClickExit;


public class Main2Activity extends AppCompatActivity {
    private BottomNavigationBar mNavigationView;

    private FragmentManager mFragmentManager;

    private Fragment mCurrentFragment;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, Main2Activity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initWeb();

    }

    private void initWeb() {
        mCurrentFragment=new FristFragment();
        mFragmentManager=getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.app_item, mCurrentFragment).commit();

        mNavigationView = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mNavigationView
                .setBarBackgroundColor("#3F51B5")
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT)
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp,"主页").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.ic_find_replace_white_24dp, "帮助").setActiveColorResource(R.color.teal))
                .initialise();
        mNavigationView.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switchMenu(getFragmentName(position));
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }


    private String getFragmentName(int menuId) {
        switch (menuId) {
            case 0:
                return FristFragment.class.getName();
            case 1:
                return AboutFragment.class.getName();
            default:
                return null;
        }
    }
    private void switchMenu(String fragmentName) {

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentName);

        if (fragment != null) {
            if (fragment == mCurrentFragment) return;

            mFragmentManager.beginTransaction().show(fragment).commit();
        } else {
            fragment = Fragment.instantiate(this, fragmentName);
            mFragmentManager.beginTransaction().add(R.id.app_item, fragment, fragmentName).commit();
        }

        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commit();
        }
        mCurrentFragment = fragment;
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
