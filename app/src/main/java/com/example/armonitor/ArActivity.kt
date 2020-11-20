
package com.example.armonitor


 import android.Manifest
 import android.content.BroadcastReceiver
 import android.content.Context
 import android.content.Intent
 import android.content.IntentFilter
 import android.content.pm.PackageManager
 import android.net.Uri
 import android.net.wifi.ScanResult
 import android.net.wifi.WifiManager
 import android.net.wifi.rtt.RangingRequest
 import android.net.wifi.rtt.RangingResult
 import android.net.wifi.rtt.RangingResultCallback
 import android.os.Bundle
 import android.util.Log
 import android.view.View
 import android.widget.Button
 import android.widget.TextView
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
 import androidx.core.app.ActivityCompat
 import com.google.ar.core.*
 import com.google.ar.sceneform.AnchorNode
 import com.google.ar.sceneform.Node
 import com.google.ar.sceneform.rendering.ModelRenderable
 import com.google.ar.sceneform.rendering.Renderable
 import com.google.ar.sceneform.ux.ArFragment
 import com.google.ar.sceneform.ux.TransformableNode
 import kotlinx.android.synthetic.main.activity_ar.*
 import java.util.concurrent.CompletableFuture
 import java.util.concurrent.ExecutionException
 import kotlin.math.log
 import kotlin.math.pow


class ArActivity : AppCompatActivity() {

    private lateinit var distanceDisplay: TextView
    private lateinit var arFragment: ArFragment
    private lateinit var selectedObject: Uri
    private var andyRenderable: ModelRenderable? = null
    private lateinit var wifiMan: WifiManager
    private var base = 10.0
    private var dist: Double? = null
    private lateinit var networks: List<ScanResult>


    private var wifiBroadcastReceiver: BroadcastReceiver = object:BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {

            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        val toMsg = findViewById<Button>(R.id.toMsg)
        distanceDisplay = findViewById<TextView>(R.id.distance)


        wifiMan = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        arFragment = supportFragmentManager.findFragmentById(arView.id) as ArFragment


        setModelPath("android.resource://com.example.armonitor/raw/andy")

        toMsg.setOnClickListener(View.OnClickListener {
            openMsgActivity()
        })

        val andy: CompletableFuture<ModelRenderable> = ModelRenderable.builder().setSource(this, R.raw.andy).build()


        CompletableFuture.allOf(andy).handle { notUsed, throwable ->
            if (throwable != null)
            {
                DemoUtils.displayError(this, "Unable to load renderables", throwable)
                return@handle null
            }

            try {
                andyRenderable = andy.get()
            } catch (ex: InterruptedException) {
                        DemoUtils.displayError(this, "Unable to load renderables", ex)
            } catch (ex: ExecutionException) {
                        DemoUtils.displayError(this, "Unable to load renderables", ex)
            }

            null
        }


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

        if(!wifiMan.isWifiEnabled)
        {
            Toast.makeText(applicationContext, "WIFI DISABLED", Toast.LENGTH_LONG).show()
        }
        else
        {
            scanWifi()
        }

        //onUpdateListener for each frame
        arFragment.arSceneView.scene.addOnUpdateListener { frameTime->
            arFragment.onUpdate(frameTime)
            onUpdate()
        }
    }



    private fun scanWifi()
    {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this.registerReceiver(wifiBroadcastReceiver, intentFilter)
        val success = wifiMan.startScan()
        if(success)
        {
            networks = wifiMan.scanResults
            if(networks.isEmpty())
            {
                   Toast.makeText(applicationContext, "NO NETWORKS DETECTED", Toast.LENGTH_LONG).show()
            }
            else
            {
                 Toast.makeText(applicationContext, "NETWORKS DETECTED", Toast.LENGTH_LONG).show()
            }

            for(network in networks)
            {
                if(network.SSID == "Auramoroz Ltd.")
                {
                    Toast.makeText(applicationContext, "${network.BSSID}", Toast.LENGTH_LONG).show()

                    val req: RangingRequest = RangingRequest.Builder().run {
                        addAccessPoint(network)
                        build()
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                }
            }
        }

        else
        {
            Toast.makeText(applicationContext, "SCAN FAILED", Toast.LENGTH_LONG).show()
        }

    }






    //GET CAMERA POSITION ON EACH FRAME
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
        dist = base.pow( (wifiMan.connectionInfo.rssi)/10*6.9861)
        distanceDisplay.setText("${dist!!.toFloat()}")

    }

    private fun getAndy():  Node? {
        val base = Node()
        base.setRenderable(andyRenderable)
        val c: Context = this
        base.setOnTapListener { v, event ->
            Toast.makeText(
                    c, "Andy touched.", Toast.LENGTH_LONG)
                    .show()
        }
        return base
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
}