package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.plus.Plus;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.rbsoftware.pfm.personalfinancemanager.accountsummary.AccountSummary;
import com.rbsoftware.pfm.personalfinancemanager.charts.Charts;
import com.rbsoftware.pfm.personalfinancemanager.history.History;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private static final String PREF_FILE = "PrefFile";
    public final static int PARAM_USERID = 0;
    public final static int PARAM_SALARY = 1;
    public final static int PARAM_RENTAL_INCOME = 2;
    public final static int PARAM_INTEREST = 3;
    public final static int PARAM_GIFTS = 4;
    public final static int PARAM_OTHER_INCOME = 5;
    public final static int PARAM_TAXES = 6;
    public final static int PARAM_MORTGAGE = 7;
    public final static int PARAM_CREDIT_CARD = 8;
    public final static int PARAM_UTILITIES = 9;
    public final static int PARAM_FOOD = 10;
    public final static int PARAM_CAR_PAYMENT = 11;
    public final static int PARAM_PERSONAL = 12;
    public final static int PARAM_ACTIVITIES = 13;
    public final static int PARAM_OTHER_EXPENSE = 14;

    public static String defaultCurrency;
    public static FloatingActionButton fab;

    public static Tracker mTracker;

    private List<Object> params; //List FinanceDocument constructor parameters
    private static String userID; //unique user identifier
    private AccountHeader drawerAccountHeader;
    private Drawer mMaterialDrawer;
    public static FinanceDocumentModel financeDocumentModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        AnalyticsTracker application = (AnalyticsTracker) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get intent userdata from login activity
        Intent intent = getIntent();
        userID = intent.getExtras().getString("id");
        params = new ArrayList<>();


        if (savedInstanceState == null) openFragment(1);


        // Protect creation of static variable.
        if (financeDocumentModel == null) {
            // Model needs to stay in existence for lifetime of app.
            financeDocumentModel = new FinanceDocumentModel(getApplicationContext());
            //setup index
            financeDocumentModel.setIndexManager();
        }
        financeDocumentModel.setReplicationListener(this);


        reloadReplicationSettings();

        //Updating currency rates
        reloadCurrency();

        //FAB declaration and listener
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent report = new Intent(MainActivity.this, ReportActivity.class);
                startActivityForResult(report, 1);
            }
        });

        setupNavigationDrawer(savedInstanceState, toolbar, intent);

        boolean firstStart = Boolean.valueOf(readFromSharedPreferences(this, "firstStart", "true"));

        if (firstStart) {
            //Start service to check for alarms
            Log.d(TAG, "NotificationService is not running. Starting..");
            WakefulIntentService.acquireStaticLock(this);
            this.startService(new Intent(this, NotificationService.class));
            firstStart = false;
            saveToSharedPreferences(this, "firstStart", Boolean.toString(firstStart));
        }

    }


    @Override
    protected void onResume() {

        //Reading default currency from settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultCurrency = sharedPreferences.getString("defaultCurrency", "USD");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //add the values which need to be saved from the drawer to the bundle
        outState.putAll(mMaterialDrawer.saveInstanceState(outState));
        //add the values which need to be saved from the accountHeader to the bundle
        outState.putAll(drawerAccountHeader.saveInstanceState(outState));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> reportResult = data.getStringArrayListExtra("reportResult");

                params.add(PARAM_USERID, userID);
                params.add(PARAM_SALARY, Utils.getItem(reportResult, 0));
                params.add(PARAM_RENTAL_INCOME, Utils.getItem(reportResult, 1));
                params.add(PARAM_INTEREST, Utils.getItem(reportResult, 2));
                params.add(PARAM_GIFTS, Utils.getItem(reportResult, 3));
                params.add(PARAM_OTHER_INCOME, Utils.getItem(reportResult, 4));
                params.add(PARAM_TAXES, Utils.getItem(reportResult, 5));
                params.add(PARAM_MORTGAGE, Utils.getItem(reportResult, 6));
                params.add(PARAM_CREDIT_CARD, Utils.getItem(reportResult, 7));
                params.add(PARAM_UTILITIES, Utils.getItem(reportResult, 8));
                params.add(PARAM_FOOD, Utils.getItem(reportResult, 9));
                params.add(PARAM_CAR_PAYMENT, Utils.getItem(reportResult, 10));
                params.add(PARAM_PERSONAL, Utils.getItem(reportResult, 11));
                params.add(PARAM_ACTIVITIES, Utils.getItem(reportResult, 12));
                params.add(PARAM_OTHER_EXPENSE, Utils.getItem(reportResult, 13));
                createNewFinanceDocument(params);
                // financeDocumentModel.startPushReplication();


            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult


    //HELPER METHODS

    /**
     * Creates navigation drawer
     *
     * @param savedInstanceState of activity
     * @param toolbar            of activity
     * @param intent             received after login
     */
    private void setupNavigationDrawer(Bundle savedInstanceState, Toolbar toolbar, Intent intent) {

        //Set up image loading through Picasso
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }


        });
        // Create the AccountHeader
        drawerAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)

                .withHeaderBackground(R.drawable.account_header_background)
                .addProfiles(
                        new ProfileDrawerItem().withName(intent.getStringExtra("name"))
                                .withIcon(intent.getStringExtra("photoURL"))
                )
                .withSelectionListEnabledForSingleProfile(false)


                .withProfileImagesClickable(false)
                .withSavedInstance(savedInstanceState)
                .build();

        //Build navigation drawer
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(drawerAccountHeader)
                .withDelayDrawerClickEvent(0)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getResources().getStringArray(R.array.drawer_menu)[0]).withIcon(GoogleMaterial.Icon.gmd_dashboard),
                        new PrimaryDrawerItem().withName(getResources().getStringArray(R.array.drawer_menu)[1]).withIcon(GoogleMaterial.Icon.gmd_pie_chart),
                        new PrimaryDrawerItem().withName(getResources().getStringArray(R.array.drawer_menu)[2]).withIcon(GoogleMaterial.Icon.gmd_history),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getResources().getStringArray(R.array.drawer_menu)[3]).withSelectable(false).withIcon(GoogleMaterial.Icon.gmd_settings),
                        new PrimaryDrawerItem().withName(getResources().getStringArray(R.array.drawer_menu)[4]).withSelectable(false).withIcon(GoogleMaterial.Icon.gmd_exit_to_app)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 5) {
                            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivityForResult(i, MainActivity.RESULT_OK);
                            return true;
                        } else if (position == 6) {
                            signout();
                            return true;

                        } else {
                            openFragment(position);
                        }
                        return false;
                    }
                })

                .withSavedInstance(savedInstanceState);

        //make multipane layout for tablets in landscape orientation
        if (Utils.isTablet(this) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            drawerBuilder.withTranslucentStatusBar(false).withTranslucentNavigationBar(false);

            mMaterialDrawer = drawerBuilder.buildView();
            ((ViewGroup) findViewById(R.id.nav_tablet)).addView(mMaterialDrawer.getSlider());
        } else {
            mMaterialDrawer = drawerBuilder.build();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mMaterialDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            }
        }

    }

    /**
     * Opens fragment
     *
     * @param position fragment position
     */
    private void openFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 1:

                if (fragmentManager.findFragmentByTag("AccountSummary") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("AccountSummary")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new AccountSummary(), "AccountSummary").commit();
                }

                break;
            case 2:

                if (fragmentManager.findFragmentByTag("Charts") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("Charts")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new Charts(), "Charts").commit();
                }
                break;
            case 3:

                if (fragmentManager.findFragmentByTag("History") != null) {
                    //if the fragment exists, show it.
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("History")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new History(), "History").commit();
                }
                break;


        }


    }

    /**
     * Sign out from application
     */
    private void signout() {
        if (LoginActivity.mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
            LoginActivity.mGoogleApiClient.disconnect();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    /**
     * Creation new document from data
     *
     * @param params list of finance document fields
     */
    private void createNewFinanceDocument(List<Object> params) {
        FinanceDocument financeDocument = new FinanceDocument(params);
        financeDocumentModel.createDocument(financeDocument);
        //Save tag when new doc created
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDate = df.format(c.getTime());
        saveToSharedPreferences(this, "createdDate", currentDate);

    }


    /**
     * Getter userId
     *
     * @return uesrId
     */
    public static String getUserId() {
        return userID;
    }

    /**
     * Restarting replication settings
     */

    private void reloadReplicationSettings() {
        try {
            financeDocumentModel.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(getApplicationContext().toString(), "Unable to construct remote URI from configuration", e);
            Toast.makeText(getApplicationContext(),
                    "Replication error",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called by TasksModel when it receives a replication complete callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationComplete() {

        Toast.makeText(getApplicationContext(),
                "Replication completed",
                Toast.LENGTH_LONG).show();

    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationError() {
        Log.i(getApplicationContext().toString(), "error()");

        Toast.makeText(getApplicationContext(),
                "Replication error",
                Toast.LENGTH_LONG).show();

    }


    /**
     * Fetches last currency rates from internet
     */
    private void reloadCurrency() {
        ConnectionDetector mConnectionDetector = new ConnectionDetector(getApplicationContext());
        if (mConnectionDetector.isConnectingToInternet()) {
            Calendar c = Calendar.getInstance(TimeZone.getDefault());
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDate = df.format(c.getTime());
            String updatedDate = readFromSharedPreferences(this, "updatedDate", "");
            if (!updatedDate.equals(currentDate) || (financeDocumentModel.getCurrencyDocument(FinanceDocumentModel.CURRENCY_ID) == null)) {
                Log.d(TAG, "Updating currency rates");
                new CurrencyConversion(this).execute();


            } else {
                Log.d(TAG, "Currency rates were updated today");
            }
        } else {
            Log.e(TAG, "Can't update currency rates. No internet connection");
        }
    }

    /**
     * Static methods for saving to sharedpreferences
     *
     * @param context   application context
     * @param prefName  name variable
     * @param prefValue value
     */
    public static void saveToSharedPreferences(Context context, String prefName, String prefValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    /**
     * Static methods for reading from sharedpreferences
     *
     * @param context      application context
     * @param prefName     name variable
     * @param defaultValue default value
     */
    public static String readFromSharedPreferences(Context context, String prefName, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(prefName, defaultValue);
    }


}
