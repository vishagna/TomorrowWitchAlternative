package com.example.theworldrequiem.ExtendView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.Button

class AnimationObjectView (context: Context?, attrs: AttributeSet) : View(context, attrs) {

    private var frames: MutableList<Bitmap> = mutableListOf()
    private var delayTime: Float = 0.5f
    private var currentFrameIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false // Biến trạng thái để kiểm tra animation có đang chạy
    private var noRepeat = false

    fun updateAnimation(pFrames: MutableList<Bitmap>, pDelayTime: Float?)
    {
        frames = pFrames
        if (pDelayTime != null) {
            delayTime = pDelayTime
        }
        castAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun castAnimation() {
        if (!isRunning) { // Chỉ chạy nếu animation đang dừng
            isRunning = true
            handler.post(animationRunnable)
        }
    }

    private val animationRunnable = object : Runnable {
        override fun run() {
            if (frames.isNotEmpty()) {
                background = BitmapDrawable(resources, frames[currentFrameIndex])
                currentFrameIndex = (currentFrameIndex + 1) % frames.size
                handler.postDelayed(this, (delayTime * 1000).toLong())
                if(noRepeat)
                {
                    if(currentFrameIndex == frames.size-1) stopAnimation()
                }
            }
        }
    }

    // Hàm để dừng animation
    fun stopAnimation() {
        isRunning = false
        handler.removeCallbacks(animationRunnable)
    }
}