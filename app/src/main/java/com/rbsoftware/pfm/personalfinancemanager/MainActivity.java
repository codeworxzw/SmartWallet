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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    private String data;
    private static String userID; //unique user identifier



    public static FinanceDocumentModel doc;



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

        NavigationDrawerFragment drawerFragment = new NavigationDrawerFragment();
        drawerFragment.setArguments(intent.getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer_fragment,drawerFragment).commit();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Protect creation of static variable.
        if (doc == null) {
            // Model needs to stay in existence for lifetime of app.
            doc = new FinanceDocumentModel(getApplicationContext());
        }
        doc.setReplicationListener(this);
        doc.setIndexManager();

        reloadReplicationSettings();

        //FAB declaration and listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Document creation
                // createNewFinanceDocument(data);
                //replication start
                //doc.startPushReplication();
                Intent report = new Intent(MainActivity.this, ReportActivity.class);
                startActivityForResult(report, 1);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("incomeData");
                createNewFinanceDocument(result);


            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    //HELPER METHODS

    //Creation new document from data
    private void createNewFinanceDocument(String data) {
        FinanceDocument t = new FinanceDocument(data);
        doc.createDocument(t);

    }


    // Getter userId
    public static  String getUserId(){
        return userID;
    }

    //Restarting replication settings
    private void reloadReplicationSettings() {
        try {
            this.doc.reloadReplicationSettings();
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
