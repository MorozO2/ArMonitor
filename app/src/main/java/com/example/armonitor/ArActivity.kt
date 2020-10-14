package com.example.armonitor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*


class ArActivity : AppCompatActivity(){


    private var mUserRequestedInstall = true
    var mSession: Session? = null
    private var toMsg: Button? = null
    private lateinit var arFragment: ArFragment
    private lateinit var selectedObject: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        val toMsg = findViewById<Button>(R.id.toMsg)
        arFragment = supportFragmentManager.findFragmentById(arView.id) as ArFragment
        //setModelPath("rocket.sfb")
        toMsg.setOnClickListener(View.OnClickListener {
            openMsgActivity()
        })
        //Tab listener for the ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            //If surface is not horizontal and upward facing
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                //return for the callback
                return@setOnTapArPlaneListener
            }
            //create a new anchor
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, selectedObject)
        }

        //onUpdateListener for each frame
      arFragment.arSceneView.scene.addOnUpdateListener {frameTime->
            arFragment.onUpdate(frameTime)
          onUpdate()
        }
    }

    fun onUpdate()
    {
        val frame: Frame? = arFragment.arSceneView.arFrame
        val camera: Camera? = frame?.camera
        if (camera != null) {
            if(camera.trackingState == TrackingState.TRACKING) {
                val cameraPose: Pose = camera.displayOrientedPose
                Log.i("Pose", "$cameraPose")
            }
        }
    }

    private fun openMsgActivity()
    {
        val openMsg = Intent(this, MainActivity::class.java)
        startActivity(openMsg)
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, modelUri: Uri) {
        val modelRenderable = ModelRenderable.builder()
                .setSource((fragment.requireContext()), modelUri)
                .build()
        //when the model render is build add node to scene
        modelRenderable.thenAccept { renderableObject -> addNodeToScene(fragment, anchor, renderableObject) }
        //handle error
        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
            toast.show()
            null
        }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    private fun setModelPath(modelFileName: String) {
        selectedObject = Uri.parse(modelFileName)
        val toast = Toast.makeText(applicationContext, modelFileName, Toast.LENGTH_SHORT)
        toast.show()
    }


/*
    protected override fun onResume() {
        super.onResume()
        //CHECKS CAMERA PERMISSION
        if(!CameraPermissionHelper.hasCameraPermission(this))
        {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }

        //CHECKS WHETHER GOOGLE PLAY SERVICES FOR AR IS INSTALLED
        try {

            if(mSession == null)
            {
                when(ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall))
                {
                    ArCoreApk.InstallStatus.INSTALLED -> mSession = Session(this)
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        mUserRequestedInstall = false
                        return
                    }
                }
            }

        }catch(  e: UnavailableUserDeclinedInstallationException){
            Toast.makeText(this, "TODO: handle exception: $e", Toast.LENGTH_LONG).show()
            return
        }
    }

    public override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(!CameraPermissionHelper.hasCameraPermission(this))
        {
            Toast.makeText(this, "Camera persmisssion is needed to run this applicatoin", Toast.LENGTH_LONG).show()
            if(!CameraPermissionHelper.shouldShowRequestPermissionRationale(this))
            {
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }
    */


}