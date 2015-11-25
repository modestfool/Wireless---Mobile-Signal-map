
package com.project.cse570.networkinfo.SQLite;

import android.provider.BaseColumns;

/**
 * @author: Basava R. Kanaparthi (basava.08@gmail.com) created on 11/22/2015.
 */


/*
*  1. Timestamp
*  2. Cell Vendor Type
*  3. Network Type
*  4. Location
*  5. IMEI
*  6. how to compare our type with the vendor coverage type
*  7. Movement Pattern
*
* */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "network_info";
        /*Timestamps and device information */
        public static final String COLUMN_NAME_DEVICE_ID = "device_id";
        public static final String COLUMN_NAME_DEVICE_SOFTWARE_VERSION = "device_software_version";
        public static final String COLUMN_NAME_TIMESTAMP_EPOCH = "timestamp_epoch";
        public static final String COLUMN_NAME_TIMESTAMP_DATE = "timestamp_date";
        public static final String COLUMN_NAME_TIMESTAMP_DAY = "timestamp_day";
        public static final String COLUMN_NAME_TIMESTAMP_TIME = "timestamp_time";

        /* Fields of interest, network type (2G, 3G, 4G, Unknown) and respective integer*/
        public static final String COLUMN_NAME_NETWORK_TYPE = "network_type";
        public static final String COLUMN_NAME_NETWORK_TYPE_INT = "network_type_int";
        public static final String COLUMN_NAME_NETWORK_OPERATOR_NAME = "network_operator_name";
        public static final String COLUMN_NAME_NETWORK_OPERATOR_CODE = "network_operator_code";
        public static final String COLUMN_NAME_CELL_LOCATION ="cell_location";
        public static final String COLUMN_NAME_CALL_STATE ="call_state";
        public static final String COLUMN_NAME_DATA_ACTIVITY ="data_activity";
        public static final String COLUMN_NAME_DATE_STATE ="data_state";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ACCURACY = "accuracy_meters";


    }
}