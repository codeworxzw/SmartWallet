package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;

import android.widget.Toast;


import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    public static final String PREF_FILE = "PrefFile";
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

    private List<Object> params; //List FinanceDocument constructor parameters
    private String data;
    private static String userID; //unique user identifier
    private NavigationDrawerFragment drawerFragment;
    private boolean retained = false;


    public static FinanceDocumentModel financeDocumentModel;
    private FinanceDocument financeDocument;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get intent userdata from login activity
        Intent intent = getIntent();
        data = intent.getExtras().getString("name");
        userID = intent.getExtras().getString("id");
        params = new ArrayList<>();

        if (savedInstanceState == null) {
            drawerFragment = new NavigationDrawerFragment();
            drawerFragment.setArguments(intent.getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer_fragment, drawerFragment, "DrawerTag").commit();
        } else {
            drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentByTag("DrawerTag");

        }

        retained = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Document creation
                // createNewFinanceDocument(data);
                //replication start
                //financeDocumentModel.startPushReplication();
                Intent report = new Intent(MainActivity.this, ReportActivity.class);
                startActivityForResult(report, 1);
            }
        });

        boolean firstStart = Boolean.valueOf(readFromSharedPreferences(this, "firstStart", "true"));

        if (firstStart) {
            //Start service to check for alarms
            Log.d(TAG,"NotificationService is not running. Starting..");
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
        super.onSaveInstanceState(outState);
        outState.putBoolean("retained", retained);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> reportResult = data.getStringArrayListExtra("reportResult");

                params.add(PARAM_USERID, userID);
                params.add(PARAM_SALARY, getItem(reportResult, 0));
                params.add(PARAM_RENTAL_INCOME, getItem(reportResult, 1));
                params.add(PARAM_INTEREST, getItem(reportResult, 2));
                params.add(PARAM_GIFTS, getItem(reportResult, 3));
                params.add(PARAM_OTHER_INCOME, getItem(reportResult, 4));
                params.add(PARAM_TAXES, getItem(reportResult, 5));
                params.add(PARAM_MORTGAGE, getItem(reportResult, 6));
                params.add(PARAM_CREDIT_CARD, getItem(reportResult, 7));
                params.add(PARAM_UTILITIES, getItem(reportResult, 8));
                params.add(PARAM_FOOD, getItem(reportResult, 9));
                params.add(PARAM_CAR_PAYMENT, getItem(reportResult, 10));
                params.add(PARAM_PERSONAL, getItem(reportResult, 11));
                params.add(PARAM_ACTIVITIES, getItem(reportResult, 12));
                params.add(PARAM_OTHER_EXPENSE, getItem(reportResult, 13));
                createNewFinanceDocument(params);
                // financeDocumentModel.startPushReplication();


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    //HELPER METHODS

    /**
     * Parsing string to retrieve document data
     */
    private ArrayList<String> getItem(ArrayList<String> reportResult, int i) {
        ArrayList<String> item = new ArrayList<>();
        item.add(0, "0");
        item.add(1, defaultCurrency);
        item.add(2, "Never");
        for (String listItem : reportResult) {
            String[] parts = listItem.split("-");
            int position = Integer.valueOf(parts[0]);
            if (i == position) {
                item.clear();
                item.add(0, parts[2]);
                item.add(1, parts[3]);
                /* Recursion disabled in version 1.0
                    TODO enable recursion in future versions
                item.add(2,parts[4]);
                */
                item.add(2, "Never");
            }


        }

        return item;
    }





    /**
     * Creation new document from data
     *
     * @param params list of finance document fieleds
     */
    private void createNewFinanceDocument(List<Object> params) {
        financeDocument = new FinanceDocument(params);
        financeDocumentModel.createDocument(financeDocument);


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
            this.financeDocumentModel.reloadReplicationSettings();
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
