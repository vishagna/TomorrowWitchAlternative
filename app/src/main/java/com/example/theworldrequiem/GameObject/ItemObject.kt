package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Data.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ItemObject(pPosition: PointF, pItem: Item) {
    val position = pPosition
    val item = pItem


    var currentFrameIndex = 0
    var currentEndIndex = 0

    var animationJob: Job? = null

    var isEnd = false
    var isDestroy = false

    fun move(canvas: Canvas)
    {
        canvas.drawBitmap(item.frames[currentFrameIndex], position.x, position.y, null)
    }

    fun end(context: Context)
    {
        if(!isEnd)
        {
            isEnd = true
            animationJob?.cancel()
            if(context is LifecycleOwner)
            {
                animationJob = context.lifecycleScope.launch(Dispatchers.Main)
                {
                    for (i in currentEndIndex until item.frames.size-1) {
                        delay(150)  // Wait for 100ms
                        currentEndIndex = (currentEndIndex + 1)
                    }
                    delay(150)  // Wait for 100ms
                    isDestroy = true
                }
            }
        }
    }

    fun start(context: Context)
    {
        item.initial(context)
        if(context is LifecycleOwner)
        {
            animationJob = context.lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    delay(100)  // Wait for 100ms
                    currentFrameIndex = (currentFrameIndex + 1) % item.frames.size  // Update frame
                }
            }
        }
        else {
            throw IllegalArgumentException("Context must be a LifecycleOwner")
        }
    }

    fun stop()
    {
        animationJob?.cancel()
    }

    fun getBoundingBox(): RectF {
        return RectF(
            position.x + item.frames[currentFrameIndex].width / 4,
            position.y + item.frames[currentFrameIndex].height / 4,
            position.x + item.frames[currentFrameIndex].width / 4 * 3,
            position.y + item.frames[currentFrameIndex].height / 4 * 3
        )
    }
}