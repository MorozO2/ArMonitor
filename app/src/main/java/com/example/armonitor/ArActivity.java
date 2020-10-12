package com.example.armonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class ArActivity extends AppCompatActivity implements GLSurfaceView.Renderer{

    private static final String TAG = ArActivity.class.getSimpleName();
    private Button toMsg;
    private GLSurfaceView surfaceView;
    private Session session;
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private boolean installRequested;

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
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }


                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }


                session = new Session(/* context= */ this);
            } catch (Exception e) {
                Log.e(TAG, "Failed to create AR session: " + e.getMessage());
                return;
            }
        }

        try {
            session.resume();
        } catch (CameraNotAvailableException e) {

            session = null;
            return;
        }

        surfaceView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
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
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if(session == null)
        {
            return;
        }

        try{
            session.setCameraTextureName(backgroundRenderer.getTextureId());
            Frame frame = session.update();
            backgroundRenderer.draw(frame);
        } catch(Exception e){

        }
    }


}