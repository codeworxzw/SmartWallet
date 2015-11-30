package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private String data;
    private GoogleApiClient mGoogleApiClient;

    private static FinanceDocumentModel doc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        drawerFragment.setUp(R.id.navigation_drawer_fragment, (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Get intent userdata from login activity
        Intent intent = getIntent();
        data =intent.getExtras().getString("name");



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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]



        // Protect creation of static variable.
        if (doc == null) {
            // Model needs to stay in existence for lifetime of app.
            this.doc = new FinanceDocumentModel(this.getApplicationContext());
        }
        this.doc.setReplicationListener(this);
        reloadReplicationSettings();

        //FAB declaration and listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Document creation
                createNewFinanceDocument(data);
                //replication start
                doc.startPushReplication();
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /* Called whenever we call invalidateOptionsMenu() */




    public void signout(){
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                            // [START_EXCLUDE]
                            // updateUI(false);
                            // [END_EXCLUDE]
                        }
                    });
        }
    }

    //HELPER METHODS

    //Creation new document from data
    private void createNewFinanceDocument(String data) {
        FinanceDocument t = new FinanceDocument(data);
        doc.createDocument(t);

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
