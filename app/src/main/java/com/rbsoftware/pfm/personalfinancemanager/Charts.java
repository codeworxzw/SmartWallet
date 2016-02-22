package com.rbsoftware.pfm.personalfinancemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Charts fragment that holds child fragments
 **/
public class Charts extends Fragment {

    private Fragment mFragment;
    private ViewPager mPager;
    private FragmentManager FM;
    private CollectionPagerAdapter adapter;

    public Charts() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FM = getChildFragmentManager();
        if(adapter == null) {
            adapter = new CollectionPagerAdapter(FM);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getResources().getStringArray(R.array.drawer_menu)[1]);
        if(mPager == null) {
            mPager = (ViewPager) getActivity().findViewById(R.id.pager);
        }
        mPager.setAdapter(adapter);

        MainActivity.fab.hide();
    }



    private class CollectionPagerAdapter extends FragmentStatePagerAdapter {
        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mFragment = new IncomeExpenseChart();
            }
            if (position == 1) {
                mFragment = new TrendsChart();
            }
            return mFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title="";
            if (position == 0) {
                title = getResources().getString(R.string.overview);
            }
            if (position == 1) {
                title = getResources().getString(R.string.trends);

            }
            return title;
        }
    }


}
