/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * <p/>
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.project.cse570.networkinfo.Activities;

//import org.eclipse.paho.android.service.sample.Connection.ConnectionStatus;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * This Class handles receiving information from the
 * {@link MqttAndroidClient} and updating the {@link Connection} associated with
 * the action
 */
class ActionListener implements IMqttActionListener {

    /**
     * The {@link Action} that is associated with this instance of
     * <code>ActionListener</code>
     **/
    private Action action;
    /**
     * The arguments passed to be used for formatting strings
     **/
    private String additionalArgs;
    /**
     * {@link Context} for performing various operations
     **/
    private Context context;
    private MqttAndroidClient client = null;

    /**
     * Creates a generic action listener for actions performed form any activity
     *
     * @param context        The application context
     * @param action         The action that is being performed
     *                       S
     *                       The handle for the client which the action is being performed
     *                       on
     * @param additionalArgs Used for as arguments for string formating
     */
    public ActionListener(Context context, Action action, String additionalArgs, MqttAndroidClient client) {

        this.context = context;
        this.action = action;
        this.additionalArgs = additionalArgs;
        this.client = client;
    }

    /**
     * The action associated with this listener has been successful.
     *
     * @param asyncActionToken This argument is not used
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (action) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case PUBLISH:
                publish();
                break;
        }

    }

    /**
     * A publish action has been successfully completed, update connection
     * object associated with the client this action belongs to, then notify the
     * user of success
     */
    private void publish() {

//    Connection c = Connections.getInstance(context).getConnection(clientHandle);
//    String actionTaken = context.getString(R.string.toast_pub_success,
//        (Object[]) additionalArgs);
//    c.addAction(actionTaken);
//    Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);

        System.out.println("Successfully published");
        Log.d("Spectre", "Publish Success");
    }

    /**
     * A subscribe action has been successfully completed, update the connection
     * object associated with the client this action belongs to and then notify
     * the user of success
     */
    private void subscribe() {
//    Connection c = Connections.getInstance(context).getConnection(clientHandle);
//    String actionTaken = context.getString(R.string.toast_sub_success,
//        (Object[]) additionalArgs);
//    c.addAction(actionTaken);
//    Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);


        Log.e("mytag", "Subscribe Success");

    }

    /**
     * A disconnection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void disconnect() {

    }

    /**
     * A connection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void connect() {

//	  String mac = ClientDeviceContext.getDeviceContext(context).getMACAddress();
//	  String topic = "client/" +  mac + "/hello";
//
//      StringBuffer sb =  new StringBuffer();
//      double lat = ClientDeviceContext.getDeviceContext(context).getLatitude();
//      double lon = ClientDeviceContext.getDeviceContext(context).getLongitude();
//	  String model = ClientDeviceContext.getDeviceContext(context).getDeviceModel();
//      double batteryPercentRemaining = ClientDeviceContext.getDeviceContext(context).getBatteryLevel();


        //send a hello message with my current location

        Connection.getConnection().publish("anju/hello", "Hello Anju".getBytes(), 2, true);
        Log.e("anju", "hello published");

        //also subscrie to controller messages

        //String subscribeTopic = "controller/"+mac;
        // Connection.getConnection().subscribe(subscribeTopic, 2);

    }

    public void subscribeToSchedulingMessage(MqttAndroidClient client, String mac) {


        String topic = "controller/" + mac;
        int qos = 2;

        ActionListener callback = new ActionListener(context, Action.SUBSCRIBE, null, client);
        try {

            Log.e("mytag", "Subscribing to controller with " + topic);
            client.subscribe(topic, qos, null, callback);
        } catch (MqttException me) {

            Log.e("mytag", "error subscribing");
        }

    }

    /**
     * The action associated with the object was a failure
     *
     * @param token     This argument is not used
     * @param exception The exception which indicates why the action failed
     */
    @Override
    public void onFailure(IMqttToken token, Throwable exception) {
        switch (action) {
            case CONNECT:
                connect(exception);
                break;
            case DISCONNECT:
                disconnect(exception);
                break;
            case SUBSCRIBE:
                subscribe(exception);
                break;
            case PUBLISH:
                publish(exception);
                break;
        }

    }

    /**
     * A publish action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void publish(Throwable exception) {

        System.out.println("Publish Failed");
        Log.e("Spectre", "Publish Failed " + exception);

    }

    /**
     * A subscribe action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void subscribe(Throwable exception) {

        System.out.println("Subscribe Failed");
        Log.d("Spectre", "Subscribe Failed");

    }

    /**
     * A disconnect action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void disconnect(Throwable exception) {

        Log.d("Spectre", "Disconnect Failed");

    }

    /**
     * A connect action was unsuccessful, notify the user and update client history
     *
     * @param exception This argument is not used
     */
    private void connect(Throwable exception) {

        System.out.println("Connect Failed");
        Log.e("Spectre", "Connect Failed" + exception.getMessage());

    }

    /**
     * Actions that can be performed Asynchronously <strong>and</strong> associated with a
     * {@link ActionListener} object
     */
    enum Action {
        /**
         * Connect Action
         **/
        CONNECT,
        /**
         * Disconnect Action
         **/
        DISCONNECT,
        /**
         * Subscribe Action
         **/
        SUBSCRIBE,
        /**
         * Publish Action
         **/
        PUBLISH
    }

}