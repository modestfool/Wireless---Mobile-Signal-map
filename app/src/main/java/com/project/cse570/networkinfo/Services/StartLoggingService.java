/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 22,November,2015.
 */
package com.project.cse570.networkinfo.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class StartLoggingService extends Service {
    private TelephonyManager mTelephonyManager;
    private CellStateListener mCellStateListener;
    @Override
    public void onCreate(){
        super.onCreate();
        mTelephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mCellStateListener = new CellStateListener(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        mTelephonyManager.listen(mCellStateListener,
                        PhoneStateListener.LISTEN_CELL_INFO |
                        PhoneStateListener.LISTEN_CELL_LOCATION |
                        PhoneStateListener.LISTEN_SERVICE_STATE |
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
       mTelephonyManager.listen(mCellStateListener,PhoneStateListener.LISTEN_NONE);
    }
}
