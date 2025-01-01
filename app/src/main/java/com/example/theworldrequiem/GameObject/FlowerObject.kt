package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class FlowerObject(
    pId: Int,
    pName: String,
    pTime: Int,
    pImageFrames: MutableList<Bitmap>,
    pDeadFrames: MutableList<Bitmap>,
    pXPos: Float,
    pYPos:Float
) {
    private val id = pId
    private val enemyName = pName
    private val imageFrames = pImageFrames
    private val deadFrames = pDeadFrames
    var xPos = pXPos
    var yPos = pYPos

    private var currentFrameIndex = 0
    private var currentDeadIndex = 0
    private var job: Job? = null

    var isDead = false
    var isDisappear = false

    fun move(canvas: Canvas) {
        if(!isDead)
        {
            canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
        }
        else
        {
            canvas.drawBitmap(deadFrames[currentDeadIndex], xPos, yPos, null)
        }
    }

    fun start(context: Context?) {
        if (context is LifecycleOwner) {
            job = context.lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    delay(100)  // Wait for 100ms
                    currentFrameIndex = (currentFrameIndex + 1) % imageFrames.size  // Update frame
                }
            }
        } else {
            throw IllegalArgumentException("Context must be a LifecycleOwner")
        }
    }

    fun end(context: Context?) {
        if(!isDead)
        {
            isDead = true
            job?.cancel()
            if (context is LifecycleOwner) {
                job = context.lifecycleScope.launch(Dispatchers.Main) {
                    for (i in currentDeadIndex until deadFrames.size-1) {
                        delay(150)  // Wait for 100ms
                        currentDeadIndex = (currentDeadIndex + 1)
                    }
                    delay(150)  // Wait for 100ms
                    isDisappear = true
                }
            } else {
                throw IllegalArgumentException("Context must be a LifecycleOwner")
            }
        }

    }

    fun stop() {
        job?.cancel()  // Stop the animation when needed
    }
}