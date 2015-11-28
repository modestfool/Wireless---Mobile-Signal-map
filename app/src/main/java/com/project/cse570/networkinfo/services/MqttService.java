/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 28,November,2015.
 */
package com.project.cse570.networkinfo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.project.cse570.networkinfo.activities.Connection;

public class MqttService extends Service {
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
        Connection.createConnection("130.245.144.191", 1883, false, "networkInfo", this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Connection.getConnection().getClient().unregisterResources();
        Connection.getConnection().getClient().close();
    }

}
