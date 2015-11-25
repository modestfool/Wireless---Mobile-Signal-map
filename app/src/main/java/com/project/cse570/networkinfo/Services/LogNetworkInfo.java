/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 24,November,2015.
 */
package com.project.cse570.networkinfo.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.project.cse570.networkinfo.SQLite.FeedReaderContract;
import com.project.cse570.networkinfo.SQLite.NetworkDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class LogNetworkInfo implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG ="LogNetworkInfo";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    static Location mCurrentLocation;
    Date mLastUpdateTime;

    public long LogNetworkDetails(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient(context);
        mGoogleApiClient.connect();
        int networkType = mTelephonyManager.getNetworkType();
        String networkOperatorName = mTelephonyManager.getNetworkOperatorName();
        String networkOperator = mTelephonyManager.getNetworkOperator();
        String simOperatorName = mTelephonyManager.getSimOperatorName();
        String simOperator = mTelephonyManager.getSimOperator();

        Log.d(LOG_TAG,String.valueOf(networkType));
        Log.d(LOG_TAG,networkOperatorName);
//        Log.d(LOG_TAG,networkOperator);
        Log.d(LOG_TAG,simOperatorName);
        Log.d(LOG_TAG,simOperator);
        Log.d(LOG_TAG,simOperator);

        String networkTypeString;

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkTypeString = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkTypeString = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkTypeString = "4G";
                break;
            default:
                networkTypeString = "Unknown";
        }
        return insertNetworkEntry(networkTypeString, context);
    }

    static long insertNetworkEntry(String network_type, Context context){
        ContentValues mContentValues = new ContentValues();
        NetworkDBHelper mNetworkDBHelper = new NetworkDBHelper(context);
        SQLiteDatabase db = mNetworkDBHelper.getWritableDatabase();
        Log.d(LOG_TAG, network_type);
        mContentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, network_type);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, mContentValues); ;
        Log.d(LOG_TAG,String.valueOf(newRowId));
        db.close();
        return newRowId;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApi Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
//           Log.d(LOG_TAG,String.valueOf(mLastLocation.getLatitude())+ ", " +
//                            String.valueOf(mLastLocation.getLongitude()));
            Log.d(LOG_TAG, mLastLocation.toString());
        }

        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "GoogleApi Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG,"GoogleApi Connection Failed");
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
       // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLastUpdateTime = new Date();
        mLastUpdateTime.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
        if(mCurrentLocation!=null){
            Log.d(LOG_TAG,mCurrentLocation.toString() + sdf.format(mLastUpdateTime));
        }

    }
}
