package com.example.theworldrequiem.global

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MusicManager {
    private val mediaPlayers = mutableListOf<MediaPlayer>()
    private var backgroundMedia: MediaPlayer? = null
    private var backgroundMusicJob: Job? = null
    var volume = 1f

    // Phát nhạc mới
    fun playMusic(context: Context, musicResId: Int, loop: Boolean = false) {
        val mediaPlayer = MediaPlayer.create(context, musicResId).apply {
            isLooping = loop
            setOnCompletionListener {
                stopMusic(this)
            }
            setVolume(volume, volume)
            start()
        }
        mediaPlayers.add(mediaPlayer)
    }

    // Dừng nhạc cụ thể
    fun stopMusic(mediaPlayer: MediaPlayer) {
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayers.remove(mediaPlayer)
    }

    // Dừng tất cả nhạc
    fun stopAllMusic() {
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayers.clear()
    }

    // Tạm dừng tất cả nhạc
    fun pauseAllMusic() {
        mediaPlayers.forEach { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    // Tiếp tục tất cả nhạc
    fun resumeAllMusic() {
        mediaPlayers.forEach { mediaPlayer ->
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    // Kiểm tra có đang phát nhạc không
    fun isPlaying(): Boolean {
        return mediaPlayers.any { it.isPlaying }
    }

    // Phát nhạc nền trên một luồng riêng
    fun playBackgroundMusic(context: Context, musicResId: Int, loop: Boolean = true) {
        stopBackgroundMusic()
        backgroundMedia = MediaPlayer.create(context, musicResId).apply {
            isLooping = loop
            setOnCompletionListener {
                stopMusic(this)
            }
            start()
        }
    }

    // Dừng nhạc nền
    fun stopBackgroundMusic() {
        backgroundMedia?.stop()
    }

    fun setNewVolume(pVolume: Float) {
        // Đảm bảo volume nằm trong khoảng từ 0.0 (im lặng) đến 1.0 (to nhất)
        val adjustedVolume = pVolume.coerceIn(0f, 1f)
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.setVolume(adjustedVolume, adjustedVolume)
        }
        backgroundMedia?.setVolume(adjustedVolume, adjustedVolume)
        volume = pVolume
    }

}