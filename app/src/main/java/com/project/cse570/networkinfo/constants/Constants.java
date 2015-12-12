package com.project.cse570.networkinfo.constants;/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 11/29/2015.
 */

/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 29,November,2015.
 */
public final class Constants {

    public static final long PERIODIC_INTERVAL = 5 * 1000; //15 seconds
    public static final String MQTT_HOST = "130.245.144.191";
    public static final int MQTT_PORT = 1883;
    public static final boolean IS_SSL = false;
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
    public static final String DATA_ACTIVITY_OUT = "Out";
    public static final String DATA_ACTIVITY_NONE = "None";
    public static String TOPIC = "networkInfo/data";
    public static String CLIENT_ID_PREFIX = "networkInfo";
}
