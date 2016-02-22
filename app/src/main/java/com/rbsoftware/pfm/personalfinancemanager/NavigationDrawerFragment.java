package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Holds navigation drawer elements
 */
public class NavigationDrawerFragment extends Fragment {


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private int fragmentPos;
    private Fragment mFragment;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }




    /* Called whenever we call invalidateOptionsMenu() */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        // Inflate the layout for this fragment
        if(savedInstanceState == null){
            fragmentPos =0;
        }
        else{
            fragmentPos = savedInstanceState.getInt("fragmentPos");
        }

        return mDrawerView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView mUserName = (TextView) mDrawerView.findViewById(R.id.tv_user_name);
        ImageView mUserPhoto = (ImageView) mDrawerView.findViewById(R.id.user_photo);
        mUserName.setText(getArguments().getString("name", getArguments().getString("email")));
        String photoURL = getArguments().getString("photoURL", null);
        if (photoURL != null) {
            Picasso.with(getContext()).load(photoURL).transform(new CircleTransform()).into(mUserPhoto);
        } else {
            Picasso.with(getContext()).load(R.drawable.user_photo_256px).into(mUserPhoto);

        }

        ListView mDrawerList = (ListView) mDrawerView.findViewById(R.id.navigation_drawer_listview);
        String[] mListItems = getResources().getStringArray(R.array.drawer_menu);
        int[] mListImages = {
                R.drawable.ic_bill_grey_24dp,
                R.drawable.ic_statistics_grey_24dp,
                R.drawable.ic_history_grey_24dp,
                R.drawable.ic_settings_grey_24dp,
                R.drawable.ic_exit_grey_24dp};

        mDrawerList.setAdapter(new DrawerListAdapter(getActivity(), mListImages, mListItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getActivity().invalidateOptionsMenu();
            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        //Setting up default fragment


        mToolbar.setTitle(mListItems[fragmentPos]);
        if (mFragment == null) {   //RetainInstanceState applied. Checking if mFragment is not created yet
            openFragment(fragmentPos);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("fragmentPos",fragmentPos);
        super.onSaveInstanceState(outState);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            mDrawerLayout.closeDrawers();

            if (position == 3) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(i, MainActivity.RESULT_OK);
            } else if (position == 4) {
                signout();

            } else {
                openFragment(position);
            }
        }
    }

    private void openFragment(int position) {
        switch (position) {
            case 0:
                mFragment = new AccountSummary();
                break;
            case 1:
                mFragment = new Charts();
                break;
            case 2:
                mFragment = new History();
                break;


        }
        fragmentPos = position;

        FragmentManager FM = getFragmentManager();

        FM.beginTransaction().replace(R.id.fragment_container, mFragment).commit();

    }


    private void signout() {
        if (LoginActivity.mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
            LoginActivity.mGoogleApiClient.disconnect();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

}
