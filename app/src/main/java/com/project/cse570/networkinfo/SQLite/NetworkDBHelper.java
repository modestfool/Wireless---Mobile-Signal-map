/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 22,November,2015.
 */
package com.project.cse570.networkinfo.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 11/22/2015.
 */
public class NetworkDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NetworkInfo.db";
    private static final String LOG_TAG = "NetworkDBHelper";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;
    public static String DEVICE_ID = "";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE_CLASS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_ID + TEXT_TYPE + " DEFAULT " + DEVICE_ID + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DEVICE_SOFTWARE_VERSION + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_EPOCH + INTEGER_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_DAY + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP_HOUR + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_OPERATOR_CODE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ACCURACY + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_CALL_STATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_ACTIVITY + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATA_STATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_CELL_INFO + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NEIGHBORING_CELLS_INFO + TEXT_TYPE +
                    " )";


    public NetworkDBHelper(Context mContext){
        super(mContext,DATABASE_NAME,null,DATABASE_VERSION);
        TelephonyManager mtelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        DEVICE_ID = mtelephonyManager.getDeviceId();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop and create again.
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
