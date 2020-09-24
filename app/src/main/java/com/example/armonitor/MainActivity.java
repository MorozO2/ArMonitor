package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


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

        final String topic = "topic/testing";
        final int qos = 1;
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://10.202.50.252:1883",
                        clientId);






        //CONNECT////////////////////////////////////////////////////////////////////////////////////////////
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");

                    //SUBSCRIBE: MUST BE INSIDE THE onSuccess() method after client.connect()//////////////////
                    try {
                        final IMqttToken subToken = client.subscribe(topic, qos);
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
                    //SUBSCRIBE*/////////////////////////////////////////////////////////////////
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
        //CONNECT*////////////////////////////////////////////////////////////////////////////////


        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = message.toString();
                Log.d(TAG, payload);
                Toast.makeText(getApplicationContext(), payload, payload.length()).show();
                DisplayMessage(payload);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Delivery complete");
            }
        });



    }

    //METHOD FOR OPENING AR ACTIVITY
    private void openArActivity() {
        Intent openArView = new Intent(this, ArActivity.class);
        startActivity(openArView);
    }

    private void DisplayMessage(String msg)
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.receivedLayout);
        TextView mqttMsg = new TextView( getApplicationContext());
        mqttMsg.setBackgroundColor(getResources().getColor(R.color.lime));
        mqttMsg.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        mqttMsg.setText(msg);
        ll.addView(mqttMsg);
    }
}

