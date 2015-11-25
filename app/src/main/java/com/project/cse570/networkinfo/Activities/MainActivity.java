/**
 * An application, that runs in the background to collect logs the Cellular Network info
 * @author: Basava R Kanaparthi(basava.08@gmail.com)
 */
package com.project.cse570.networkinfo.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.project.cse570.networkinfo.R;
import com.project.cse570.networkinfo.Services.PeriodicLoggingService;

/**
 * The execution point, whenever the app is launched, it comes here.
 */
public class MainActivity extends AppCompatActivity {

    //private Button startLogging = new Button(get)
    private TextView mTextView;
    private ToggleButton mToggleLogging;
    private Button mStopLogging;
    private Button mGetLastLogs;
    TelephonyManager mTelephonyManager;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    public static Context mContext;
    static final String TOGGLE_STATE = "ToggleButtonState";
    static final String LOG_TAG = "MainActivity";
    static final long PERIODIC_INTERVAL = 15*1000; //15 seconds

    //static final String PACKAGE_NAME =
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.textView2);
        mToggleLogging = (ToggleButton)findViewById(R.id.togglebutton_log);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();
        mToggleLogging.setChecked(restoreState(TOGGLE_STATE,mContext));
        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent startIntent = new Intent(mContext, PeriodicLoggingService.class);
        mPendingIntent = PendingIntent.getBroadcast(mContext,0,startIntent,0);

        mToggleLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mToggleLogging.isChecked())
                {
                    //startService(new Intent(getApplicationContext(), StartLoggingService.class));
                    mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,0, PERIODIC_INTERVAL,mPendingIntent);
                }
                else if(!mToggleLogging.isChecked())
                {
                   // stopService(new Intent(getApplicationContext(),StartLoggingService.class));
                    if(mAlarmManager!=null)
                    {
                        mAlarmManager.cancel(mPendingIntent);
                    }
                }
            }
        });

        mTelephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        //CellInfoGsm cellinfogsm = (CellInfoGsm)mTelephonyManager.getAllCellInfo().get(0);
        //CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
        //int cellSignalStrengthGsmDbm = cellSignalStrengthGsm.getDbm();
        //int networkType = mTelephonyManager.getNetworkType();
        //mTextView.setText(String.valueOf(networkType) + String.valueOf(cellSignalStrengthGsmDbm));
//        mTelephonyManager.listen(new CellStateListener(getApplicationContext()),
//                        PhoneStateListener.LISTEN_CELL_INFO |
//                        PhoneStateListener.LISTEN_CELL_LOCATION |
//                        PhoneStateListener.LISTEN_SERVICE_STATE |
//                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        Log.d(LOG_TAG, String.valueOf(mToggleLogging.isChecked()));
        //savedInstanceState.putBoolean(TOGGLE_STATE, mToggleLogging.isChecked());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, String.valueOf(savedInstanceState.getBoolean(TOGGLE_STATE)));
        //mToggleLogging.setChecked(savedInstanceState.getBoolean(TOGGLE_STATE, false));
    }

    @Override
    public void onPause(){
        super.onPause();
        saveState(TOGGLE_STATE,mToggleLogging.isChecked(),mContext);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveState(String key, Boolean value, Context mContext){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean(key,value);
        mEditor.apply();
    }

    Boolean restoreState(String key, Context mContext){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mPrefs.getBoolean(key,false);
    }
}
