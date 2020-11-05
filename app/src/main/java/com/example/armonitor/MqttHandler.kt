package com.example.armonitor

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MqttHandler(private val context: Context) {

    val client by lazy {
        val clientId = MqttClient.generateClientId()
        MqttAndroidClient(context, "tcp://192.168.10.249:1883", clientId)
    }

    fun connect(topic: String) {

        try {
            val token = client.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)
                {
                    Log.i("Connection", "success")
                    subscribe(topic)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("Connection", "failure")
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


    fun subscribe(topic : String)
    {
        var qos = 1
        try{

            client.subscribe(topic, qos, null, object :
                    IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i("Subsription" , "success")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable)
                {
                    Log.i("Subscription", "failure")
                }
            })

        } catch (e: MqttException){
            Log.i("Subscription", "failure")
        }
    }

    fun receiveMessages()
    {
        client.setCallback(object : MqttCallback
        {
            override fun connectionLost(cause: Throwable?)
            {
                Log.i("Connection", "Lost")
            }

            override fun messageArrived(topic: String, message: MqttMessage)
            {
                try{
                    val data = String(message.payload, charset( "UTF-8"))
                    val id = message.id.toString()

                    Log.i("Message:", data)
                    Log.i("Message ID:", id)
                   // DisplayMessage(data)
                }catch(e : Exception){
                    Log.i("Message", "reception error")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?)
            {
                Log.i("Message","received")
            }
        })
    }

    fun unsubscribe(topic : String)
    {
        try{

            val unsubToken = client.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener
            {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i("Unsubscription", "success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i("Unsubscription", "failure")
                }
            }

        }catch(e : MqttException){
            Log.i("Unsubscription", "failure")
        }
    }


}