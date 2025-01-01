package com.example.theworldrequiem.GameObject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.theworldrequiem.R
import com.example.theworldrequiem.global.MusicManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InteractionObject(pFrames: MutableList<Bitmap> ) {

    var hp = 5;

    var frames = pFrames
    var currentFrameIndex = 0
    var animationJob: Job? = null

    var isInvincible = false
    var invincibleJob: Job? = null
    var stuckJob: Job? = null
    var isStuck = false

    var xPos: Float = 0F
    var yPos: Float = 0F
    private var targetX: Float = 0F
    private var targetY: Float = 0F
    private val primaryInterpolationFactor = 0.1f
    var isAcceleration = 1f;
    private var interpolationFactor = 0.1F // Tốc độ nội suy (0 đến 1)

//    fun move(canvas: Canvas) {
//        // Nội suy tuyến tính giữa vị trí hiện tại và vị trí đích
//        xPos += (targetX - xPos) * interpolationFactor
//        yPos += (targetY - yPos) * interpolationFactor
//
//        // Vẽ bitmap tại vị trí nội suy mới
//        canvas.drawBitmap(frames[currentFrameIndex], xPos, yPos, null)
//    }

    fun move(canvas: Canvas) {
        // Xác định xem đối tượng có di chuyển sang trái hay không
        val movingRight = targetX > xPos

        if(!isStuck)
        {
            // Nội suy tuyến tính giữa vị trí hiện tại và vị trí đích
            xPos += (targetX - xPos) * interpolationFactor * isAcceleration
            yPos += (targetY - yPos) * interpolationFactor * isAcceleration
        }


         //Lưu trạng thái của canvas
        canvas.save()

        // Nếu di chuyển sang trái, lật ngang đối tượng
        if (movingRight) {
            canvas.scale(
                -1f, 1f,  // Lật ngang (trục X)
                xPos + frames[currentFrameIndex].width / 2,  // Tâm lật ngang X
                yPos + frames[currentFrameIndex].height / 2  // Tâm lật ngang Y
            )
        }

        // Vẽ bitmap tại vị trí hiện tại
        if(isInvincible)
        {
            val paint = Paint().apply {
                alpha = 150 // Alpha: 0 (trong suốt hoàn toàn) -> 255 (không trong suốt)
                colorFilter = PorterDuffColorFilter(
                    0xFF004D40.toInt(), // Mã màu xanh đậm (ARGB)
                    PorterDuff.Mode.SRC_ATOP // Phương thức pha trộn
                )
            }
            canvas.drawBitmap(frames[currentFrameIndex], xPos, yPos, paint )
        }
        else
        {
            val paint = Paint().apply {
                colorFilter = PorterDuffColorFilter(
                    0xFF004D40.toInt(), // Mã màu xanh đậm (ARGB)
                    PorterDuff.Mode.SRC_ATOP // Phương thức pha trộn
                )
            }
            canvas.drawBitmap(frames[currentFrameIndex], xPos, yPos, null)
        }

        // Khôi phục trạng thái canvas (bỏ lật ảnh)
        canvas.restore()
    }

    fun takeDamage(context: Context)
    {
        if(!isInvincible)
        {
            isInvincible = true
            MusicManager.playMusic(context, R.raw.hurt)
            invincibleJob?.cancel()
            if(context is LifecycleOwner)
            {
                invincibleJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    while (true) {
                        delay(1000)  // Wait for 100ms
                        isInvincible = false  // Update frame
                    }
                }
            }
        }
    }




    fun start(context: Context, width: Int, height: Int)
    {
        xPos = ((width - frames[currentFrameIndex].width) / 2).toFloat()
        yPos = ((height - frames[currentFrameIndex].height) / 2).toFloat()
        if(context is LifecycleOwner)
        {
            animationJob = context.lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    delay(100)  // Wait for 100ms
                    currentFrameIndex = (currentFrameIndex + 1) % frames.size  // Update frame
                }
            }
        }
    }



    fun updatePosition(x: Float, y: Float) {
        // Thiết lập vị trí đích
        targetX = x - frames[currentFrameIndex].width / 2
        targetY = y - frames[currentFrameIndex].width / 2
    }

    fun getBoundingBox(): RectF {
        return RectF(
            xPos + frames[currentFrameIndex].width / 4,
            yPos,
            xPos + frames[currentFrameIndex].width,
            yPos + frames[currentFrameIndex].height / 4 * 3
        )
    }

    fun getExactPosition(): PointF
    {
        return PointF(
            xPos + frames[currentFrameIndex].width / 2,
            yPos + frames[currentFrameIndex].height / 2
        )
    }

    fun getStuck(context: Context)
    {
        if(!isStuck)
        {
            if(context is LifecycleOwner)
            {
                stuckJob = context.lifecycleScope.launch(Dispatchers.Main) {
                    isStuck = true
                    interpolationFactor -= 0.05f
                    delay(2000)
                    interpolationFactor += 0.05f
                    isStuck = false
                }
            }
        }
    }


//    fun start(width: Int, height: Int) {
//        frame = Bitmap.createScaledBitmap(frame[currentFrameIndex], width / 3, width / 3, true)
//        xPos = ((width - frame[currentFrameIndex].width) / 2).toFloat()
//        yPos = ((height - frame[currentFrameIndex].height) / 2).toFloat()
//    }
}
