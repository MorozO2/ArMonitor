package com.example.armonitor


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient

class MainActivity : AppCompatActivity() {

    private var toAr: Button? = null
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val mqtt: MqttHandler = MqttHandler(this)
    val topics: Array<String> = arrayOf("raspi1/temp", "raspi2/paper")
    val qos = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toAr = findViewById<Button>(R.id.toAr)

        toAr.setOnClickListener(View.OnClickListener
        {
            openArActivity()
        })


        mqtt.connect(topics)
       // mqtt.subscribe("raspi1/paper")
        mqtt.receiveMessages(::DisplayMessage)
    }

    ///METHOD FOR OPENING AR ACTIVITY//////////////////////////////////////////
    private fun openArActivity() {
        val openArView = Intent(this, ArActivity::class.java)
        startActivity(openArView)
    }

    fun DisplayMessage(msg: String, viewColor: Int, topic: String) {


        var ll = findViewById<View>(R.id.receivedLayout) as LinearLayout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val currentView = ll.findViewWithTag<TextView>("raspi1/temp")
        params.setMargins(ViewGroup.LayoutParams.MATCH_PARENT, 10, ViewGroup.LayoutParams.MATCH_PARENT, 10)

        if(currentView == null) {
            val mqttMsg = TextView(applicationContext)
            mqttMsg.layoutParams = params
            mqttMsg.setBackgroundColor(viewColor)
            mqttMsg.gravity = Gravity.CENTER or Gravity.BOTTOM
            mqttMsg.setTextSize(30.0F)
            mqttMsg.setTag(topic)
            mqttMsg.text = topic + "       " + msg

            ll.addView(mqttMsg)
        }
        else
        {
            currentView.text = "View ID: " + currentView.id.toString() + "       " + msg
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}


