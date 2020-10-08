package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class ArActivity extends AppCompatActivity implements GLSurfaceView.Renderer{

    private static final String TAG = ArActivity.class.getSimpleName();
    private Button toMsg;
    private GLSurfaceView surfaceView;
    private Session session;
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        final Session session;
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

        try{
            backgroundRenderer.createOnGlThread(this);
        }catch(IOException e)
        {
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            session = new Session(this);
            session.resume();

        } catch (UnavailableArcoreNotInstalledException | UnavailableApkTooOldException | UnavailableSdkTooOldException | UnavailableDeviceNotCompatibleException | CameraNotAvailableException e) {

        session = null;
        return;
        }
        surfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(session!=null)
        {
            surfaceView.onPause();
            session.pause();
        }
    }
}