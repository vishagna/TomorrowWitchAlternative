package com.example.theworldrequiem

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.theworldrequiem.ui.theme.TheWorldRequiemTheme
import com.example.theworldrequiem.unuse.CameraView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Camera Accepted", Toast.LENGTH_SHORT ).show();
            }
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            TheWorldRequiemTheme {
                val cameraView = CameraView(context = this, lifecycleOwner = this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        cameraView.CameraScreen(
                            onImageCaptureClick = { /* TODO: Xử lý sự kiện chụp ảnh */ },
                            onVideoCaptureClick = { /* TODO: Xử lý sự kiện quay video */ }
                        )
                    }
                }
            }
        }
    }
}



