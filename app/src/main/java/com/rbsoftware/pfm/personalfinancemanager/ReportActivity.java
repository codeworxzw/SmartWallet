package com.rbsoftware.pfm.personalfinancemanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ReportActivity extends AppCompatActivity {

   private Fragment mFragment;
   private ViewPager mPager;
    private FragmentManager FM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        FM = getSupportFragmentManager();
        mPager= (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new CollectionPagerAdapter(FM));
    }

    private class CollectionPagerAdapter extends FragmentStatePagerAdapter {
        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                mFragment = new IncomeFragment();
            }
            if(position == 1){
                mFragment = new ExpenseFragment();
            }
            return mFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = new String();
            if(position == 0){
                title = getResources().getString(R.string.income);
            }
            if(position == 1){
                title =getResources().getString(R.string.expense); ;
            }
            return  title;
        }
    }
}
