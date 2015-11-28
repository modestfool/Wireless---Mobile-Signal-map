/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.project.cse570.networkinfo.Activities;

//import org.eclipse.paho.android.service.sample.R;
import java.util.Arrays;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Formatter;
//import org.eclipse.paho.android.service.sample.Connection.ConnectionStatus;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import android.content.*;
//import com.example.spectre.*;

/**
 * Handles call backs from the MQTT Client
 *
 */
public class MqttCallbackHandler implements MqttCallback {

  /** {@link Context} for the application used to format and import external strings**/
  private Context context;
  /** Client handle to reference the connection that this handler is attached to**/
  private String clientHandle;
  
  private MqttAndroidClient client = null;
  
 
  /**
   * Creates an <code>MqttCallbackHandler</code> object
   * @param context The application's context
   * @param clientHandle The handle to a {@link Connection} object
   */
  public MqttCallbackHandler(Context context, String clientHandle, MqttAndroidClient client)
  {
    this.context = context;
    this.clientHandle = clientHandle;
    this.client = client;
  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(Throwable)
   */
  @Override
  public void connectionLost(Throwable cause) {

  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(String, org.eclipse.paho.client.mqttv3.MqttMessage)
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
	  
	/*
	 * The message body will either contain the string dummyData or
	 * a channel number. If It is a dummy data we will give back wifi RSSI. This is for testing
	 * purpose only, and intended to be used when we do not have RTL-SDR hooked to the device.
	 * 
	 * If we have RTL SDR we send the channel number
	 * 
	 */
    
    String messageBody = new String(message.getPayload());
       
    Log.e("mytag", "Got message: Topic: " + topic + " Message: " +  messageBody);
    
    String[] messageFor = topic.split("/");
//    /*String mac = ClientDeviceContext.getDeviceContext(context).getMACAddress();
//
//    Log.e("mytag", " message for " + messageFor[1]);
//    Log.e("mytag", " MAC " + messageFor[1]);
//
//    if (messageFor[1].equals(mac)){
//    	//Only collect data if the instruction was meant for this device
//    	//MAC address in the instruction tells me if the instruction was meant for this device
//    	Log.e("mytag", "message meant for me");
//
//
//    	if(messageBody.equals("dummyData")){
//
//    		Log.e("mytag","Sending dummy data");
//
//    		ClientDeviceContext.getDeviceContext(context).getDummyData();
//    	}
//    	else{
//    		ClientDeviceContext.getDeviceContext(context).getData(messageBody);
//    	}
//
//
//
//    }*/
 
  }
  
 
  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Do nothing
  }

}
