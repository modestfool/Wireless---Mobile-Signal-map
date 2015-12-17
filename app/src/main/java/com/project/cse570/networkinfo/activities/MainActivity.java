/**
 * An application, that runs in the background to collect logs the Cellular Network info
 *
 * @author: Basava R Kanaparthi(basava.08@gmail.com) created on 21,November,2015.
 */
package com.project.cse570.networkinfo.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.project.cse570.networkinfo.R;
import com.project.cse570.networkinfo.constants.Constants;
import com.project.cse570.networkinfo.services.MqttService;
import com.project.cse570.networkinfo.services.PeriodicLoggingService;
import com.project.cse570.networkinfo.sqlite.FeedReaderContract;
import com.project.cse570.networkinfo.sqlite.NetworkDBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;

/**
 * The execution point, whenever the app is launched, it comes here.
 */
public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    public static final String DB_PATH = String.format("/data/data/com.project.cse570.networkinfo/databases/%s", NetworkDBHelper.DATABASE_NAME);
    static final String TOGGLE_STATE = "ToggleButtonState";
    static final String LOG_TAG = "MainActivity";

    public static Context mContext;
    TelephonyManager mTelephonyManager;
    //private Button startLogging = new Button(get)
    private TextView mTextView, toolbarTextView;
    private ToggleButton mToggleLogging;
    private Button mShowMap;
    private Button mExportLogs;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private PieChart mPieChart;
    private Cursor mCursor1, mCursor2;
    private SQLiteDatabase db;
    //static final String PACKAGE_NAME =
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "OpenSans-Regular.ttf");

//        mTextView = (TextView) findViewById(R.id.textView2);
        mToggleLogging = (ToggleButton) findViewById(R.id.togglebutton_log);
        mExportLogs = (Button) findViewById(R.id.button_export_logs);
        mShowMap = (Button) findViewById(R.id.button_show_map);
//        toolbarTextView = (TextView) toolbar.findViewById(R.id.helpTextView);

//        mTextView.setTypeface(tf);
        mToggleLogging.setTypeface(tf);
        mExportLogs.setTypeface(tf);
        mShowMap.setTypeface(tf);
//        toolbarTextView.setTypeface(tf);

        setSupportActionBar(toolbar);

        NetworkDBHelper mNetworkDBHelper = new NetworkDBHelper(this);
        db = mNetworkDBHelper.getReadableDatabase();
        mCursor1 = db.rawQuery("SELECT " + FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS + " ,COUNT(*) AS cnt FROM "
                + FeedReaderContract.FeedEntry.TABLE_NAME + " GROUP BY " + FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS, null);

        mCursor2 = db.rawQuery("SELECT COUNT(*) AS cnt FROM " + FeedReaderContract.FeedEntry.TABLE_NAME, null);

        mPieChart = (PieChart) findViewById(R.id.pieChart1);
        mPieChart.setDescription("");


        mPieChart.setCenterTextTypeface(tf);
        mPieChart.setCenterText("Network Type");
        mPieChart.setCenterTextSize(11f);
        mPieChart.setCenterTextTypeface(tf);
        // radius of the center hole in percent of maximum radius
        mPieChart.setHoleRadius(45f);
        mPieChart.setTransparentCircleRadius(50f);

        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);

        int count = 4;

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        mCursor2.moveToFirst();
        int count2 = mCursor2.getInt(mCursor2.getColumnIndex("cnt"));
        Log.d(LOG_TAG + "Rows", String.valueOf(count2));

        while (mCursor1.moveToNext()) {
            int count3 = mCursor1.getInt(mCursor1.getColumnIndex("cnt"));
            String network = mCursor1.getString(mCursor1.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS));
            Log.d(LOG_TAG, mCursor1.toString());
            Log.d(LOG_TAG + " Network", network);
            Log.d(LOG_TAG + " Fraction", String.valueOf((double) count3 / count2));


            switch (network) {
                case Constants.NETWORK_TYPE_CLASS_2G:
                    xVals.add(Constants.NETWORK_TYPE_CLASS_2G);
                    //xVals.add("entry" + (0+1));
                    entries1.add(new Entry(((float) count3 / count2) * 100, 0));
                    break;
                case Constants.NETWORK_TYPE_CLASS_3G:
                    xVals.add(Constants.NETWORK_TYPE_CLASS_3G);
                    //xVals.add("entry" + (1+1));
                    entries1.add(new Entry(((float) count3 / count2) * 100, 1));
                    break;
                case Constants.NETWORK_TYPE_CLASS_4G:
                    xVals.add(Constants.NETWORK_TYPE_CLASS_4G);
                    //xVals.add("entry" + (2+1));
                    entries1.add(new Entry(((float) count3 / count2) * 100, 2));
                    break;
                case Constants.UNKNOWN:
                    xVals.add(Constants.UNKNOWN);
                    //xVals.add("entry" + (3+1));
                    entries1.add(new Entry(((float) count3 / count2) * 100, 3));
                    break;
            }
        }
        /*for(int i = 0; i < count; i++) {
            xVals.add("entry" + (i+1));

            entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
        }*/

        PieDataSet ds1 = new PieDataSet(entries1, "Cellular Networks");
        ds1.setSliceSpace(2f);
        ds1.setSelectionShift(5f);

        ds1.setColors(ColorTemplate.PASTEL_COLORS);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(12f);
        ds1.setValueFormatter(new PercentFormatter());
        //ds1.setHighlightEnabled(true);

        PieData d = new PieData(xVals, ds1);
        d.setValueTypeface(tf);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.setData(d);

//        mPieChart.highlightValues(null);
        mPieChart.invalidate();



        //Create the connection
        //Connection.createConnection("130.245.144.191", 1883, false, "ANJU", this);

        Log.d(LOG_TAG, DB_PATH);
        mContext = getApplicationContext();
        mToggleLogging.setChecked(restoreState(TOGGLE_STATE, mContext));
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent startIntent = new Intent(mContext, PeriodicLoggingService.class);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, startIntent, 0);
        final Intent mqttserviceIntent = new Intent(mContext, MqttService.class);

        mToggleLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToggleLogging.isChecked()) {
                    //startService(new Intent(getApplicationContext(), StartLoggingService.class));
                    mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, Constants.PERIODIC_INTERVAL, mPendingIntent);
                    startService(mqttserviceIntent);
                } else {
                    // stopService(new Intent(getApplicationContext(),StartLoggingService.class));
                    if (mAlarmManager != null) {
                        mAlarmManager.cancel(mPendingIntent);
                        stopService(mqttserviceIntent);
                    }
                }
            }
        });

        mExportLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File directory = new File(Environment.getExternalStorageDirectory() + "/NetworkInfo");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                if (directory.exists()) {
                    exportLogs();
                } else {
                    Log.d(LOG_TAG, "Export Directory doesn't exist and create failed");
                    Toast.makeText(getApplicationContext(), "Export Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        mShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
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

    private void exportLogs() {

        try {
            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                Date date = new Date();
                String backupDBPath = "/NetworkInfo/" + String.valueOf(date.getTime()) + "_" + NetworkDBHelper.DATABASE_NAME;
                File currentDB = new File(DB_PATH);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }


    }

   /* @Override
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
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        saveState(TOGGLE_STATE, mToggleLogging.isChecked(), mContext);
//        Connection.getConnection().getClient().unregisterResources();
//        Connection.getConnection().getClient().close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Create the connection
        //Connection.createConnection("130.245.144.191", 1883, false, "ANJU", this);
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
        if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveState(String key, Boolean value, Context mContext) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    Boolean restoreState(String key, Context mContext) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mPrefs.getBoolean(key, false);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

}
