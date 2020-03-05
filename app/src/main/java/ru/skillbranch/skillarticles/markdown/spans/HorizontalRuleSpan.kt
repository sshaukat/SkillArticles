package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px


class HorizontalRuleSpan(
    @Px
    val ruleWidth: Float,
    @ColorInt
    val ruleColor: Int
) : ReplacementSpan() { // НАследуется ль ReplacementSpan - для замещения части тектса

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return 0
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.forLine {
            canvas.drawLine(
                0f,
                (top+bottom)/2f,
                canvas.width.toFloat(),
                (top+bottom)/2f,
                paint
            )
        }

    }

    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth
        color = ruleColor
        style = Paint.Style.STROKE // просто линия
        strokeWidth = ruleWidth
        block()
        // Восстановим старые настройки
        strokeWidth = oldWidth
        color = oldColor
        style = oldStyle
    }
}