package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ReportActivity extends AppCompatActivity implements IncomeFragment.IncomeCommunicator{

   private Fragment mFragment;
   private ViewPager mPager;
    private FragmentManager FM;
    private String salary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FM = getSupportFragmentManager();
        mPager= (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new CollectionPagerAdapter(FM));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void respond(String text) {
        salary = text;
        Intent result =new Intent();
        result.putExtra("salary", salary);
        setResult(RESULT_OK,result);
        finish();
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
