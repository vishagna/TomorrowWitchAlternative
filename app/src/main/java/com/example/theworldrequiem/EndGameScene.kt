package com.example.theworldrequiem

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Database.DatabaseManager
import com.example.theworldrequiem.databinding.ActivityEndGameSceneBinding
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EndGameScene : AppCompatActivity() {
    private lateinit var binding: ActivityEndGameSceneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEndGameSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MusicManager.stopBackgroundMusic()
        MusicManager.playBackgroundMusic(this, R.raw.lose_bg, true)
        MusicManager.stopAllMusic()
        val gameState = intent.getStringExtra("gameState")
        var coinClaimed = intent.getStringExtra("coinClaimed")
        val timeRemain = intent.getStringExtra("timeRemain")
        if(gameState == "win") if (coinClaimed != null) {
            coinClaimed = (coinClaimed.toInt() + 50).toString()
        }

        binding.coinClaimed.text = "Coin Claimed: $coinClaimed"
        binding.timeRemain.text = "Time Remaining: $timeRemain"
        binding.endBack.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
            finish()
        }
        if (gameState != null) {
            if (coinClaimed != null) {
                endAnimation(gameState, coinClaimed.toInt())
            }
        }

    }

    fun endAnimation(endState: String, coinClaim: Int)
    {
        this.lifecycleScope.launch(Dispatchers.Main) {
            val databaseManager = DatabaseManager(this@EndGameScene)
            databaseManager.updateCoin(coinClaim)

            delay(2000)
            binding.curtain.setImageBitmap(BitmapFactory.decodeResource(this@EndGameScene.resources, R.drawable.curtain_short))
            delay(2000)
            binding.lightEffect.visibility = View.VISIBLE
            delay(100)
            binding.lightEffect.visibility = View.INVISIBLE
            delay(100)
            binding.lightEffect.visibility = View.VISIBLE
            delay(100)
            binding.lightEffect.visibility = View.INVISIBLE
            binding.window.setImageBitmap(BitmapFactory.decodeResource(this@EndGameScene.resources, R.drawable.window_2))
            if(endState == "lose") {
                binding.endState.text = "You Lose!"
                binding.loseState.visibility = View.VISIBLE
                MusicManager.playMusic(this@EndGameScene, R.raw.dead_scream)
            }
            else {
                binding.endState.text = "You Win!"
                binding.endState.setTextColor(Color.GREEN) // Màu lục mặc định
                binding.winState.visibility = View.VISIBLE
                MusicManager.playBackgroundMusic(this@EndGameScene, R.raw.win_bg)
            }
            binding.infoTable.visibility = View.VISIBLE
        }
    }




}