package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class ArActivity extends AppCompatActivity {

    private static final String TAG = ArActivity.class.getSimpleName();
    private Button toMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        toMsg = findViewById(R.id.toMsg);
        toMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMsgActivity();
            }
        });
    }

    private void openMsgActivity() {
        Intent openMsgView = new Intent(this, MainActivity.class);
        startActivity(openMsgView);
    }

}