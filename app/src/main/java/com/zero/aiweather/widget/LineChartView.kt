package com.zero.aiweather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.zero.aiweather.R
import com.zero.aiweather.utils.MathCalculateUtil
import com.zero.aiweather.utils.DensityUtil

class LineChartView(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {
    var lineColor: Int
    var lineWidth: Float
    var lineValueColor: Int
    var lineValueSize: Float
    var pointRadius: Float

    var maxValue = 50

    var lineValues = arrayListOf<Int>()

    private var itemWidth = 0

    private val canvasRect = Rect()
    private val textRect = Rect()

    private var paint: Paint

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LineChartView).run {
            lineColor = getColor(R.styleable.LineChartView_line_color,
                    ContextCompat.getColor(context, R.color.black))
            lineWidth = getDimension(R.styleable.LineChartView_line_width,
                    DensityUtil.dp2px(2f).toFloat())
            lineValueColor = getColor(R.styleable.LineChartView_line_value_color,
                    ContextCompat.getColor(context, R.color.black))
            lineValueSize = getDimension(R.styleable.LineChartView_line_value_size,
                    DensityUtil.sp2px(14f))
            pointRadius = getDimension(
                    R.styleable.LineChartView_point_radius,
                    DensityUtil.dp2px(3f).toFloat())
            recycle()
        }

        paint = Paint().apply {
            strokeWidth = lineWidth
            textAlign = Paint.Align.CENTER
            textSize = lineValueSize
            isAntiAlias = true
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left - paddingRight - paddingLeft
        val height = bottom - top - paddingBottom - paddingTop
        canvasRect.set(paddingLeft, paddingTop, width, height)
        val text = "25"
        paint.getTextBounds(text, 0, text.length, textRect)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val startX = canvasRect.left + itemWidth / 2
        drawLine(canvas, startX)
        drawText(canvas, startX)
    }

    private fun drawLine(canvas: Canvas?, startX: Int) {
        paint.style = Paint.Style.STROKE
        paint.color = lineColor
        for (i in lineValues.indices) {
            canvas?.drawCircle(
                    (startX + itemWidth * i).toFloat(),
                    getPointY(lineValues[i]),
                    pointRadius,
                    paint)
            if (i != lineValues.lastIndex) {
                canvas?.drawLine(
                        (startX + itemWidth * i + pointRadius),
                        getPointY(lineValues[i]),
                        (startX + itemWidth * (i + 1) - pointRadius),
                        getPointY(lineValues[i + 1]),
                        paint)
            }
        }
    }

    private fun drawText(canvas: Canvas?, startX: Int) {
        paint.style = Paint.Style.FILL
        paint.color = lineValueColor
        for (i in lineValues.indices) {
            canvas?.drawText(
                    lineValues[i].toString(),
                    (startX + itemWidth * i).toFloat(),
                    getPointY(lineValues[i]) - pointRadius * 2,
                    paint)
        }
    }

    private fun getPointY(value: Int): Float = (canvasRect.height() / 2
            - (canvasRect.height() / 2 - textRect.height() - pointRadius * 2)
            * MathCalculateUtil.div(value.toDouble(), maxValue.toDouble())
            ).toFloat()

    fun notifyLineChart() {
        itemWidth = canvasRect.width() / lineValues.size
        postInvalidate()
    }
}