package com.rbsoftware.pfm.personalfinancemanager;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    public static GoogleApiClient mGoogleApiClient;

    // Connection detector class
    private ConnectionDetector mConnectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                /*.addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)*/
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //mGoogleApiClient.connect();
        // [END build_client]

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        if (signInButton != null) {
            signInButton.setOnClickListener(this);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(gso.getScopeArray());
        }
        // [END customize_button]
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleResult(googleSignInResult);
                }
            });
        }

    }


    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleResult(result);
            }


            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    // [END onActivityResult]


    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    // [END signIn]


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        mGoogleApiClient.reconnect();


    }


    @Override
    public void onClick(View v) {

        if (mConnectionDetector.isConnectingToInternet()) {
            signIn();
        } else {
            showNoNetworkDialog();
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
    private void handleResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            if (Boolean.valueOf(MainActivity.readFromSharedPreferences(this, "firstStart", "true"))) {
                showSelectCurrencyDialog(result.getSignInAccount());
            } else {
                finishSignIn(result.getSignInAccount());
            }
        }


    }

    /**
     * Starts MainActivity and passes user's data there
     */
    private void finishSignIn(GoogleSignInAccount acct) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name", acct.getDisplayName());
        intent.putExtra("id", acct.getId());
        intent.putExtra("email", acct.getEmail());
        if (acct.getPhotoUrl() != null) {
            intent.putExtra("photoURL", acct.getPhotoUrl());
        }
        startActivity(intent);
        finish();
    }

    /**
     * Shows dialog for default currency selection
     */
    private void showSelectCurrencyDialog(final GoogleSignInAccount acct) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.default_currency))
                .setCancelable(false)
                .setSingleChoiceItems(
                        new ArrayAdapter<>(this,
                                R.layout.select_default_currency_list_item,
                                getResources().getStringArray(R.array.report_activity_currency_spinner)),
                        0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("defaultCurrency", getResources().getStringArray(R.array.report_activity_currency_spinner)[which]);
                                editor.apply();
                                dialog.dismiss();
                                finishSignIn(acct);

                            }
                        }
                )
                .show();
    }


}