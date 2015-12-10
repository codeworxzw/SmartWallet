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
        userID = intent.getExtras().getString("id");
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
                String salary=data.getStringExtra("salary");
                params.add(0, userID);
                params.add(1, salary);
                params.add(2, "");
                params.add(3, "");
                params.add(4, "");
                params.add(5, "");
                params.add(6, "");
                params.add(7, "");
                params.add(8, "");
                params.add(9, "");
                params.add(10, "");
                params.add(11, "");
                params.add(12, "");
                params.add(13, "");
                params.add(14, "");
                createNewFinanceDocument(params);


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


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
