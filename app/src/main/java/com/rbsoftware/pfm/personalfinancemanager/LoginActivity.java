package com.rbsoftware.pfm.personalfinancemanager;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        ConnectionCallbacks,
        View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    public static GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;
    // Connection detector class
    ConnectionDetector mConnectionDetector;
    private boolean mIntentInProgress;
    private SignInButton signInButton;
    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        //mGoogleApiClient.connect();
        // [END build_client]

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setVisibility(View.GONE);
        // [END customize_button]
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }


    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    // [END onActivityResult]


    // [START signIn]
    private void signIn() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }

    }
    // [END signIn]


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    RC_SIGN_IN).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
            signInButton.setVisibility(View.VISIBLE);
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (mConnectionDetector.isConnectingToInternet()) {
                    signIn();
                } else {
                    showNoNetworkDialog();
                }
                break;

        }
    }

    /**
     * Shows alert dialog if no network connection
     */
    private void showNoNetworkDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle(getString(R.string.no_network_title));
        alertDialog.setMessage(getString(R.string.no_network_message));
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Fetches profile data and starts MainActivity
     */
    private void handleResult() {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person acct = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name", acct.getDisplayName());
            intent.putExtra("id", acct.getId());
            intent.putExtra("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
            intent.putExtra("photoURL", acct.getImage().getUrl());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        handleResult();


    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
}