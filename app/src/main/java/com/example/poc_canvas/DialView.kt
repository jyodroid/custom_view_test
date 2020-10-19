package com.example.poc_canvas

import android.content.Context
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

/**
 * If you are creating a view from scratch (by extending view), you are responsible for drawing the
 * entire view each time the screen refreshes, and for overriding the View methods that handle
 * drawing
 *  - Calculate view's size when it first appears and each time size change
 *  - Override onDraw()
 *  - Call invalidate(), so whe invalidate on interaction the entire view so the onDraw() is called again
 */

class DialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var radius = 0.0f   //Radius of the circle
    private var fanSpeed = FanSpeed.OFF //The active selection

    //Possible variable which will be used to draw label and indicator circle position
    private val pointPosition = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

}

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

