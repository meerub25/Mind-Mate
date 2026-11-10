package com.mindmate.wellness.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Custom 7-Day Mood Trend Wave Line Chart.
 * Displays smooth wavy purple line with soft lavender fill,
 * colored dots (green=good, blue=neutral, orange=low),
 * mood emojis, and weekday labels.
 */
class MoodTrendChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scores: List<Float> = listOf(3f, 4f, 2f, 4f, 5f, 3f, 4f)
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#5B4FE0") // MindEase Purple
        pathEffect = CornerPathEffect(32f)
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#30EDE9FE") // Soft lavender fill
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        color = Color.parseColor("#F1EEFB")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9CA3AF")
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 34f
        textAlign = Paint.Align.CENTER
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun setTrendData(points: List<Float>) {
        if (points.isNotEmpty()) {
            this.scores = points
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (scores.isEmpty()) return

        val paddingLeft = 48f
        val paddingRight = 48f
        val paddingTop = 54f
        val paddingBottom = 64f

        val w = width.toFloat() - paddingLeft - paddingRight
        val h = height.toFloat() - paddingTop - paddingBottom

        // Draw 3 subtle guide lines
        for (i in 1..5 step 2) {
            val y = paddingTop + h - ((i - 1f) / 4f) * h
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint)
        }

        val stepX = w / (scores.size - 1).coerceAtLeast(1)
        val path = Path()
        val fillPath = Path()

        val coords = mutableListOf<Pair<Float, Float>>()

        for (i in scores.indices) {
            val s = scores[i].coerceIn(1f, 5f)
            val x = paddingLeft + i * stepX
            val y = paddingTop + h - ((s - 1f) / 4f) * h
            coords.add(Pair(x, y))

            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, paddingTop + h)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        // Close fill area under curve
        fillPath.lineTo(coords.last().first, paddingTop + h)
        fillPath.close()

        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(path, linePaint)

        // Draw colored dots, mood emojis and weekday names
        for (i in coords.indices) {
            val pt = coords[i]
            val score = scores[i]

            // Color: green for good (>=4), blue for neutral (3), orange for low (<=2)
            dotPaint.color = when {
                score >= 4f -> Color.parseColor("#10B981")
                score == 3f -> Color.parseColor("#3B82F6")
                else -> Color.parseColor("#F97316")
            }
            canvas.drawCircle(pt.first, pt.second, 9f, dotPaint)

            val emoji = when {
                score >= 5f -> "✨"
                score >= 4f -> "🙂"
                score == 3f -> "😐"
                score >= 2f -> "😔"
                else -> "😫"
            }
            canvas.drawText(emoji, pt.first, pt.second - 16f, emojiPaint)

            val dayLabel = days[i % days.size]
            canvas.drawText(dayLabel, pt.first, height.toFloat() - 12f, textPaint)
        }
    }
}
