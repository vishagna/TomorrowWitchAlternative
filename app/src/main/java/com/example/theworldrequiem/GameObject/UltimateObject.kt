package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.Data.Ultimate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UltimateObject(pXPos: Float, pYPos: Float, pUltimate: Ultimate) {
    var xPos = pXPos
    var yPos = pYPos
    val ultimate = pUltimate
    var currentFrameIndex = 0
    var currentDestroyIndex = 0
    var animationJob: Job? = null
    var isEnd = false
    var isDestroy = false

    fun move(canvas: Canvas)
    {
        canvas.drawBitmap(ultimate.frames[currentFrameIndex], xPos, yPos, null )
    }

    fun updatePosition(pPosition: PointF)
    {
        xPos = (pPosition.x - ultimate.frames[currentFrameIndex].width.toFloat()/2).toFloat()
        yPos = (pPosition.y - ultimate.frames[currentFrameIndex].height.toFloat()/2).toFloat()
    }

    fun start(context: Context)
    {
        var timeCount = 0f
        if (context is LifecycleOwner) {
            animationJob = context.lifecycleScope.launch(Dispatchers.Main) {
                while (true) {

                    delay(100)  // Wait for 100ms
                    timeCount+=100
                    currentFrameIndex = (currentFrameIndex + 1) % ultimate.frames.size  // Update frame
                    if(timeCount >= ultimate.remainTime * 1000)
                    {
                        end(context)
                        break
                    }
                }
            }
        } else {
            throw IllegalArgumentException("Context must be a LifecycleOwner")
        }
    }

    fun getBoundingBox(): RectF {
        return RectF(
            xPos + ultimate.frames[currentFrameIndex].width / 4,
            yPos + ultimate.frames[currentFrameIndex].height / 4,
            xPos + ultimate.frames[currentFrameIndex].width / 4 * 3,
            yPos + ultimate.frames[currentFrameIndex].height / 4 * 3
        )
    }

    fun end(context: Context?) {
        if(!isEnd)
        {
            isEnd = true
            animationJob?.cancel()
            if (context is LifecycleOwner) {
                animationJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    for (i in currentDestroyIndex until ultimate.endFrames.size-1) {
                        delay(150)  // Wait for 100ms
                        currentDestroyIndex = (currentDestroyIndex + 1)
                    }
                    delay(150)  // Wait for 100ms
                    isDestroy = true

//                    while (true) {
//                        if(currentEndIndex < endFrames.size)
//                        {
//                            delay(150)  // Wait for 100ms
//                            currentEndIndex = (currentEndIndex + 1)  // Update frame
//                        }
//                    }
                }
            } else {
                throw IllegalArgumentException("Context must be a LifecycleOwner")
            }
        }

    }
}