package com.rbsoftware.pfm.personalfinancemanager;


import android.content.Intent;
import android.os.Bundle;
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
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    List<String> params; //List FinanceDocument constructor parameters
    private String data;
    private static String userID; //unique user identifier



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
        data =intent.getExtras().getString("name");
        userID = "111";
        params =new ArrayList<>();

        NavigationDrawerFragment drawerFragment = new NavigationDrawerFragment();
        drawerFragment.setArguments(intent.getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer_fragment,drawerFragment).commit();



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

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                ArrayList<String> reportResult=data.getStringArrayListExtra("reportResult");

                Log.d("reportResult", reportResult.toString()+"");
                params.add(0, userID);
                params.add(1, getItem(reportResult, 0));
                params.add(2, getItem(reportResult, 1));
                params.add(3, getItem(reportResult, 2));
                params.add(4, getItem(reportResult, 3));
                params.add(5, getItem(reportResult, 4));
                params.add(6, getItem(reportResult, 5));
                params.add(7, getItem(reportResult, 6));
                params.add(8, getItem(reportResult, 7));
                params.add(9, getItem(reportResult, 8));
                params.add(10, getItem(reportResult, 9));
                params.add(11, getItem(reportResult, 10));
                params.add(12, getItem(reportResult, 11));
                params.add(13, getItem(reportResult, 12));
                params.add(14, getItem(reportResult, 13));
                createNewFinanceDocument(params);
               // financeDocumentModel.startPushReplication();


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private String getItem(ArrayList<String> reportResult, int i) {
        String item="0";
        for(String listItem: reportResult){
            String[] parts = listItem.split("-");
            int position = Integer.valueOf(parts[0]);
            if(i == position) {
                item = parts[2];
                Log.d("List", position + " " + item);
            }
        }

        return item;
    }


    //HELPER METHODS

    //Creation new document from data
    private void createNewFinanceDocument(List<String> params) {
        financeDocument = new FinanceDocument(params);
        financeDocumentModel.createDocument(financeDocument);


    }


    // Getter userId
    public static  String getUserId(){
        return userID;
    }

    //Restarting replication settings
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

}
