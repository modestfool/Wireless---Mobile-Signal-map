/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 22,November,2015.
 */
package com.project.cse570.networkinfo.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;

/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 11/22/2015.
 */
public class NetworkDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NetworkInfo.db";
    public static String DEVICE_ID = "";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NETWORK_TYPE + TEXT_TYPE +
                    " )";
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;


    public NetworkDBHelper(Context mContext){
        super(mContext,DATABASE_NAME,null,DATABASE_VERSION);
        TelephonyManager mtelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        DEVICE_ID = mtelephonyManager.getDeviceId();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop and create again.
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
