package com.example.theworldrequiem.unuse

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.YuvImage
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.theworldrequiem.R
import com.example.theworldrequiem.databinding.ActivityAdvancedCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.ByteArrayOutputStream



class AdvancedCamera : AppCompatActivity() {

    private lateinit var binding: ActivityAdvancedCameraBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraPreview: Preview
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private var rotation: Int = 0
    private var isFront: Boolean = false

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(detector: FaceDetector, imageProxy: ImageProxy) {

        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        val bitmap = imageProxyToBitmap(imageProxy)
        if (bitmap != null) {
            // Áp dụng filter trắng đen hoặc xử lý khác
            val blackAndWhiteBitmap = convertToBlackAndWhite(bitmap)
            binding.colorFilter.setImageBitmap(blackAndWhiteBitmap)

        }

        detector.process(inputImage).addOnSuccessListener { faces ->
            binding.faceOverlayLayout.clear()
            faces.forEach { face ->
                val faceBox = FaceBox(binding.faceOverlayLayout, face, imageProxy.image!!.cropRect)
                binding.faceOverlayLayout.add(faceBox)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }



    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdvancedCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateCamera(isFront)
        val changeCamera =  binding.changeCameraSide
        changeCamera.setOnClickListener {
            isFront = !isFront
            updateCamera(isFront)
        }

        val captureImage = binding.captureImage
        captureImage.setOnClickListener {
            captureImage()
        }





    }

    private fun updateCamera(side: Boolean)
    {
        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

        cameraSelector = if(side) CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        else CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            cameraPreview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val options = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(options)



            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        rotation = imageProxy.imageInfo.rotationDegrees
                        processImageProxy(detector, imageProxy)
                    }
                }



            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    cameraPreview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraView", "Use case binding failed", exc)
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,
                fileName) // Tên file
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // Loại MIME
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // Thư mục Pictures
        }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        if (uri != null) {
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, uri, contentValues).build()

            // Ghi ảnh
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException) {
                        Log.e("CameraXApp", "Photo capture failed: ${error.message}", error)
                    }
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Lấy URI đã lưu từ kết quả
                        val savedUri = outputFileResults.savedUri ?: uri

                        val msg = "Photo capture succeeded: $savedUri"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d("CameraXApp", msg)
                    }
                }
            )
        } else {
            Toast.makeText(baseContext, "Failed to create MediaStore entry!", Toast.LENGTH_SHORT).show()
            Log.e("CameraXApp", "Failed to create MediaStore entry")
        }
    }

    companion object {
        private val TAG = AdvancedCamera::class.simpleName
        fun startActivity(context: Context) {
            Intent(context, AdvancedCamera::class.java).also {
                context.startActivity(it)
            }
        }
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}


@OptIn(ExperimentalGetImage::class)
fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    val mediaImage = imageProxy.image ?: return null

    // Chuyển đổi `MediaImage` sang YUV Image
    val yBuffer = mediaImage.planes[0].buffer // Y
    val uBuffer = mediaImage.planes[1].buffer // U
    val vBuffer = mediaImage.planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y, U, and V planes vào NV21 byte array
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    // Chuyển đổi NV21 format byte array thành Bitmap
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, mediaImage.width, mediaImage.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, mediaImage.width, mediaImage.height), 100, out)
    val imageBytes = out.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    // Lấy hướng xoay từ `ImageProxy` và áp dụng xoay vào bitmap
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    return rotateBitmap(bitmap, rotationDegrees)
}




fun convertToBlackAndWhite(bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)  // Đặt saturation thành 0 để chuyển ảnh sang trắng đen

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return resultBitmap
}

fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    val matrix = android.graphics.Matrix()
    matrix.postRotate(rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}









