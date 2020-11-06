package com.example.armonitor


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

    private var toAr: Button? = null
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val mqtt: MqttHandler = MqttHandler(this)
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


        mqtt.connect(topic)
      //  subscribe(topic)
        mqtt.receiveMessages(::DisplayMessage)
    }

    ///METHOD FOR OPENING AR ACTIVITY//////////////////////////////////////////
    private fun openArActivity() {
        val openArView = Intent(this, ArActivity::class.java)
        startActivity(openArView)
    }

    fun DisplayMessage(msg: String) {

        var ll = findViewById<View>(R.id.receivedLayout) as LinearLayout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(ViewGroup.LayoutParams.MATCH_PARENT, 10, ViewGroup.LayoutParams.MATCH_PARENT, 10)
        val mqttMsg = TextView(applicationContext)
        mqttMsg.layoutParams = params
        mqttMsg.setBackgroundColor(applicationContext.getColor(R.color.lime))
        mqttMsg.gravity = Gravity.CENTER or Gravity.BOTTOM
        mqttMsg.text = msg
        ll.addView(mqttMsg)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}


