package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.ar.core.ArCoreApk;


public class ArActivity extends AppCompatActivity {

    private static final String TAG = ArActivity.class.getSimpleName();
    private Button toMsg;
    private GLSurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        surfaceView = findViewById(R.id.surfaceview);

        
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