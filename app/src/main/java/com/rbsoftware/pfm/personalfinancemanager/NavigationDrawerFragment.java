package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    public static final String PREF_FILE = "PrefFile";
    public static final String USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mLearnedDrawer;
    private boolean mFromSavedInstanseState;
    private View mFragmentId;
    private RecyclerView mRecyclerView;
    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLearnedDrawer= Boolean.valueOf(ReadFromSharedPreferences(getActivity(),USER_LEARNED_DRAWER,"false"));
        if(savedInstanceState != null){
            mFromSavedInstanseState=true;
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_naigation_drawer, container, false);
       // mRecyclerView= (RecyclerView) layout.findViewById(R.id.recycler_left);
        return layout;
    }

    public void setUp(int fragmentId, android.support.v4.widget.DrawerLayout drawerLayout, android.support.v7.widget.Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mFragmentId = getActivity().findViewById(fragmentId);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(mLearnedDrawer){
                    mLearnedDrawer=true;
                    SaveToSharedPreferences(getActivity(), USER_LEARNED_DRAWER, Boolean.toString(mLearnedDrawer));
                }
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        if(mLearnedDrawer && !mFromSavedInstanseState){
            mDrawerLayout.openDrawer(mFragmentId);

        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void SaveToSharedPreferences(Context context, String prefName, String prefValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    public static String ReadFromSharedPreferences(Context context, String prefName, String defaultfValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(prefName,defaultfValue);
    }
}
