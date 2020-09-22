package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {

    private Button toAr;
    private Button mqttConnect;
    private static final String TAG = MainActivity.class.getSimpleName();
    String topic = "topic/testing";
    int qos = 1;
    String clientId = MqttClient.generateClientId();
    MqttAndroidClient client =
            new MqttAndroidClient(this.getApplicationContext(), "tcp://10.202.50.252:1883",
                    clientId);


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
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        //SUBSCRIBE
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d(TAG, "Subscribed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d(TAG, "Subscribe failed");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //SUBSCRIBE


    }

    //METHOD FOR OPENING AR ACTIVITY
    private void openArActivity() {
        Intent openArView = new Intent(this, ArActivity.class);
        startActivity(openArView);
    }

    /*/IMPLEMENTATION METHODS FROM MqttCallBack
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
    }*/
}