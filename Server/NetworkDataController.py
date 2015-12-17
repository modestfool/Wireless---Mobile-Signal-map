# -*- coding: utf-8 -*-
"""
Created on Sat Nov 28 16:10:02 2015

@author: sindhu
"""

import paho.mqtt.client as mqtt
import pymongo
import json
import time

BROKER_IP = "130.245.144.191"
BROKER_PORT = 1883
CLIENT_IP = "127.0.0.1"
CLIENT_PORT = "27017"
MONGO_DATABASE_NAME = "NetworkInfo"
    

def controller():
    
    controller = mqtt.Client("DataController",clean_session=False , userdata=None)
    
    controller.connect(BROKER_IP, BROKER_PORT, 60)
    
    controller.on_connect = on_connect
    controller.on_subscribe = on_subscribe
    controller.on_publish = on_publish
    controller.on_disconnect = on_disconnect
    
    
    #controller.subscribe("networkInfo_355490061236976/data", 0)
    #controller.message_callback_add("networkInfo_355490061236976/data", on_message_received)
    
    controller.subscribe("networkInfo/data",0)
    controller.message_callback_add("networkInfo/data",on_message_received)


    controller.subscribe("networkInfo/hello", 0)
    controller.message_callback_add("networkInfo/hello", on_client_hello)
    
    
    controller.loop_start()
    
    while 1:
        time.sleep(0.05)
	pass
    
def get_db():
    
        connection_endpoint = CLIENT_IP+':'+CLIENT_PORT
        client = pymongo.MongoClient(connection_endpoint)
        db = client.NetworkInfo
        return db
    
def add_data(db,data):
        #print message.payload
	try:
        	js = json.loads(data.strip())
		db.NetworkInfo.insert(js)
    	except Exception as e:
		print e.message
		pass
def get_data(db):
        return db.NetworkInfo.find_one()
        
        
def on_connect(client, userdata, rc):
    
    print "Connected"
    
def on_publish(client, userdata, mid):
    
    print "on publish"
    
def on_subscribe(client, userdata, mid, granted_qos):
    
    print "subscribed"


def on_disconnect(client, userdata, rc):
    
    print "disconnected"
       

def on_message_received(client, userdata, message):
    
    print "Got message on topic : " + message.topic
    print "Message payload : " + message.payload
    db = get_db() 
    add_data(db,message.payload)
    #print get_data(db)
    
def on_client_hello(client, userdata, message):
    
    print "Got hello : " + message.topic
    print "Message payload : " + message.payload
    
if __name__ == "__main__":
    
    controller()
    
