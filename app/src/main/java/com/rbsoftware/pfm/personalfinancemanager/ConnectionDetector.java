package com.rbsoftware.pfm.personalfinancemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by burzakovskiy on 1/25/2016.
 * Class for checking network state
 */
public class ConnectionDetector {

    private final Context _context;

    /**
     * Constructor
     * @param context application context
     */
    public ConnectionDetector(Context context) {
        this._context = context;
    }

    /**
     * Check network access
     * @return true if network is available or false if not
     */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {

                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }

            }

        }
        return false;
    }
}
