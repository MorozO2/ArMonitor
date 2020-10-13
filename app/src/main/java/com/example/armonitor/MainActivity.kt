package com.example.armonitor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.Throws

class MainActivity : AppCompatActivity() {

    private var toAr: Button? = null
    private lateinit var mqttAndroidClient: MqttAndroidClient
    val topic = "topic/testing"
    val qos = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toAr = findViewById<Button>(R.id.toAr)

        toAr.setOnClickListener(View.OnClickListener
        {
            openArActivity()
        })


        connect(this)
      //  subscribe(topic)
        receiveMessages()
    }

    ///METHOD FOR OPENING AR ACTIVITY//////////////////////////////////////////
    private fun openArActivity() {
        val openArView = Intent(this, ArActivity::class.java)
        startActivity(openArView)
    }


    private fun DisplayMessage(msg: String) {
        val ll = findViewById<View>(R.id.receivedLayout) as LinearLayout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(ViewGroup.LayoutParams.MATCH_PARENT, 10, ViewGroup.LayoutParams.MATCH_PARENT, 10)
        val mqttMsg = TextView(applicationContext)
        mqttMsg.layoutParams = params
        mqttMsg.setBackgroundColor(resources.getColor(R.color.lime))
        mqttMsg.gravity = Gravity.CENTER or Gravity.BOTTOM
        mqttMsg.text = msg
        ll.addView(mqttMsg)
    }

    private fun connect(context : Context) {
        val clientID = MqttClient.generateClientId()
        mqttAndroidClient = MqttAndroidClient(context.applicationContext, "tcp://192.168.10.248:1883", clientID)

        try {
            val token = mqttAndroidClient.connect()
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

            mqttAndroidClient.subscribe(topic, qos, null, object :
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

    fun unsubscribe(topic : String)
    {
        try{

            val unsubToken = mqttAndroidClient.unsubscribe(topic)
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

    fun receiveMessages()
    {
        mqttAndroidClient.setCallback(object : MqttCallback
        {
            override fun connectionLost(cause: Throwable?)
            {
                Log.i("Connection", "Lost")
            }

            override fun messageArrived(topic: String, message: MqttMessage)
            {
                try{
                    val data = String(message.payload, charset( "UTF-8"))
                    Log.i("Message:", data)
                    DisplayMessage(data)
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


    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}