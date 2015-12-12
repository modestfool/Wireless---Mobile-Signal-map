/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 28,November,2015.
 */
package com.project.cse570.networkinfo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.project.cse570.networkinfo.activities.Connection;
import com.project.cse570.networkinfo.constants.Constants;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttService extends Service implements MqttCallback {
    private static final String LOG_TAG = "MqttService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Create the connection
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        String deviceId;
        TelephonyManager mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager.getDeviceId() != null) {
            Log.d(LOG_TAG, mTelephonyManager.getDeviceId());
            deviceId = mTelephonyManager.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(LOG_TAG, deviceId);
        }
        //Connection.createConnection("130.245.144.191", 1883, false, "networkInfo_2133", this);
        establishConnection(String.format(Constants.CLIENT_ID_PREFIX + "_%s", deviceId));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Connection.getConnection().getClient().unregisterResources();
        Connection.getConnection().getClient().close();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d(LOG_TAG, "ConnectionLost. Trying to re-establish");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    private void establishConnection(String clientId) {
        Connection.createConnection(Constants.MQTT_HOST, Constants.MQTT_PORT, Constants.IS_SSL, clientId, this);
    }
}
