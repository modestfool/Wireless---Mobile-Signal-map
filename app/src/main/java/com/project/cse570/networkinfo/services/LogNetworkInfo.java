/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 24,November,2015.
 */
package com.project.cse570.networkinfo.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.project.cse570.networkinfo.activities.Connection;
import com.project.cse570.networkinfo.sqlite.FeedReaderContract;
import com.project.cse570.networkinfo.sqlite.NetworkDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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

    public static final String UNKNOWN = "UNKNOWN";
    //Data Activity
    public static final String DATA_ACTIVITY_DORMANT = "Dormant";
    public static final String DATA_ACTIVITY_IN = "In";
    public static final String DATA_ACTIVITY_INOUT = "InOut";
    private static final String LOG_TAG = "LogNetworkInfo";
    private static final String DATA_ACTIVITY_OUT = "Out";
    private static final String DATA_ACTIVITY_NONE = "None";

    // Call State
    private static final String CALL_STATE_IDLE = TelephonyManager.EXTRA_STATE_IDLE;
    private static final String CALL_STATE_OFFHOOK = TelephonyManager.EXTRA_STATE_OFFHOOK;
    private static final String CALL_STATE_RINGING = TelephonyManager.EXTRA_STATE_RINGING;

    // Data State
    private static final String DATA_CONNECTED = "Connected";
    private static final String DATA_DISCONNECTED = "Disconnected";
    private static final String DATA_CONNECTING = "Connecting";
    private static final String DATA_SUSPENDED = "Suspended";
    // Cell Location
    private static final String CELL_LOCATION_TYPE = "CellLocationType";
    private static final String CELL_GSM = "GSM";
    private static final String CELL_LOCATION_KEY_LAC = "LAC";
    private static final String CELL_LOCATION_KEY_CID = "CID";
    private static final String CELL_LOCATION_KEY_PSC = "PSC";
    private static final String CELL_CDMA = "CDMA";
    private static final String CELL_LOCATION_KEY_BSID = "BaseStationId";
    private static final String CELL_LOCATION_KEY_BSLATITUTE = "BaseStationLatitude";
    private static final String CELL_LOCATION_KEY_BSLONGITUDE = "BaseStationLongitude";
    private static final String CELL_LOCATION_KEY_NETWORKID = "NetworkId";
    private static final String CELL_LOCATION_KEY_SYSTEMID = "SystemId";

    private static final String KEY_UNKNOWN_TYPE = "toString";

    // Cell Info
    private static final String CELL_INFO_KEY_TYPE = "CellType";
    private static final String CELL_INFO_VALUE_GSM = "GSM";
    private static final String CELL_INFO_VALUE_CDMA = "CDMA";
    private static final String CELL_INFO_VALUE_LTE = "LTE";
    private static final String CELL_INFO_VALUE_WCDMA = "WCDMA";

    private static final String CELL_INFO_KEY_IS_REGISTERED = "IsRegistered";
    private static final String CELL_INFO_KEY_TIMESTAMP_VALUE = "TimeStamp";
    // GSM CellIdentity
    private static final String CELL_INFO_KEY_GSM_MCC = "GSM_Mcc";
    private static final String CELL_INFO_KEY_GSM_MNC = "GSM_Mnc";
    private static final String CELL_INFO_KEY_GSM_LAC = "GSM_Lac";
    private static final String CELL_INFO_KEY_GSM_CID = "GSM_Cid";
    // GSM CellSignalStrength
    private static final String CELL_INFO_KEY_GSM_ASU_LEVEL = "GSM_AsuLevel";
    private static final String CELL_INFO_KEY_GSM_DBM = "GSM_Dbm";
    private static final String CELL_INFO_KEY_GSM_LEVEL = "GSM_SignalLevel";
    private static final String CELL_INFO_KEY_GSM_SIGNAL_STRENGTH = "GSM_SignalStrength";
    private static final String CELL_INFO_KEY_GSM_BER = "GSM_BER";
    // CDMA CellIdentity
    private static final String CELL_INFO_KEY_CDMA_BSID = "CDMA_BaseStationId";
    private static final String CELL_INFO_KEY_CDMA_BSLATITUTE = "CDMA_BaseStationLatitude";
    private static final String CELL_INFO_KEY_CDMA_BSLONGITUDE = "CDMA_BaseStationLongitude";
    private static final String CELL_INFO_KEY_CDMA_NETWORKID = "CDMA_NetworkId";
    private static final String CELL_INFO_KEY_CDMA_SYSTEMID = "CDMA_SystemId";
    // CDMA CellSignalStrength
    private static final String CELL_INFO_KEY_CDMA_ASU_LEVEL = "CDMA_AsuLevel";
    private static final String CELL_INFO_KEY_CDMA_RSSI_DBM = "CDMA_RSSI_Dbm";
    private static final String CELL_INFO_KEY_CDMA_ECIO = "CDMA_Ecio";
    private static final String CELL_INFO_KEY_CDMA_LEVEL = "CDMA_SignalLevel";
    private static final String CELL_INFO_KEY_CDMA_DBM = "CDMA_Dbm";
    private static final String CELL_INFO_KEY_CDMA_EVDO_DBM = "CDMA_EvdoDbm";
    private static final String CELL_INFO_KEY_CDMA_EVDO_ECIO = "CDMA_EvdoEcio";
    private static final String CELL_INFO_KEY_CDMA_EVDO_LEVEL = "CDMA_EvdoLevel";
    private static final String CELL_INFO_KEY_CDMA_EVDO_SNR = "CDMA_EvdoSnr";
    private static final String CELL_INFO_KEY_CDMA_LEVEL2 = "SignalLevel";

    // LTE CellIdentity
    private static final String CELL_INFO_KEY_LTE_CI = "LTE_Ci";
    private static final String CELL_INFO_KEY_LTE_MCC = "LTE_Mcc";
    private static final String CELL_INFO_KEY_LTE_MNC = "LTE_Mnc";
    private static final String CELL_INFO_KEY_LTE_PCI = "LTE_Pci";
    private static final String CELL_INFO_KEY_LTE_TAC = "LTE_Tac";
    // LTE CellSignalStrength
    private static final String CELL_INFO_KEY_LTE_ASU_LEVEL = "LTE_AsuLevel";
    private static final String CELL_INFO_KEY_LTE_DBM = "LTE_Dbm";
    private static final String CELL_INFO_KEY_LTE_LEVEL = "LTE_SignalLevel";
    private static final String CELL_INFO_KEY_LTE_TIMING_ADVANCE = "LTE_TimingAdvance";

    // WCDMA CellIdentity
    private static final String CELL_INFO_KEY_WCDMA_CID = "WCDMA_Cid";
    private static final String CELL_INFO_KEY_WCDMA_LAC = "WCDMA_Lac";
    private static final String CELL_INFO_KEY_WCDMA_MCC = "WCDMA_Mcc";
    private static final String CELL_INFO_KEY_WCDMA_MNC = "WCDMA_Mnc";
    private static final String CELL_INFO_KEY_WCDMA_PSC = "WCDMA_Psc";
    // WCDMA CellSignalStrength
    private static final String CELL_INFO_KEY_WCDMA_ASU_LEVEL = "WCDMA_AsuLevel";
    private static final String CELL_INFO_KEY_WCDMA_DBM = "WCDMA_Dbm";
    private static final String CELL_INFO_KEY_WCDMA_LEVEL = "WCDMA_SignalLevel";

    // Wifi Info
    private static final String WIFI_INFO_BSSID = "Wifi_BSSID";
    private static final String WIFI_INFO_FREQUENCY = "Wifi_Frequency";
    private static final String WIFI_INFO_HIDDEN_SSID = "Wifi_HiddenSSID";
    private static final String WIFI_INFO_IP_ADDRESS = "Wifi_IpAddress";
    private static final String WIFI_INFO_LINK_SPEED = "Wifi_LinkSpeed";
    private static final String WIFI_INFO_MAC_ADDRESS = "Wifi_MacAddress";
    private static final String WIFI_INFO_NETWORK_ID = "Wifi_NetworkId";
    private static final String WIFI_INFO_RSSI = "Wifi_RSSI";
    private static final String WIFI_INFO_SSID = "Wifi_SSID";
    // Wifi Scanresult
    private static final String WIFI_INFO_CHANNEL_WIDTH = "Wifi_ChannelWidth";
    private static final String WIFI_INFO_LEVEL = "Wifi_Level";
    private static final String WIFI_INFO_VENUE_NAME = "Wifi_VenueName";
    private static final String WIFI_INFO_OPERATOR_FRIENDLY_NAME = "Wifi_OpearatorFriendlyName";
    private static final String WIFI_INFO_SCAN_RESULTS = "Wifi_ScanResults";

    static Location mCurrentLocation;
    static Context context;
    static Map<String, Object> rowNetworkEntryMap = new HashMap<String, Object>();
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Date mLastUpdateTime;
    long newRowId = -1L;

    static long insertNetworkEntry(ContentValues rowEntryContentValues, Context context) {
        NetworkDBHelper mNetworkDBHelper = new NetworkDBHelper(context);
        SQLiteDatabase db = mNetworkDBHelper.getWritableDatabase();
        //Log.d(LOG_TAG, network_type);
        //mContentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, network_type);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, rowEntryContentValues);

        String topic = "networkInfo/data";
//        JSONObject jo = new JSONObject(rowEntryContentValues);
        // Gson gson = new Gson();
//        String jo = gson.toJson(rowEntryContentValues);
        //String jo = gson.toJson(rowNetworkEntryMap);
        JSONObject jo = new JSONObject(rowNetworkEntryMap);
        //Connection.getConnection().publish(topic,rowEntryContentValues.toString().getBytes(),2,true);
        if (Connection.getConnection() != null)
            Connection.getConnection().publish(topic, jo.toString().getBytes(), 2, true);
//        Log.d(LOG_TAG, String.valueOf((rowEntryContentValues.getAsByteArray("dummy"))));
        db.close();
        return newRowId;
    }


    public long LogNetworkDetails(Context context) {

        LogNetworkInfo.context = context;
        buildGoogleApiClient(context);
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            //ContentValues rowEntryContentValues = getNetworkInfo(context);
            //return insertNetworkEntry(rowEntryContentValues, context);
        } else {
            Log.d(LOG_TAG, "Location services TurnedOff - Not Logging");
            //return -1L;
        }
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

        ContentValues rowEntryContentValues = getNetworkInfo(context);
        newRowId = insertNetworkEntry(rowEntryContentValues, context);
        Log.d(LOG_TAG, String.valueOf(newRowId));
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
//        SimpleDateFormat sdf;
//        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
//        if(mCurrentLocation!=null){
//            Log.d(LOG_TAG,mCurrentLocation.toString() + sdf.format(mLastUpdateTime));
//        }
    }

    ContentValues getNetworkInfo(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        ContentValues rowNetworkEntry = new ContentValues();

        Date date = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.US);
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH", Locale.US);

        String deviceId;
        String deviceSoftwareVersion;
        long timestampEpoch;
        String timestampDate;
        String timestampDay;
        String timestampHour;

        String networkTypeString;
        String networkTypeClass;
        String networkOperatorName;
        String networkOperatorCode;
        JSONObject cellLocation;

        String call_state;
        String data_activity;
        String data_state;

        String latitude = "";
        String longitude = "";
        String accuracy = "";

        //String cell_info = "";
        JSONObject cell_info = new JSONObject();
        String neighboring_cell_info = "";
        JSONArray neighboringJsonArray = new JSONArray();
        if (mTelephonyManager.getDeviceId() != null) {
            Log.d(LOG_TAG, mTelephonyManager.getDeviceId());
            deviceId = mTelephonyManager.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(LOG_TAG, deviceId);
        }
        deviceSoftwareVersion = mTelephonyManager.getDeviceSoftwareVersion();
        timestampEpoch = date.getTime();
        timestampDate = sdfDate.format(date);
        timestampDay = sdfDay.format(date);
        timestampHour = sdfHour.format(date);

//        String simOperatorName = mTelephonyManager.getSimOperatorName();
//        String simOperator = mTelephonyManager.getSimOperator();
        networkOperatorName = mTelephonyManager.getNetworkOperatorName();
        networkOperatorCode = mTelephonyManager.getNetworkOperator();

        cellLocation = getCellLocation(mTelephonyManager);

        int data_activity_status = mTelephonyManager.getDataActivity();
        switch (data_activity_status) {
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                data_activity = DATA_ACTIVITY_DORMANT;
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                data_activity = DATA_ACTIVITY_IN;
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                data_activity = DATA_ACTIVITY_INOUT;
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                data_activity = DATA_ACTIVITY_OUT;
                break;
            case TelephonyManager.DATA_ACTIVITY_NONE:
                data_activity = DATA_ACTIVITY_NONE;
                break;
            default:
                data_activity = UNKNOWN;
        }


        int call_state_int = mTelephonyManager.getCallState();
        switch (call_state_int) {
            case TelephonyManager.CALL_STATE_IDLE:
                call_state = CALL_STATE_IDLE;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                call_state = CALL_STATE_OFFHOOK;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                call_state = CALL_STATE_RINGING;
                break;
            default:
                call_state = UNKNOWN;
        }

        int data_state_int = mTelephonyManager.getDataState();
        switch (data_state_int) {
            case TelephonyManager.DATA_CONNECTED:
                data_state = DATA_CONNECTED;
                break;
            case TelephonyManager.DATA_CONNECTING:
                data_state = DATA_CONNECTING;
                break;
            case TelephonyManager.DATA_SUSPENDED:
                data_state = DATA_SUSPENDED;
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                data_state = DATA_DISCONNECTED;
                break;
            default:
                data_state = UNKNOWN;
        }

        if (mCurrentLocation != null) {
            latitude = String.valueOf(mCurrentLocation.getLatitude());
            longitude = String.valueOf(mCurrentLocation.getLongitude());
            accuracy = String.valueOf(mCurrentLocation.getAccuracy());
        } else if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            accuracy = String.valueOf(mLastLocation.getAccuracy());
        }
        List<CellInfo> allCellInfo = mTelephonyManager.getAllCellInfo();
        if (allCellInfo.size() > 0) {
            cell_info = getCellInfo(allCellInfo.get(0));//.toString();
            if (allCellInfo.size() > 1) {
//                JSONArray neighboringJsonArray = new JSONArray();
                for (CellInfo neighborCellInfo : allCellInfo.subList(1, allCellInfo.size())) {
                    neighboringJsonArray.put(getCellInfo(neighborCellInfo));
                }
                neighboring_cell_info = neighboringJsonArray.toString();
                Log.d(LOG_TAG, neighboring_cell_info);
//            for (int i = 1; i < allCellInfo.size(); i++) {
//                neighboring_cell_info += " | " + allCellInfo.get(i).toString();
//            }
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
                networkTypeString = UNKNOWN;
                networkTypeClass = UNKNOWN;
        }

        JSONObject wifiInfo = getWifiInfo(context);
        JSONObject wifiScanResult = getWifiScanResult(context);
//        Map rowNetworkEntryMap = new HashMap();
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_ID, deviceId);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_SOFTWARE_VERSION, deviceSoftwareVersion);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_EPOCH, timestampEpoch);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DATE, timestampDate);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DAY, timestampDay);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_HOUR, timestampHour);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS, networkTypeClass);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, networkTypeString);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_NAME, networkOperatorName);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_CODE, networkOperatorCode);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_LOCATION, cellLocation.toString());

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CALL_STATE, call_state);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_ACTIVITY, data_activity);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_STATE, data_state);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ACCURACY, accuracy);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_INFO, cell_info.toString());
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NEIGHBORING_CELLS_INFO, neighboring_cell_info);

        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CURRENT_WIFI_INFO, wifiInfo.toString());
        rowNetworkEntry.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SCAN_RESULT, wifiScanResult.toString());

        /*
        * Map Values to be sent over the network
        * */
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_ID, deviceId);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_SOFTWARE_VERSION, deviceSoftwareVersion);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_EPOCH, timestampEpoch);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DATE, timestampDate);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DAY, timestampDay);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_HOUR, timestampHour);

        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS, networkTypeClass);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE, networkTypeString);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_NAME, networkOperatorName);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_CODE, networkOperatorCode);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_LOCATION, cellLocation);

        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CALL_STATE, call_state);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_ACTIVITY, data_activity);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_STATE, data_state);

        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ACCURACY, accuracy);

        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_INFO, cell_info);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NEIGHBORING_CELLS_INFO, neighboringJsonArray);

        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CURRENT_WIFI_INFO, wifiInfo);
        rowNetworkEntryMap.put(FeedReaderContract.FeedEntry.COLUMN_NAME_WIFI_SCAN_RESULT, wifiScanResult);

        /*
        * End of MapValues
        *
         */



//        Log.d(LOG_TAG, rowNetworkEntry.toString());
//        Log.d(LOG_TAG,String.valueOf(networkType));
//        Log.d(LOG_TAG,networkOperatorName);
//        Log.d(LOG_TAG,networkOperatorCode);
//        Log.d(LOG_TAG,simOperatorName);
//        Log.d(LOG_TAG,simOperator);

        return rowNetworkEntry;
    }

    JSONObject getCellLocation(TelephonyManager mTelephonyManager) {
        CellLocation location = mTelephonyManager.getCellLocation();
        Map<String, Map<String, Serializable>> networkTypeMap = new HashMap<>();
        Map<String, java.io.Serializable> networkValuesMap = new HashMap<>();

        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            networkValuesMap.put(CELL_LOCATION_TYPE, CELL_GSM);
            networkValuesMap.put(CELL_LOCATION_KEY_CID, gcLoc.getCid());
            networkValuesMap.put(CELL_LOCATION_KEY_LAC, gcLoc.getLac());
            networkValuesMap.put(CELL_LOCATION_KEY_PSC, gcLoc.getPsc());
            networkTypeMap.put(CELL_GSM, networkValuesMap);
//            Log.i(LOG_TAG,
//                    "onCellLocationChanged: GsmCellLocation "
//                            + gcLoc.toString());
//            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getCid "
//                    + gcLoc.getCid());
//            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getLac "
//                    + gcLoc.getLac());
//            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getPsc"
//                    + gcLoc.getPsc()); // Requires min API 9
        } else {
            if (location instanceof CdmaCellLocation) {
                CdmaCellLocation ccLoc = (CdmaCellLocation) location;
                networkValuesMap.put(CELL_LOCATION_TYPE, CELL_CDMA);
                networkValuesMap.put(CELL_LOCATION_KEY_BSID, ccLoc.getBaseStationId());
                networkValuesMap.put(CELL_LOCATION_KEY_BSLATITUTE, ccLoc.getBaseStationLatitude());
                networkValuesMap.put(CELL_LOCATION_KEY_BSLONGITUDE, ccLoc.getBaseStationLongitude());
                networkValuesMap.put(CELL_LOCATION_KEY_NETWORKID, ccLoc.getNetworkId());
                networkValuesMap.put(CELL_LOCATION_KEY_SYSTEMID, ccLoc.getSystemId());
                networkTypeMap.put(CELL_CDMA, networkValuesMap);

//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation "
//                                + ccLoc.toString());
//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationId "
//                                + ccLoc.getBaseStationId());
//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
//                                + ccLoc.getBaseStationLatitude());
//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
//                                + ccLoc.getBaseStationLongitude());
//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation getNetworkId "
//                                + ccLoc.getNetworkId());
//                Log.i(LOG_TAG,
//                        "onCellLocationChanged: CdmaCellLocation getSystemId "
//                                + ccLoc.getSystemId());
            } else {
                networkValuesMap.put(CELL_LOCATION_TYPE, UNKNOWN);
                networkValuesMap.put(KEY_UNKNOWN_TYPE, location.toString());
                networkTypeMap.put(UNKNOWN, networkValuesMap);
//                Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
            }
        }
        JSONObject jo = new JSONObject(networkTypeMap);
        Log.d(LOG_TAG, jo.toString());
        //return jo.toString();
//        Gson gson = new Gson();
//        return gson.toJson(networkTypeMap); // Returns the string
        return jo;
    }

    JSONObject getCellInfo(CellInfo cellInfo) {
        String cell_info;
        Map<String, Map<Object, Serializable>> networkTypeMap = new HashMap<>();
        Map<Object, Serializable> networkValuesMap = new HashMap<>();
        // Common Fields
        networkValuesMap.put(CELL_INFO_KEY_IS_REGISTERED, (cellInfo.isRegistered()) ? "True" : "False");
        networkValuesMap.put(CELL_INFO_KEY_TIMESTAMP_VALUE, cellInfo.getTimeStamp());
        if (cellInfo instanceof CellInfoCdma) {
            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
            // CellIdentity
            networkValuesMap.put(CELL_INFO_KEY_CDMA_BSID, (cellInfoCdma.getCellIdentity().getBasestationId()));
            networkValuesMap.put(CELL_INFO_KEY_CDMA_BSLATITUTE, cellInfoCdma.getCellIdentity().getLatitude());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_BSLONGITUDE, cellInfoCdma.getCellIdentity().getLongitude());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_NETWORKID, cellInfoCdma.getCellIdentity().getNetworkId());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_SYSTEMID, cellInfoCdma.getCellIdentity().getSystemId());
            // CellSignalStrength
            networkValuesMap.put(CELL_INFO_KEY_CDMA_ASU_LEVEL, cellInfoCdma.getCellSignalStrength().getAsuLevel());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_RSSI_DBM, cellInfoCdma.getCellSignalStrength().getCdmaDbm());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_ECIO, cellInfoCdma.getCellSignalStrength().getCdmaEcio());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_LEVEL, cellInfoCdma.getCellSignalStrength().getCdmaLevel());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_DBM, cellInfoCdma.getCellSignalStrength().getDbm());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_EVDO_DBM, cellInfoCdma.getCellSignalStrength().getEvdoDbm());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_EVDO_ECIO, cellInfoCdma.getCellSignalStrength().getEvdoEcio());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_EVDO_LEVEL, cellInfoCdma.getCellSignalStrength().getEvdoLevel());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_EVDO_SNR, cellInfoCdma.getCellSignalStrength().getEvdoSnr());
            networkValuesMap.put(CELL_INFO_KEY_CDMA_LEVEL2, cellInfoCdma.getCellSignalStrength().getLevel());
            networkTypeMap.put(CELL_INFO_VALUE_CDMA, networkValuesMap);
        } else if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            // CellIdentity
            networkValuesMap.put(CELL_INFO_KEY_GSM_CID, cellInfoGsm.getCellIdentity().getCid());
            networkValuesMap.put(CELL_INFO_KEY_GSM_LAC, cellInfoGsm.getCellIdentity().getLac());
            networkValuesMap.put(CELL_INFO_KEY_GSM_MCC, cellInfoGsm.getCellIdentity().getMcc());
            networkValuesMap.put(CELL_INFO_KEY_GSM_MNC, cellInfoGsm.getCellIdentity().getMnc());
            // CellSignalStrength
            networkValuesMap.put(CELL_INFO_KEY_GSM_ASU_LEVEL, cellInfoGsm.getCellSignalStrength().getAsuLevel());
            networkValuesMap.put(CELL_INFO_KEY_GSM_DBM, cellInfoGsm.getCellSignalStrength().getDbm());
            networkValuesMap.put(CELL_INFO_KEY_GSM_LEVEL, cellInfoGsm.getCellSignalStrength().getLevel());

            networkTypeMap.put(CELL_INFO_VALUE_GSM, networkValuesMap);
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            // CellIdentity
            networkValuesMap.put(CELL_INFO_KEY_LTE_CI, cellInfoLte.getCellIdentity().getCi());
            networkValuesMap.put(CELL_INFO_KEY_LTE_MCC, cellInfoLte.getCellIdentity().getMcc());
            networkValuesMap.put(CELL_INFO_KEY_LTE_MNC, cellInfoLte.getCellIdentity().getMnc());
            networkValuesMap.put(CELL_INFO_KEY_LTE_PCI, cellInfoLte.getCellIdentity().getPci());
            networkValuesMap.put(CELL_INFO_KEY_LTE_TAC, cellInfoLte.getCellIdentity().getTac());
            // CellSignalStrength
            networkValuesMap.put(CELL_INFO_KEY_LTE_ASU_LEVEL, cellInfoLte.getCellSignalStrength().getAsuLevel());
            networkValuesMap.put(CELL_INFO_KEY_LTE_DBM, cellInfoLte.getCellSignalStrength().getDbm());
            networkValuesMap.put(CELL_INFO_KEY_LTE_LEVEL, cellInfoLte.getCellSignalStrength().getLevel());
            networkValuesMap.put(CELL_INFO_KEY_LTE_TIMING_ADVANCE, cellInfoLte.getCellSignalStrength().getTimingAdvance());

            networkTypeMap.put(CELL_INFO_VALUE_LTE, networkValuesMap);
        } else if (cellInfo instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            // CellIdentity
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_CID, cellInfoWcdma.getCellIdentity().getCid());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_LAC, cellInfoWcdma.getCellIdentity().getLac());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_MCC, cellInfoWcdma.getCellIdentity().getMcc());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_MNC, cellInfoWcdma.getCellIdentity().getMnc());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_PSC, cellInfoWcdma.getCellIdentity().getPsc());
            // CellSignalStrength
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_ASU_LEVEL, cellInfoWcdma.getCellSignalStrength().getAsuLevel());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_DBM, cellInfoWcdma.getCellSignalStrength().getDbm());
            networkValuesMap.put(CELL_INFO_KEY_WCDMA_LEVEL, cellInfoWcdma.getCellSignalStrength().getLevel());

            networkTypeMap.put(CELL_INFO_VALUE_WCDMA, networkValuesMap);
        } else {
            networkValuesMap.put(KEY_UNKNOWN_TYPE, cellInfo.toString());
            networkTypeMap.put(UNKNOWN, networkValuesMap);
        }
        JSONObject jo = new JSONObject(networkTypeMap);
        Log.d(LOG_TAG, jo.toString());
//        Log.d(LOG_TAG, jo.toString());
        /*Gson gson = new Gson();
        String networkTypeJson = gson.toJson(networkTypeMap);
        JSONObject jo = null;
        try {
            jo = new JSONObject(networkTypeJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return jo;
    }

    JSONObject getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Map<String, Serializable> wifiInfoMap = new HashMap<>();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            wifiInfoMap.put(WIFI_INFO_BSSID, wifiInfo.getBSSID());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                wifiInfoMap.put(WIFI_INFO_FREQUENCY, wifiInfo.getFrequency() + WifiInfo.FREQUENCY_UNITS);
            }
            wifiInfoMap.put(WIFI_INFO_HIDDEN_SSID, (wifiInfo.getHiddenSSID()) ? "True" : "False");
            wifiInfoMap.put(WIFI_INFO_IP_ADDRESS, wifiInfo.getIpAddress());
            wifiInfoMap.put(WIFI_INFO_LINK_SPEED, wifiInfo.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
            wifiInfoMap.put(WIFI_INFO_MAC_ADDRESS, wifiInfo.getMacAddress());
            wifiInfoMap.put(WIFI_INFO_NETWORK_ID, wifiInfo.getNetworkId());
            wifiInfoMap.put(WIFI_INFO_RSSI, wifiInfo.getRssi());
            String ssid = wifiInfo.getSSID().replace("\"", "");
            Log.d(LOG_TAG, ssid);
            wifiInfoMap.put(WIFI_INFO_SSID, ssid);
//            Gson gson = new Gson();
//            return gson.toJson(wifiInfoMap);
        }
//        else {
//            return "";
//        }
        JSONObject jo = new JSONObject(wifiInfoMap);
        Log.d(LOG_TAG, jo.toString());
        return jo;
    }

    JSONObject getWifiScanResult(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Map<String, Map<String, Object>> wifiScanMap = new HashMap<>();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        JSONArray jsonWifiArray = new JSONArray();
        if (scanResults.size() > 0) {
            for (ScanResult s : scanResults) {
                Map<String, Object> wifiInfoMap = new HashMap<>();
                wifiInfoMap.put(WIFI_INFO_BSSID, s.BSSID);
                wifiInfoMap.put(WIFI_INFO_SSID, s.SSID);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    wifiInfoMap.put(WIFI_INFO_CHANNEL_WIDTH, s.channelWidth);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    wifiInfoMap.put(WIFI_INFO_FREQUENCY, s.frequency + WifiInfo.FREQUENCY_UNITS);
                } else {
                    wifiInfoMap.put(WIFI_INFO_FREQUENCY, s.frequency + "MHz");
                }
                wifiInfoMap.put(WIFI_INFO_LEVEL, s.level);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    wifiInfoMap.put(WIFI_INFO_OPERATOR_FRIENDLY_NAME, s.operatorFriendlyName);
                    wifiInfoMap.put(WIFI_INFO_VENUE_NAME, s.venueName);
                }
                wifiScanMap.put(s.SSID, wifiInfoMap);
//                jsonWifiArray.put(new JSONObject(wifiInfoMap));
            }
        } else {
            if (wifiManager.isScanAlwaysAvailable()) {
                wifiManager.startScan();
            }
        }
//        Gson gson = new Gson();
//        return gson.toJson(wifiScanMap);
        JSONObject jo = new JSONObject(wifiScanMap);
        Log.d(LOG_TAG, jo.toString());
        return jo;
    }
}
