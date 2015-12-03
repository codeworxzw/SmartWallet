package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    public static final String PREF_FILE = "PrefFile";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mDrawerList;
    private String[] mListItems;
    private GoogleApiClient mGoogleApiClient;
    private TextView mUserName;
    private ImageView mUserPhoto;
    private View mDrawerView;
    private int fragmentPos;
    private Fragment mFragment;
    private FragmentManager FM;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            // [START configure_signin]
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            // [END configure_signin]

            // [START build_client]
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();
            // [END build_client]
        }

    }



    /* Called whenever we call invalidateOptionsMenu() */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        // Inflate the layout for this fragment
        fragmentPos=Integer.valueOf(ReadFromSharedPreferences(getActivity(), "fragmentPos", "0"));



        return mDrawerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerLayout = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mUserName = (TextView) mDrawerView.findViewById(R.id.tv_user_name);
        mUserPhoto = (ImageView) mDrawerView.findViewById(R.id.user_photo);
        mUserName.setText(getArguments().getString("name"));
        String photoURL = getArguments().getString("photoURL");
        new ImageLoad(photoURL,mUserPhoto);
        Log.d("photo",photoURL+" ");

        mDrawerList = (ListView) mDrawerView.findViewById(R.id.navigation_drawer_listview);
        mListItems= getResources().getStringArray(R.array.drawer_menu);

        mDrawerList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mListItems));
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
        openFragment(fragmentPos);
    }



    public static void SaveToSharedPreferences(Context context, String prefName, String prefValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    public static String ReadFromSharedPreferences(Context context, String prefName, String defaultValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(prefName,defaultValue);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            mDrawerLayout.closeDrawers();

           openFragment(position);
        }
    }

    public void openFragment(int position){
        if(position == 5){
            signout();
        }
        else {

            switch (position) {
                case 0:
                    mFragment= new AccountSummary();
                    break;
                case 1:
                    mFragment= new Charts();
                    break;


            }
            fragmentPos = position;
            SaveToSharedPreferences(getActivity(),"fragmentPos", Integer.toString(position));
            FM = getFragmentManager();

            FM.beginTransaction().replace(R.id.fragment_container, mFragment).commit();

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public void signout(){
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        }
                    });
        }
    }

}
