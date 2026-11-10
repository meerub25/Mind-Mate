package com.mindmate.wellness.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Visual breathing pacer view with expanding/contracting soothing rings.
 */
class BreathingPacerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var expansionProgress = 0.5f // 0.0 (min size) to 1.0 (max size)

    private val outerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor("#4D5B4FE0")
    }

    private val innerFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#255B4FE0")
    }

    private val centerCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#5B4FE0")
    }

    fun setProgress(progress: Float) {
        this.expansionProgress = progress.coerceIn(0.2f, 1.0f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = (width.coerceAtMost(height) / 2f) - 16f
        val currentRadius = maxRadius * expansionProgress

        // Outer guide ring
        canvas.drawCircle(cx, cy, maxRadius, outerRingPaint)

        // Pulsating soothing body
        canvas.drawCircle(cx, cy, currentRadius, innerFillPaint)

        // Center lotus dot
        canvas.drawCircle(cx, cy, 14f, centerCorePaint)
    }
}
