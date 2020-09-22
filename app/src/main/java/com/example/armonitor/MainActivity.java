package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private Button toAr;
    private Button mqttConnect;
    private static final String TAG = MainActivity.class.getSimpleName();
    MqttCallback mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toAr = findViewById(R.id.toAr);
        toAr.setOnClickListener(new View.OnClickListener() { //BUTTON FOR OPENING AR ACTIVITY
            @Override
            public void onClick(View v) {
                openArActivity();
            }
        });

        try {
            MqttClient client = new MqttClient("tcp://10.202.50.252:1883", "AndroidThingSub", new MemoryPersistence());
            client.setCallback(this);
            client.connect();
            String topic = "topic/testing";
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    //METHOD FOR OPENING AR ACTIVITY
    private void openArActivity() {
        Intent openArView = new Intent(this, ArActivity.class);
        startActivity(openArView);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG,"Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "delivery complete");
    }
}