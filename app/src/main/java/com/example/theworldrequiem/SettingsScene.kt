package com.example.theworldrequiem

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Database.DatabaseManager
import com.example.theworldrequiem.databinding.ActivitySettingsSceneBinding
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsScene : AppCompatActivity() {
    lateinit var binding: ActivitySettingsSceneBinding
    var sound = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.settingsBack.setOnClickListener{
            saveSound()
            finish()
        }

        val databaseManager = DatabaseManager(this)
        databaseManager.getSound()?.let {
            binding.soundsVolume.value = it
        }

        binding.soundsVolume.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val volume = value // Chuyển đổi về giá trị từ 0.0 đến 1.0
                MusicManager.setNewVolume(volume)
                sound = value
            }
        }

    }

    fun saveSound()
    {
        this.lifecycleScope.launch(Dispatchers.Main) {
            val databaseManager = DatabaseManager(this@SettingsScene)
            databaseManager.updateSound(sound)
        }
    }

}