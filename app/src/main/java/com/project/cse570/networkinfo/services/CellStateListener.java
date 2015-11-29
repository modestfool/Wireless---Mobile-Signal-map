/**
 * @author: Basava R. Kanaparthi,(basava.08@gmail.com) created on 22,November,2015.
 */
package com.project.cse570.networkinfo.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.project.cse570.networkinfo.sqlite.NetworkDBHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CellStateListener extends PhoneStateListener {

    static String LOG_TAG = "CellStateListener";
    Context mContext;
    NetworkDBHelper mNetworkDBHelper;
    SQLiteDatabase db;
    LogNetworkInfo mLogNetworkInfo;

    public CellStateListener(Context context) {
        mContext = context;
        mNetworkDBHelper = new NetworkDBHelper(mContext);
        db = mNetworkDBHelper.getWritableDatabase();
        mLogNetworkInfo = new LogNetworkInfo();
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        Log.i(LOG_TAG, "CellInfo Changed");
        Toast.makeText(mContext, "CellInfo Changed", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, String.valueOf(mLogNetworkInfo.LogNetworkDetails(mContext)));
//        int networkType = mTelephonyManager.getNetworkType();
//        Log.i(LOG_TAG,String.valueOf(networkType));
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);

        Toast.makeText(mContext, "CellLocation Changed", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, String.valueOf(mLogNetworkInfo.LogNetworkDetails(mContext)));

//        int networkType = mTelephonyManager.getNetworkType();
//        Log.i(LOG_TAG, String.valueOf(networkType));

        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            Log.i(LOG_TAG,
                    "onCellLocationChanged: GsmCellLocation "
                            + gcLoc.toString());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getCid "
                    + gcLoc.getCid());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getLac "
                    + gcLoc.getLac());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getPsc"
                    + gcLoc.getPsc()); // Requires min API 9
        } else {
            if (location instanceof CdmaCellLocation) {
                CdmaCellLocation ccLoc = (CdmaCellLocation) location;
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation "
                                + ccLoc.toString());
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationId "
                                + ccLoc.getBaseStationId());
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
                                + ccLoc.getBaseStationLatitude());
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
                                + ccLoc.getBaseStationLongitude());
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation getNetworkId "
                                + ccLoc.getNetworkId());
                Log.i(LOG_TAG,
                        "onCellLocationChanged: CdmaCellLocation getSystemId "
                                + ccLoc.getSystemId());
            } else {
                Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
            }
        }
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);

        Log.i(LOG_TAG, String.valueOf(mLogNetworkInfo.LogNetworkDetails(mContext)));

        Log.i(LOG_TAG, "onServiceStateChanged: " + serviceState.toString());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaLong "
                + serviceState.getOperatorAlphaLong());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaShort "
                + serviceState.getOperatorAlphaShort());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorNumeric "
                + serviceState.getOperatorNumeric());
        Log.i(LOG_TAG, "onServiceStateChanged: getIsManualSelection "
                + serviceState.getIsManualSelection());
        Log.i(LOG_TAG,
                "onServiceStateChanged: getRoaming "
                        + serviceState.getRoaming());

        switch (serviceState.getState()) {
            case ServiceState.STATE_IN_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_IN_SERVICE");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                break;
            case ServiceState.STATE_POWER_OFF:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_POWER_OFF");
                break;
        }
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);


//        int networkType = mTelephonyManager.getNetworkType();
//        Log.i(LOG_TAG, String.valueOf(networkType));

        Log.i(LOG_TAG, "onSignalStrengthsChanged: " + signalStrength);
        Log.i(LOG_TAG, String.valueOf(mLogNetworkInfo.LogNetworkDetails(mContext)));

        if (signalStrength.isGsm()) {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmBitErrorRate "
                    + signalStrength.getGsmBitErrorRate());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmSignalStrength "
                    + signalStrength.getGsmSignalStrength());
        } else if (signalStrength.getCdmaDbm() > 0) {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaDbm "
                    + signalStrength.getCdmaDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaEcio "
                    + signalStrength.getCdmaEcio());
        } else {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoDbm "
                    + signalStrength.getEvdoDbm());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoEcio "
                    + signalStrength.getEvdoEcio());
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoSnr "
                    + signalStrength.getEvdoSnr());
        }

        // Reflection code starts from here
        try {
            Method[] methods = android.telephony.SignalStrength.class
                    .getMethods();
            for (Method mthd : methods) {
                if (mthd.getName().equals("getLteSignalStrength")
                        || mthd.getName().equals("getLteRsrp")
                        || mthd.getName().equals("getLteRsrq")
                        || mthd.getName().equals("getLteRssnr")
                        || mthd.getName().equals("getLteCqi")) {
                    Log.i(LOG_TAG,
                            "onSignalStrengthsChanged: " + mthd.getName() + " "
                                    + mthd.invoke(signalStrength));
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        /* Reflection code ends here */
    }
}
