package com.example.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.canvas.FanSpeed.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
    private var fanSpeed = OFF //The active selection

    private var fansSpeedLowColor = 0
    private var fansSpeedMediumColor = 0
    private var fansSpeedMaxColor = 0


    init {
        //In order to use custom view attributes, we need to obtain them
        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fansSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fansSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fansSpeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }

        isClickable = true
    }

    /**
     * The default on perform click also calls `onClickListener()` so actions can be added here
     */
    override fun performClick(): Boolean {
        if (super.performClick()) return true //must happen first, enables accessibility events as well calls onClickListener()

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)

        invalidate() //Force onDraw again
        return true
    }

    //Possible variable which will be used to draw label and indicator circle position
    private val pointPosition = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    /**
     * This method is called every time the view's size change including when view is inflating
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    /**
     * this method is called every time the screen refreshes. For performance we should do as
     * little work as possible here and don't place allocations
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = when (fanSpeed) {
            OFF -> Color.GRAY
            LOW -> fansSpeedLowColor
            MEDIUM -> fansSpeedMediumColor
            HIGH -> fansSpeedMaxColor
        }

        // With and height are part of the view superclass and indicates the current dimension
        // of the view
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        //Draw smaller circles for the indicators
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)

        paint.color = Color.BLACK

        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

        // To draw the labels we reuse the pointPosition so avoid allocations
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        values().forEach { speed ->
            pointPosition.computeXYForSpeed(speed, labelRadius)
            val label = resources.getString(speed.label)
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    /**
     * On measure is used when apps have a deep view hierarchy to accurately define how you custom
     * view fits into the layout
     * https://developer.android.com/guide/topics/ui/custom-components.html#compound
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * Calculates x and y positions on the screen for the texts label given the current fan speed
     */
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        //Angles are in radians
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }
}

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

