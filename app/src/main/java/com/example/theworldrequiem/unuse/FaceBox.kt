package com.example.theworldrequiem.unuse

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import com.example.theworldrequiem.ExtendView.FaceOverlayLayout
import com.google.mlkit.vision.face.Face

class FaceBox (overlay: FaceOverlayLayout,
               private val face: Face,
               private val imageRect: Rect) : FaceOverlayLayout.FaceBox(overlay)
{
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6.0f
    }

    private val contourPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 3.0f
    }

    override fun draw(canvas: Canvas?) {
        val rect = getBoxRect(
            imageRectWidth = imageRect.width().toFloat(),
            imageRectHeight = imageRect.height().toFloat(),
            faceBoundingBox = face.boundingBox
        )
        canvas?.drawRect(rect, paint)


        // Vẽ các contour
        val contours = face.allContours
        for (contour in contours) {
            for (point in contour.points) {
                val mappedPoint = mapPointToOverlay(point)
                // Nếu là camera trước, lật các điểm contour
                val flippedPoint = if (true) flipPointHorizontally(mappedPoint) else mappedPoint
                canvas?.drawCircle(flippedPoint.x, flippedPoint.y, 4.0f, contourPaint)
            }
        }
    }

    private fun mapPointToOverlay(point: PointF): PointF {
        val scaleX = overlay.width.toFloat() / imageRect.height().toFloat()
        val scaleY = overlay.height.toFloat() / imageRect.width().toFloat()
        val scale = scaleX.coerceAtLeast(scaleY)

        val offsetX = (overlay.width - imageRect.height() * scale) / 2.0f
        val offsetY = (overlay.height - imageRect.width() * scale) / 2.0f

        // Tính toán lại vị trí của điểm contour
        return PointF(
            point.x * scale + offsetX,
            point.y * scale + offsetY
        )
    }

    private fun flipPointHorizontally(point: PointF): PointF {
        val centerX = overlay.width.toFloat() / 2
        return PointF(2 * centerX - point.x, point.y)
    }

    // Hàm lật ngang một hình chữ nhật qua trục dọc
    private fun flipRectHorizontally(rect: RectF): RectF {
        val centerX = overlay.width.toFloat() / 2
        return RectF(
            2 * centerX - rect.right, // Lật tọa độ phải
            rect.top,
            2 * centerX - rect.left,  // Lật tọa độ trái
            rect.bottom
        )
    }


}