/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 24,November,2015.
 */
package com.project.cse570.networkinfo.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.project.cse570.networkinfo.SQLite.FeedReaderContract;
import com.project.cse570.networkinfo.SQLite.NetworkDBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class LogNetworkInfo implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /*Network Type Strings */
    // 2G
    public static final String NETWORK_TYPE_CLASS_2G = "2G";
    public static final String NETWORK_TYPE_GPRS = "GPRS";
    public static final String NETWORK_TYPE_EDGE = "EDGE";
    public static final String NETWORK_TYPE_CDMA = "CDMA";
    public static final String NETWORK_TYPE_1xRTT = "1xRTT";
    public static final String NETWORK_TYPE_IDEN = "IDEN";
    // 3G
    public static final String NETWORK_TYPE_CLASS_3G = "3G";
    public static final String NETWORK_TYPE_UMTS = "UMTS";
    public static final String NETWORK_TYPE_EVDO_0 = "EVDO_0";
    public static final String NETWORK_TYPE_EVDO_A = "EVDO_A";
    public static final String NETWORK_TYPE_HSDPA = "HSDPA";
    public static final String NETWORK_TYPE_HSUPA = "HSUPA";
    public static final String NETWORK_TYPE_HSPA = "HSPA";
    public static final String NETWORK_TYPE_EVDO_B = "EVDO_B";
    public static final String NETWORK_TYPE_EHRPD = "EHRPD";
    public static final String NETWORK_TYPE_HSPAP = "HSPAP";
    // 4G
    public static final String NETWORK_TYPE_CLASS_4G = "4G";
    public static final String NETWORK_TYPE_LTE = "LTE";
    public static final String NETWORK_TYPE_UNKNOWN = "UNKNOWN";
    private static final String LOG_TAG ="LogNetworkInfo";
    static Location mCurrentLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Date mLastUpdateTime;

    static long insertNetworkEntry(ContentValues rowEntryContentValues, Context context) {
        NetworkDBHelper mNetworkDBHelper = new NetworkDBHelper(context);
        SQLiteDatabase db = mNetworkDBHelper.getWritableDatabase();
        //Log.d(LOG_TAG, network_type);
        //mContentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, network_type);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, rowEntryContentValues);
        Log.d(LOG_TAG, String.valueOf(newRowId));
        db.close();
        return newRowId;
    }

    public long LogNetworkDetails(Context context) {

        buildGoogleApiClient(context);
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            ContentValues rowEntryContentValues = getNetworkInfo(context);
            return insertNetworkEntry(rowEntryContentValues, context);
        } else {
            Log.d(LOG_TAG, "Location Services TurnedOff - Not Logging");
            return -1L;
        }
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
        Log.d(LOG_TAG, connectionResult.getErrorMessage());
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

    ContentValues getNetworkInfo(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        ContentValues rowNetworkEntry = new ContentValues();

        Date date = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.US);
        SimpleDateFormat sdfHour = new SimpleDateFormat("hh", Locale.US);

        String deviceSoftwareVersion;
        long timestampEpoch;
        String timestampDate;
        String timestampDay;
        String timestampHour;

        String networkTypeString;
        String networkTypeClass;
        String networkOperatorName;
        String networkOperatorCode;
        String cellLocation;

        String latitude;
        String longitude;
        String accuracy;

        String cell_info = "";
        String neighboring_cell_info = "";

        deviceSoftwareVersion = mTelephonyManager.getDeviceSoftwareVersion();
        timestampEpoch = date.getTime();
        timestampDate = sdfDate.format(date);
        timestampDay = sdfDay.format(date);
        timestampHour = sdfHour.format(date);

//        String simOperatorName = mTelephonyManager.getSimOperatorName();
//        String simOperator = mTelephonyManager.getSimOperator();
        networkOperatorName = mTelephonyManager.getNetworkOperatorName();
        networkOperatorCode = mTelephonyManager.getNetworkOperator();

        cellLocation = mTelephonyManager.getCellLocation().toString();

        latitude = String.valueOf(mCurrentLocation.getLatitude());
        longitude = String.valueOf(mCurrentLocation.getLongitude());
        accuracy = String.valueOf(mCurrentLocation.getAccuracy());

        List<CellInfo> allCellInfo = mTelephonyManager.getAllCellInfo();
        if (allCellInfo.size() > 0) {
            cell_info = allCellInfo.get(0).toString();
            for (int i = 1; i < allCellInfo.size(); i++) {
                neighboring_cell_info += " | " + allCellInfo.get(i).toString();
            }
        }
        int networkType = mTelephonyManager.getNetworkType();

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkTypeString = NETWORK_TYPE_GPRS;
                networkTypeClass = NETWORK_TYPE_CLASS_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkTypeString = NETWORK_TYPE_EDGE;
                networkTypeClass = NETWORK_TYPE_CLASS_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkTypeString = NETWORK_TYPE_CDMA;
                networkTypeClass = NETWORK_TYPE_CLASS_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkTypeString = NETWORK_TYPE_1xRTT;
                networkTypeClass = NETWORK_TYPE_CLASS_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkTypeString = NETWORK_TYPE_IDEN;
                networkTypeClass = NETWORK_TYPE_CLASS_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkTypeString = NETWORK_TYPE_UMTS;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkTypeString = NETWORK_TYPE_EVDO_0;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkTypeString = NETWORK_TYPE_EVDO_A;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkTypeString = NETWORK_TYPE_HSDPA;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkTypeString = NETWORK_TYPE_HSUPA;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkTypeString = NETWORK_TYPE_HSPA;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkTypeString = NETWORK_TYPE_EVDO_B;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                networkTypeString = NETWORK_TYPE_EHRPD;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkTypeString = NETWORK_TYPE_HSPAP;
                networkTypeClass = NETWORK_TYPE_CLASS_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkTypeString = NETWORK_TYPE_LTE;
                networkTypeClass = NETWORK_TYPE_CLASS_4G;
                break;
            default:
                networkTypeString = NETWORK_TYPE_UNKNOWN;
                networkTypeClass = NETWORK_TYPE_UNKNOWN;
        }

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_SOFTWARE_VERSION, deviceSoftwareVersion);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_EPOCH, timestampEpoch);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DATE, timestampDate);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DAY, timestampDay);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_HOUR, timestampHour);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS, networkTypeClass);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, networkTypeString);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_NAME, networkOperatorName);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_CODE, networkOperatorCode);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_LOCATION, cellLocation);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ACCURACY, accuracy);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_INFO, cell_info);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NEIGHBORING_CELLS_INFO, neighboring_cell_info);

        Log.d(LOG_TAG, rowNetworkEntry.toString());
//        Log.d(LOG_TAG,String.valueOf(networkType));
//        Log.d(LOG_TAG,networkOperatorName);
//        Log.d(LOG_TAG,networkOperatorCode);
//        Log.d(LOG_TAG,simOperatorName);
//        Log.d(LOG_TAG,simOperator);

        return rowNetworkEntry;
    }
}
