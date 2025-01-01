package com.example.theworldrequiem

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import androidx.work.Configuration.Provider
import com.example.theworldrequiem.Controllers.SkillClaim
import com.example.theworldrequiem.databinding.ActivityAdvancedCameraBinding
import com.example.theworldrequiem.databinding.ActivityBodyCameraBinding
import com.example.theworldrequiem.global.MusicManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class BodyCamera : AppCompatActivity() {

    private lateinit var binding: ActivityBodyCameraBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraPreview: Preview
    private lateinit var cameraProvider: ProcessCameraProvider
    private var isFront: Boolean = true
    private var timeJob: Job? = null
    private val hpMax = 5
    var isOutTime = false
    var navigated = false
    var isSpawnBoss = false
    var timeRemain = "00:00"
    var level = 1

    private lateinit var skillClaim: SkillClaim



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBodyCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val startskill = intent.getStringExtra("startskill")
        level = intent.getIntExtra("level", 1) // Giá trị mặc định là -1
        Log.e("Level", "onCreate: $level", )
        binding.gameScene.spawnEnemy(this, level)


        MusicManager.playBackgroundMusic(this, R.raw.game_background)

        binding.ultimateFrame.updateAnimation(
            mutableListOf(
                BitmapFactory.decodeResource(this.resources, R.drawable.ultimate_frame_1),
                BitmapFactory.decodeResource(this.resources, R.drawable.ultimate_frame_2),
                BitmapFactory.decodeResource(this.resources, R.drawable.ultimate_frame_3),
                BitmapFactory.decodeResource(this.resources, R.drawable.ultimate_frame_4)
                ),
            0.3f
        )
        binding.gameScene.fetchImageShow(
            mutableListOf(
                binding.heart1,
                binding.heart2,
                binding.heart3,
                binding.heart4,
                binding.heart5
                )
        )

        binding.coin.text = 0.toString()
        binding.gameScene.coinShow = binding.coin

        updateCamera(isFront)
//        val changeCamera =  binding.bodyChangeCameraSide
//        changeCamera.setOnClickListener {
//            isFront = !isFront
//            updateCamera(isFront)
//        }
        val changeBackground = binding.bodyChangeBackground
            changeBackground.setOnClickListener {
            binding.gameScene.setBackground()
        }
        timeJob =  startCountdown(
            onTick = { secondsLeft ->
                binding.time.setText("${secondsLeft / 60}:${secondsLeft % 60}")
                timeRemain = "${secondsLeft / 60}:${secondsLeft % 60}"
            },
            onComplete = {
                println("Countdown complete!")
            }
        )

        skillClaim = SkillClaim(startskill.toString(), binding, this)
        skillClaim.onCreate(this, binding)

        binding.ultimateFrame.setOnClickListener {
            if(binding.skillClaimer.isVisible)
            {
                binding.skillClaimer.isVisible = false
            }
            else
            {
                skillClaim.startClaim(this)
                binding.skillClaimer.isVisible = true
            }
        }
    }

    fun startCountdown(onTick: (Int) -> Unit, onComplete: () -> Unit): Job {
        val countdownTime = 5.minutes.inWholeSeconds // 5 phút = 300 giây

        return CoroutineScope(Dispatchers.Main).launch {
            for (secondsLeft in countdownTime downTo 0) {
                onTick(secondsLeft.toInt()) // Gửi số giây còn lại ra ngoài
                delay(1.seconds)   // Đợi 1 giây
            }
            onComplete() // Hoàn thành đếm ngược
            isOutTime = true
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(detector: PoseDetector, imageProxy: ImageProxy) {
        //Log.e("Tag", "Detect Image")
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        detector.process(inputImage).addOnSuccessListener { pose ->
            val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
            if (nose != null) {
                binding.overlayLayout.updateInteractorPostition(binding, nose)
            }

            binding.overlayLayout.update(pose.allPoseLandmarks)
            if(skillClaim.isSelectState)
            {
                skillClaim.castingSelectState()
                binding.overlayLayout.update(pose.allPoseLandmarks)
                skillClaim.updateSelectState(binding.overlayLayout.getAction())
            }
            else if(skillClaim.isUltimateState)
            {
                skillClaim.ultimateState = binding.overlayLayout.getActionUltimate()
                skillClaim.castingUltimateState()
            }

            if(timeRemain == "2:30" && !isSpawnBoss && level==2)
            {
                binding.gameScene.spawnBoss(this@BodyCamera)
                isSpawnBoss = true
                binding.gameScene.stopSpawnEnemy()
            }

            if(binding.gameScene.checkDead() && navigated == false)
            {
                navigated = true
                val intent = Intent(this@BodyCamera, EndGameScene::class.java)
                intent.putExtra("gameState","lose")
                intent.putExtra("timeRemain",timeRemain)
                intent.putExtra("coinClaimed", binding.gameScene.coin.toString())
                startActivity(intent)
                finish()
            }

            if(((binding.gameScene.checkWin() && isOutTime) || (binding.gameScene.isBossDead)) && navigated == false)
            {
                navigated = true
                val intent = Intent(this@BodyCamera, EndGameScene::class.java)
                intent.putExtra("gameState","win")
                intent.putExtra("timeRemain",timeRemain)
                intent.putExtra("coinClaimed", binding.gameScene.coin.toString())
                startActivity(intent)
                finish()
            }


            binding.overlayLayout.clear()
            binding.overlayLayout.add(pose)
            binding.overlayLayout.updateImageRect(Rect(0, 0, inputImage.width, inputImage.height))
        }.addOnFailureListener {
            it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }

    private fun updateCamera(side: Boolean)
    {
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
                .build().also {
                    it.setSurfaceProvider(binding.bodyPreviewView.surfaceProvider)
                }

            val options = AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build()
            val detector = PoseDetection.getClient(options)

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        processImageProxy(detector, imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    cameraPreview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraView", "Use case binding failed", exc)
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }


}