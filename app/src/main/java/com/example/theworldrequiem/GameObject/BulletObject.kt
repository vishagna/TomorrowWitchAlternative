package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.R
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class BulletObject(
    pOwner: String,
    pEffect: String,
    pId: Int,
    pName: String,
    pDamage: Int,
    pImageFrames: MutableList<Bitmap>,
    pEndFrame: MutableList<Bitmap>,
    pXPos: Float,
    pYPos:Float,
    private var angle: Float
) {
    private val id = pId
    val owner = pOwner //interactor, enemy
    val effect = pEffect //lock, damage
    val bulletName = pName //harm, glow, burn, purify, negotigate
    private val imageFrames = pImageFrames
    private val endFrames = pEndFrame
    var bound: RectF? = null

    private var currentFrameIndex = 0
    private var currentEndIndex = 0

    private var job: Job? = null


    var xPos = pXPos
    var yPos = pYPos
    private val speed = 10f
    val damage = pDamage

    private var targetXPos = 0f;
    private var targetYPos = 0f;

    var isEnd = false
    var isDestroy = false

    fun copy(position: PointF): BulletObject
    {
        return BulletObject(owner, effect, id, bulletName, damage, imageFrames, endFrames, position.x, position.y, angle)
    }

    fun move(canvas: Canvas) {

        when (bulletName)
        {
            "harm", "flower", "poison", "web" -> {
                if(!isEnd)
                {
                    val radian = Math.toRadians(angle.toDouble())
                    xPos += (speed * cos(radian)).toFloat()
                    yPos += (speed * sin(radian)).toFloat()
                    canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
                }
                else
                {
                    canvas.drawBitmap(endFrames[currentEndIndex], xPos, yPos, null)
                }
            }
            "glow" ->
            {
                drawLine(canvas)
            }
            "burn" ->
            {
                if(!isEnd){
                    canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
                }
                else
                {
                    canvas.drawBitmap(endFrames[currentEndIndex], xPos, yPos, null)
                }
            }
            "negotigate" ->
            {
                if(!isEnd){
                    canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
                }
                else
                {
                    canvas.drawBitmap(endFrames[currentEndIndex], xPos, yPos, null)
                }
            }
        }

        // Vẽ frame hiện tại
    }

//    fun move(canvas: Canvas) {
//        // Draw the current frame tại vị trí ngẫu nhiên
//        yPos -= speed
//        canvas.drawBitmap(imageFrames[currentFrameIndex], xPos, yPos, null)
//    }

    fun end(context: Context) {
        if(!isEnd)
        {
            when(bulletName)
            {
                "harm", "poison", "web", "sun", "burn" -> {MusicManager.playMusic(context, R.raw.explosion)}
            }
            isEnd = true
            job?.cancel()
            if (context is LifecycleOwner) {
                job = context.lifecycleScope.launch(Dispatchers.Main) {
                    when(bulletName)
                    {
                        "harm","burn", "negotigate", "flower", "poison", "web" -> {
                            for (i in currentEndIndex until endFrames.size-1) {
                                delay(150)  // Wait for 100ms
                                currentEndIndex = (currentEndIndex + 1)
                            }
                            delay(150)  // Wait for 100ms
                            isDestroy = true
                        }
                        "glow" -> {
                            delay(300)  // Wait for 100ms
                            isDestroy = true
                        }

                    }
                }
            } else {
                throw IllegalArgumentException("Context must be a LifecycleOwner")
            }
        }

    }

    fun start(context: Context) {
        when(bulletName)
        {
            "harm", "poison", "web", "sun", "burn" -> {MusicManager.playMusic(context, R.raw.shoot)}
            "glow" -> {MusicManager.playMusic(context, R.raw.laser)}
        }
        if (context is LifecycleOwner) {
            job = context.lifecycleScope.launch(Dispatchers.Main) {
                when (bulletName)
                {
                    "harm", "flower", "poison", "web" -> {
                        while (true) {
                            delay(100)  // Wait for 100ms
                            currentFrameIndex = (currentFrameIndex + 1) % imageFrames.size  // Update frame
                        }
                    }
                    "burn" -> {
                        var endTime:Long = 0
                        while (true) {
                            delay(100)  // Wait for 100ms
                            currentFrameIndex = (currentFrameIndex + 1) % imageFrames.size  // Update frame
                            endTime += 100
                            if(endTime.toInt() >= 4000)
                            {
                                end(context)
                            }
                        }
                    }
                    "negotigate" -> {
                        var endTime:Long = 0
                        while (true) {
                            delay(100)  // Wait for 100ms
                            currentFrameIndex = (currentFrameIndex + 1) % imageFrames.size  // Update frame
                            endTime += 100
                            if(endTime.toInt() >= 15000)
                            {
                                end(context)
                            }
                        }
                    }
                    "glow" -> {
                            delay(500)
                    }
                    else -> {}
                }

            }
        } else {
            throw IllegalArgumentException("Context must be a LifecycleOwner")
        }
    }



    fun stop() {
        job?.cancel()  // Stop the animation when needed
    }

    fun getBoundingBox(): RectF {
        return RectF(
            xPos + imageFrames[currentFrameIndex].width / 4,
            yPos + imageFrames[currentFrameIndex].height / 4,
            xPos + imageFrames[currentFrameIndex].width / 4 * 3,
            yPos + imageFrames[currentFrameIndex].height / 4 * 3
        )
    }

    private val whiteLine = Paint().apply {
        color = Color.WHITE // Màu trắng
        strokeWidth = 30f    // Độ dày của đường
        style = Paint.Style.STROKE
        isAntiAlias = true  // Chống răng cưa
    }

    fun drawLine(canvas: Canvas) {
        val centerX = xPos
        val centerY = yPos

        val angleDegrees = angle // Ví dụ: góc 30 độ
        val halfLength = 2000f

        val endX1 = (centerX + halfLength * cos(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endY1 = (centerY + halfLength * sin(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endX2 = (centerX - halfLength * cos(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endY2 = (centerY - halfLength * sin(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        canvas.drawLine(endX1, endY1, endX2, endY2, whiteLine)
    }

    fun isLineIntersectingRect(rect: RectF): Boolean {

        val centerX = xPos
        val centerY = yPos

        val angleDegrees = angle // Ví dụ: góc 30 độ
        val halfLength = 2000f

        val endX1 = (centerX + halfLength * cos(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endY1 = (centerY + halfLength * sin(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endX2 = (centerX - halfLength * cos(Math.toRadians(angleDegrees.toDouble()))).toFloat()
        val endY2 = (centerY - halfLength * sin(Math.toRadians(angleDegrees.toDouble()))).toFloat()

        // Kiểm tra nếu một trong các điểm đầu của đường thẳng nằm trong RectF
        if (rect.contains(endX1, endY1) || rect.contains(endX2, endY2)) {
            return true
        }

        // Kiểm tra giao cắt với các cạnh của RectF
        val rectLeft = rect.left
        val rectTop = rect.top
        val rectRight = rect.right
        val rectBottom = rect.bottom

        // Cạnh trái
        if (doLineSegmentIntersect(
                endX1,
                endY1,
                endX2,
                endY2,
                rectLeft,
                rectTop,
                rectLeft,
                rectBottom
            )) return true
        // Cạnh trên
        if (doLineSegmentIntersect(
                endX1,
                endY1,
                endX2,
                endY2,
                rectLeft,
                rectTop,
                rectRight,
                rectTop
            )) return true
        // Cạnh phải
        if (doLineSegmentIntersect(
                endX1,
                endY1,
                endX2,
                endY2,
                rectRight,
                rectTop,
                rectRight,
                rectBottom
            )) return true
        // Cạnh dưới
        if (doLineSegmentIntersect(
                endX1,
                endY1,
                endX2,
                endY2,
                rectLeft,
                rectBottom,
                rectRight,
                rectBottom
            )) return true

        return false
    }

    fun doLineSegmentIntersect(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, x4: Float, y4: Float): Boolean {
        val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
        if (denominator == 0f) return false // Đoạn đường thẳng song song

        val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
        val u = ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / denominator

        return t in 0f..1f && u in 0f..1f
    }

}