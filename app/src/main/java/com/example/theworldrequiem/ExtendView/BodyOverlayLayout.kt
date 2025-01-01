package com.example.theworldrequiem.ExtendView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.theworldrequiem.databinding.ActivityBodyCameraBinding
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

open class BodyOverlayLayout (context: Context?, attrs: AttributeSet) : View(context, attrs) {
    private val lock = Any()
    private val poses: MutableList<Pose> = mutableListOf()
    private var pointLandmarks: MutableList<PointF> = mutableListOf()
    private var imageRect: Rect = Rect()
    var mScale: Float? = null
    var mOffsetX: Float? = null
    var mOffsetY: Float? = null

    private var canva: Canvas? = null

    fun updateImageRect (rect: Rect)
    {
        imageRect = rect
    }

    private fun flipPointHorizontally(point: PointF): PointF {
        val centerX = this.width.toFloat() / 2
        return PointF(2 * centerX - point.x, point.y)
    }

    private fun mapPointToOverlay(point: PointF): PointF? {
        if(!imageRect.isEmpty)
        {
            val scaleX = this.width.toFloat() / imageRect.height().toFloat()
            val scaleY = this.height.toFloat() / imageRect.width().toFloat()
            val scale = scaleX.coerceAtLeast(scaleY)

            val offsetX = (this.width - imageRect.height() * scale) / 2.0f
            val offsetY = (this.height - imageRect.width() * scale) / 2.0f

            // Tính toán lại vị trí của điểm contour
            return PointF(
                point.x * scale + offsetX,
                point.y * scale + offsetY
            )
        }
        else {
            return null
        }

    }

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    fun clear() {
        synchronized(lock) {
            poses.clear()
        }
        postInvalidate()
    }

    fun add(pose: Pose) {
        synchronized(lock) { poses.add(pose) }
    }

    fun update(pPoseLandmarks: MutableList<PoseLandmark>)
    {
        val newPointF: MutableList<PointF> = mutableListOf()
        for(pose in pPoseLandmarks)
        {
            mapPointToOverlay(PointF(pose.position.x, pose.position.y))?.let { newPointF.add(it) }
        }
        pointLandmarks = newPointF
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (pose in poses) {
                for (landmark in pose.allPoseLandmarks) {
                    val point = mapPointToOverlay(landmark.position)
                    val flippedPoint = if (true) point?.let { flipPointHorizontally(it) } else point
                    if (flippedPoint != null) {
                        if (point != null) {
                            canvas.drawCircle(flippedPoint.x, point.y, 10f, paint)
                        }
                    }
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun updateInteractorPostition(binding: ActivityBodyCameraBinding, thumb: PoseLandmark)
    {
        val x = thumb.position.x
        val y = thumb.position.y
        val pointF = PointF(x,y)
        var newPointF = mapPointToOverlay(pointF)
        if(newPointF != null)
        {
            newPointF = flipPointHorizontally(newPointF)
            binding.gameScene.moveInteractor(newPointF.x, newPointF.y)
        }
    }


    fun getAction(): String
    {
        if(pointLandmarks.isNotEmpty())
        {
            if(checkExist(pointLandmarks[15]) && checkExist(pointLandmarks[13]))
            {
                if(checkExist(pointLandmarks[14]) && checkExist(pointLandmarks[16]))
                {
                    if(pointLandmarks[13].y - pointLandmarks[15].y > pointLandmarks[15].x - pointLandmarks[13].x
                        && pointLandmarks[16].x - pointLandmarks[14].x > pointLandmarks[14].y - pointLandmarks[16].y)
                    {
                        Log.e("Position " , "Plus");
                        pointLandmarks.clear()
                        return "plus"
                    }
                }
                //Vertical
                else if(pointLandmarks[13].y - pointLandmarks[15].y > pointLandmarks[13].x - pointLandmarks[15].x)
                {
                    Log.e("Position " , "Vertical ${pointLandmarks[13].y - pointLandmarks[15].y} : ${pointLandmarks[15].x - pointLandmarks[13].x}");
                    pointLandmarks.clear()
                    return "vertical"
                }
                //Horizontal
                else if(pointLandmarks[13].x - pointLandmarks[15].x > pointLandmarks[13].y - pointLandmarks[15].y)
                {
                    Log.e("Position", "Horizontal ${pointLandmarks[15].x - pointLandmarks[13].x} : ${pointLandmarks[13].y - pointLandmarks[15].y}");
                    pointLandmarks.clear()

                    return "horizontal"
                }
            }
        }
        return "none"
    }

    fun getActionUltimate(): String
    {
        if(pointLandmarks.isNotEmpty())
        {
            if(checkExist(pointLandmarks[16]) && checkExist(pointLandmarks[18])
                        && checkExist(pointLandmarks[15])&& checkExist(pointLandmarks[17]))
            {
                if (calculateDistance(pointLandmarks[17], pointLandmarks[18]) < 200f)
                {
                    Log.e("UltimateCast", "Cast ULTIMATE")
                    return "holy_circle"
                }
            }
        }
        return "none"
    }

    fun checkExist(target: PointF): Boolean
    {
        if(target.x >= width || target.x <= 0
            || target.y >= height || target.y <= 0)
            {
                return false
            }
        else return true
    }




}

fun calculateDistance(point1: PointF, point2: PointF): Float {
    val dx = point2.x - point1.x
    val dy = point2.y - point1.y
    return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
}

