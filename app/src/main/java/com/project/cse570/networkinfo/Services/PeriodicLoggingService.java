/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 24,November,2015.
 */
package com.project.cse570.networkinfo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 11/24/2015.
 */
public class PeriodicLoggingService extends BroadcastReceiver{
    static final String LOG_TAG = "PeriodicLoggingService";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogNetworkInfo mLogNetworkInfo = new LogNetworkInfo();
        Toast.makeText(context, "Periodic Intent Received", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "Received Intent");
        mLogNetworkInfo.LogNetworkDetails(context);
    }
}
