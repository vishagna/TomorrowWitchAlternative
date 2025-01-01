package com.example.theworldrequiem.ExtendView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

import com.google.mlkit.vision.pose.Pose

class HandLandmarkView (context: Context?, attrs: AttributeSet) : View(context, attrs) {
    private val lock = Any()
    private lateinit var imageRect: Rect

    public fun updateImageRect (rect: Rect)
    {
        imageRect = rect
    }

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    fun clear() {
        synchronized(lock) {
        }
        postInvalidate()
    }

    fun add(pose: Pose) {
        synchronized(lock) {
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock){
        }
    }

    private fun flipPointHorizontally(point: PointF): PointF {
        val centerX = this.width.toFloat() / 2
        return PointF(2 * centerX - point.x, point.y)
    }

    private fun mapPointToOverlay(point: PointF): PointF {
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
}

