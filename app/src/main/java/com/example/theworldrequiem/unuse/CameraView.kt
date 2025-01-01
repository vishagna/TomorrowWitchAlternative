package com.example.theworldrequiem.unuse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


class CameraView(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    @OptIn(ExperimentalGetImage::class)
    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun CameraScreen(
        onImageCaptureClick: () -> Unit,
        onVideoCaptureClick: () -> Unit
    ) {
        val localContext = LocalContext.current
        var isFrontCamera by remember { mutableStateOf(true) }
        var faceContours by remember { mutableStateOf<List<Path>>(emptyList()) }
        var previewSize by remember { mutableStateOf(Size(1080, 2222)) }


        // Initialize PreviewView
        val previewView = PreviewView(localContext)

        fun processImage(imageProxy: ImageProxy, onFacesDetected: (List<Path>) -> Unit) {
            val mediaImage = imageProxy.image ?: return
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            Log.e("Tag", "VAi ${mediaImage.width} and ${mediaImage.height} ")
            Log.e("Tag 2", "VAi ${previewSize.width} and ${previewSize.height} ")
            val options = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            val detector = FaceDetection.getClient(options)

            detector.process(inputImage)
                .addOnSuccessListener { faces: List<Face> ->

                    // Tính tọa độ chính xác cho các đường viền
                    val contours = faces.mapNotNull { face ->
                        val faceContour = face.getContour(FaceContour.FACE)
                        faceContour?.let { contour ->
                            val path = Path()
                            val points: List<PointF> = contour.points

                            if (points.isNotEmpty()) {
                                // Tính tọa độ cho điểm đầu tiên
                                path.moveTo(points[0].x, points[0].y)

                                // Lặp qua các điểm để vẽ
                                for (point in points) {
                                    path.lineTo(point.x, point.y)
                                }
                                path.close()
                            }
                            path
                        }
                    }
                    onFacesDetected(contours)
                }
                .addOnFailureListener { e ->
                    Log.e("CameraView", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }


        // Function to update camera
        fun updateCamera() {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(localContext)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }


                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(if (isFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(previewSize)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(localContext)) { imageProxy ->
                            Log.e("Tag 3", "Vai ${imageProxy.width} and ${imageProxy.height}")
                            processImage(imageProxy)
                            { contours ->
                                faceContours = contours
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    Log.d(
                        "CameraView",
                        if (isFrontCamera) "Front camera started" else "Back camera started"
                    )
                } catch (exc: Exception) {
                    Log.e("CameraView", "Use case binding failed", exc)
                    Toast.makeText(localContext, "Camera initialization failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }, ContextCompat.getMainExecutor(localContext))
        }

        // Set up the camera preview using CameraX API
        DisposableEffect(Unit) {
            updateCamera()
            onDispose { /* cleanup if needed */ }
        }

        // Layout
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()

        ) {
            // Cố định kích thước cho PreviewView
            Box(
                modifier = Modifier.fillMaxSize()
                    .align(Alignment.Center)
                    .onGloballyPositioned { layoutCoordinates ->
                        val width = layoutCoordinates.size.width
                        val height = layoutCoordinates.size.height
                        previewSize = Size(width, height) // Update preview size
                        updateCamera() // Update camera with new size// Căn giữa PreviewView
                    }
            ) {
                // Display the camera feed using AndroidView
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Draw face contours
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (path in faceContours) {
                    drawPath(
                        path = path,
                        color = Color.Red,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                    )
                }
            }

            // Button layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Image Capture Button
                Button(
                    onClick = { onImageCaptureClick() },
                    modifier = Modifier.size(110.dp)
                ) {
                    Text(text = "Take Photo")
                }

                // Video Capture Button
                Button(
                    onClick = { onVideoCaptureClick() },
                    modifier = Modifier.size(110.dp)
                ) {
                    Text(text = "Capture")
                }

                // Switch Camera Button
                Button(
                    onClick = {
                        isFrontCamera = !isFrontCamera
                        updateCamera()
                    },
                    modifier = Modifier.size(110.dp)
                ) {
                    Text(text = if (isFrontCamera) "Use Back" else "Use Front")
                }
            }
        }
    }
}
