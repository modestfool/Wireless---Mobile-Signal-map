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

import android.util.Log;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
//import org.eclipse.paho.android.service.sample.R;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import org.eclipse.paho.android.service.MqttAndroidClient;
import com.project.cse570.networkinfo.Activities.ActionListener.*;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * 
 * Represents a {@link MqttAndroidClient} and the actions it has performed
 *
 */
public class Connection {

  /*
   * Basic Information about the client
   */
  
  /** The host that the {@link MqttAndroidClient} represented by this <code>Connection</code> is represented by **/
  private String host = null;
  /** The port on the server this client is connecting to **/
  private int port = 0;
  /** indicates whether the connection is needed over ssl or not **/
  boolean sslConnection = false;
  
  /** ID of the device that is connected to the mqtt host **/
  String clientID = null;
  
  /** The {@link MqttAndroidClient} instance this class represents**/
  private MqttAndroidClient client = null;

  
  /** The {@link Context} of the application this object is part of**/
  private Context context = null;

  /** The {@link MqttConnectOptions} that were used to connect this client**/
  private MqttConnectOptions conOpt;
  
  /** connection object **/
  private static Connection conn = null;

  
  /**
   * Creates a connection from persisted information in the database store, attempting
   * to create a {@link MqttAndroidClient} and the client handle.
   *
   */
  public static Connection createConnection(String host,
      int port, boolean sslConnection, String clientID,Context context) {
    
    String uri = null;
    MqttAndroidClient client = null;
    if (sslConnection) {
      uri = "ssl://" + host + ":" + port;
    }
    else {
      uri = "tcp://" + host + ":" + port;
    }
    Log.e("mytag","connecting to " + uri);
    //ActionListener callback = new ActionListener(context,ActionListener.Action.CONNECT,null,null);
    
    //client.setCallback(callback);
    try{
    	client = new MqttAndroidClient(context,uri, clientID);
    	
    	/*
    	 * Set up the ActionListner callback to handle connect event
    	 */
    	ActionListener callback = new ActionListener(context,ActionListener.Action.CONNECT,null,client);
    	
    	/*
    	 * Set up the MqttCallbackHandler to handle the events message arrived, and connection lost.
    	 * MqttCallbackHandler also has other functions like getting data from RTL-SDR
    	 */
    	
        MqttCallbackHandler msgHandler = new MqttCallbackHandler(context,clientID,client);
        
        /*
         * Set up the connection options. These options are specific to mqtt connection only.
         */
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(false);
        conOpt.setKeepAliveInterval(60);
        conOpt.setWill("client/" + clientID +"/bye", "Disconnecting".getBytes(), 2, true);
        
        
        /*
         * Register the callbacks with the client and connect
         */
        client.setCallback(msgHandler);
        client.connect(conOpt, context, callback);
    	Log.e("mytag","santosh connected " + client.isConnected());
    }
    catch(MqttException me){
    	
    	//System.out.println("reason "+me.getReasonCode());
        System.out.println("msg "+me.getMessage());
        System.out.println("loc "+me.getLocalizedMessage());
        System.out.println("cause "+me.getCause());
        System.out.println("excep "+me);
        me.printStackTrace();
        Log.e("mytag","Error in connecting" + "msg "+me.getMessage() + "cause "+me.getCause() );
    	
    }
    if(conn == null){
    	conn = new Connection(host, port, sslConnection, clientID, client);
    }
    
    return conn;

  }

  /**
   * Creates a connection object with the server information and the client
   * hand which is the reference used to pass the client around activities
   *
   */
	  private Connection(String host, int port, boolean sslConnection, String clientID, MqttAndroidClient client) {
	    //generate the client handle from its hash code
	    
	    this.host = host;
	    this.port = port;
	    this.context = context;   
	    this.sslConnection = sslConnection;
	    this.conOpt = conOpt;
	    this.client = client;
	    
	  }

  
  public void publish(String topic, byte[] message, int qos, boolean retained){
	  
  	/*
  	 * This function will publish to the mqtt broker encapsulated by this connection
  	 */
	
	/* Set up callback for the publish event */
	ActionListener callback =  new ActionListener(context, Action.PUBLISH, null,client);
	
	Log.e("mytag","Sending message client connected :" + client.isConnected());
	
	
	
	try {	
			Log.e("mytag","Sending data on: " + topic );
			//message      = "Some message from : " + getDeviceName() + " " + mTCPClient.result.toString();
	        //client.publish(topic, message.getBytes(), qos, retained, null,callback);
	        client.publish(topic, message, qos, retained, null,callback);
	    }
	    catch (MqttSecurityException e) {
	      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " , e);
	    }
	    catch (MqttException e) {
	      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle ", e);
	    }
	  
}
  
  
  
public void subscribe(String topic, int qos){
	 
	  /* Set up callback for the subscribe event */
	  ActionListener callback =  new ActionListener(context, Action.SUBSCRIBE, null,client);
	  try{
		  
		  Log.e("mytag", "Subscribing to controller with " + topic);
		  client.subscribe(topic, qos, null, callback);
	  }
	  catch(MqttException me){
		  
		  Log.e("mytag", "error subscribing");
	  }
  
  }
  
  
  

  /**
   * Get the connectOptions used to connect the client to the server.
   * @return Returns the mqtt connection options related to this connection
   */
  public MqttConnectOptions getConnectionOptions() {
    return this.conOpt;

  }
  
  
  /**
   * Gets the client which communicates with the android service.
   * @return the client which communicates with the android service
   */
  public MqttAndroidClient getClient() {
    return client;
  }

   
  /**
   * Get the host name of the server that this connection object is associated with
   * @return the host name of the server this connection object is associated with
   */
  public String getHostName() {

    return host;
  }

  /**
   * Gets the port that this connection connects to.
   * @return port that this connection connects to
   */
  public int getPort() {
    return port;
  }

  /**
   * Determines if the connection is secured using SSL, returning a C style integer value
   * @return 1 if SSL secured 0 if plain text
   */
  public int isSSL() {
    return sslConnection ? 1 : 0;
  }
  
  public static Connection getConnection(){
	  
	  return conn;
  }

}
