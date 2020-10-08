package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ArActivity extends AppCompatActivity implements GLSurfaceView.Renderer{

    private static final String TAG = ArActivity.class.getSimpleName();
    private Button toMsg;
    private GLSurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        surfaceView = findViewById(R.id.surfaceview);
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 0f, 0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}