package com.example.joy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.Preview.OnPreviewOutputUpdateListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraStartP : AppCompatActivity() {
    var backMenu: Button? = null
    var switchCameras: Button? = null
    var takePicture: Button? = null
//    var textureView: TextureView? = null
    private var lensFacing = LensFacing.BACK

    // Code for permission for using camera and storage of phone
    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landscape_activity_camera_start_p2)
        backMenu = findViewById<View>(R.id.Back) as Button
        switchCameras = findViewById<View>(R.id.Switch) as Button
        takePicture = findViewById<View>(R.id.Snap) as Button
        textureView = findViewById(R.id.view_finder)
        textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform(1920,1080)
        }
        backMenu!!.setOnClickListener { toMenu() }


        // Switching between front and back camera
        switchCameras!!.setOnClickListener {
            lensFacing = if (lensFacing == LensFacing.FRONT) LensFacing.BACK else LensFacing.FRONT
            try {
                CameraX.getCameraWithLensFacing(lensFacing)
               textureView.post{startCamera()};
            } catch (e: CameraInfoUnavailableException) {
                e.printStackTrace()
            }
        }
        //start camera if permission has been granted by user
        if (allPermissionsGranted()) {
           textureView.post{startCamera()}
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }
    private lateinit var textureView: TextureView

    private fun toMenu() {
        val backToMenu = Intent(this, MainActivity::class.java)
        startActivity(backToMenu)
    }

    // Starting camera
    private fun startCamera() {
        // unbind camera
        CameraX.unbindAll()
        val aspectRatio = Rational(textureView.width, textureView.height)
        val screen = Size(textureView.width, textureView.height) //size of the screen
        val pConfig = PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                .setLensFacing(lensFacing)
                .build()
        val preview = Preview(pConfig)
        preview.onPreviewOutputUpdateListener = OnPreviewOutputUpdateListener { it ->
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)
            textureView.surfaceTexture = it.surfaceTexture
            updateTransform(1920,1080)
        }

        // taking picture
        val imageCaptureConfig = ImageCaptureConfig.Builder().setLensFacing(lensFacing).setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(windowManager.defaultDisplay.rotation).build()
        val imgCap = ImageCapture(imageCaptureConfig)
        findViewById<View>(R.id.Snap).setOnClickListener {
            val imagesFolder = File ( Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Camera")
            if(!imagesFolder.exists()){imagesFolder.mkdirs()}
            val file = File(imagesFolder.toString() + File.separator + System.currentTimeMillis() + ".png")
            imgCap.takePicture(file, object : ImageCapture.OnImageSavedListener {
                // image is saved on phone storage
                override fun onImageSaved(file: File) {
                    val msg = "Pic captured at " + file.absolutePath
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }

                override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                    val msg = "Pic capture failed : $message"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    cause?.printStackTrace()
                }
            })
        }

        // Adding dynamically fragment

        //bind to lifecycle:
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imgCap)
    }

    // necessary for rotating the camera
    private fun updateTransform(width:Int,height:Int) {

        val mx = Matrix()
        val w = textureView.width.toFloat()
        val h = textureView.height.toFloat()
        val cX = w / 2f
        val cY = h / 2f

        val rotationDgr = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        mx.postRotate(-rotationDgr.toFloat(), cX, cY)

        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val previewWidth = metrics.widthPixels
        val previewHeight = metrics.heightPixels

        mx.postScale(
                previewWidth.toFloat() / height,
                previewHeight.toFloat() / width,
                cX,
                cY
        )

        textureView.setTransform(mx)
    }


    // Request Permission before using the camera and storage of phone
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Check for permission
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}