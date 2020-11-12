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
    val topic = "raspi1/temp"
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
        mqtt.receiveMessages(::DisplayMessage)
    }

    ///METHOD FOR OPENING AR ACTIVITY//////////////////////////////////////////
    private fun openArActivity() {
        val openArView = Intent(this, ArActivity::class.java)
        startActivity(openArView)
    }

    fun DisplayMessage(msg: String, viewColor: Int, id: Int) {


        var ll = findViewById<View>(R.id.receivedLayout) as LinearLayout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(ViewGroup.LayoutParams.MATCH_PARENT, 10, ViewGroup.LayoutParams.MATCH_PARENT, 10)
        val mqttMsg = TextView(applicationContext)
        mqttMsg.layoutParams = params
        mqttMsg.setBackgroundColor(viewColor)
        mqttMsg.setId(id)
        mqttMsg.gravity = Gravity.CENTER or Gravity.BOTTOM
        mqttMsg.setTextSize(30.0F)
        mqttMsg.text = mqttMsg.id.toString() + "       " + msg

        ll.addView(mqttMsg)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}


