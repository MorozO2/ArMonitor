package com.example.armonitor


import android.content.Intent
import android.graphics.Color
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
    val topics: Array<String> = arrayOf("temp", "paper")
    val qos = 1
    val red = Color.parseColor("#F7370D")
    val green = Color.parseColor("#9EDC0B")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toAr = findViewById<Button>(R.id.toAr)

        toAr.setOnClickListener(View.OnClickListener
        {
            openArActivity()
        })


        mqtt.connect(topics)
        mqtt.receiveMessages(::DisplayMessage)
    }

    ///METHOD FOR OPENING AR ACTIVITY//////////////////////////////////////////
    private fun openArActivity() {
        val openArView = Intent(this, ArActivity::class.java)
        startActivity(openArView)
    }

    fun DisplayMessage(msg: String, topic: String) {


        var ll = findViewById<View>(R.id.receivedLayout) as LinearLayout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val currentView = ll.findViewWithTag<TextView>(topic)
        params.setMargins(ViewGroup.LayoutParams.MATCH_PARENT, 10, ViewGroup.LayoutParams.MATCH_PARENT, 10)

        if (currentView == null) {
            val mqttMsg = TextView(applicationContext)
            mqttMsg.layoutParams = params
            mqttMsg.setBackgroundColor(setColor(topic, msg.toInt()))
            mqttMsg.gravity = Gravity.CENTER or Gravity.BOTTOM
            mqttMsg.setTextSize(30.0F)
            mqttMsg.setTag(topic)
            mqttMsg.text = topic + "       " + msg

            ll.addView(mqttMsg)
        } else {
            currentView.setBackgroundColor(setColor(topic, msg.toInt()))
            currentView.text = topic + "      " + msg
        }
    }

    fun setColor(topic: String, value: Int): Int {

        val maxTemp = 50
        val minPaper = 100
        if (topic >= "temp") {
            if (value > maxTemp)
            {
                return red
            }
            else
            {
                return green
            }
        }

        else if (topic == "paper") {
            if (value < minPaper)
            {
                return red
            }

            else
            {
                return green
            }
        }
        return 0
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}


