package com.newage.plantedaqua.helpers

import android.content.Context
import android.graphics.*
import android.graphics.Path.FillType
import android.util.AttributeSet
import android.view.View
import com.newage.plantedaqua.R

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.tan


private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35



class DrawSite @JvmOverloads  constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private lateinit var centerPoint : Point
    var centreX = 0f
    var centreY = 0f
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
        centreX = (height/2).toFloat()
        centreY = (width/2).toFloat()

    }


    private fun calculatePoints(){
        val angle = 30f
        val topAngle = angle  + angle/2
        val cell1PointX = centreX + 200
        val cell1PointY = cell1PointX * tan(topAngle)

    }


    var a = Point(0, 0)
    private var b = Point(0, 200)
    var c = Point(87, 50)

    private val path = Path()

    //y = xtanO


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
//        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
//
//
//        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
//        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
//        paint.color = Color.BLACK
//        canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)
//
//        val labelRadius = radius + RADIUS_OFFSET_LABEL
//        for (i in FanSpeed.values()) {
//            pointPosition.computeXYForSpeed(i, labelRadius)
//            val label = resources.getString(i.label)
//            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
//        }

        path.fillType = FillType.EVEN_ODD
        path.lineTo(b.x.toFloat(), b.y.toFloat())
        path.lineTo(c.x.toFloat(), c.y.toFloat())
        path.lineTo(a.x.toFloat(), a.y.toFloat())
        path.close()

        canvas.drawPath(path, paint)


    }
}