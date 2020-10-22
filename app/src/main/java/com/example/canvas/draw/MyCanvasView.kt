package com.example.canvas.draw

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.canvas.R
import kotlin.math.abs

class MyCanvasView(context: Context) : View(context) {
    private lateinit var frame: Rect

    //These two variables are used to store the drawing
//    private lateinit var extraCanvas: Canvas
//    private var extraBitmap: Bitmap? = null

    //I do prefer to store the shape as is
    // Path representing the drawing so far
    private val drawing = Path()

    // Path representing what's currently being drawn
//    private val curPath = Path()

    private var currentPath = Path()

    //Attributes
    private val backgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private val strokeWidth = 12f
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // returns the distance in pixels a touch can wander before the system thinks the user is scrolling.
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    /**
     * After the user stop moving and lifts their touch, these are the starting point for the next path
     */
    private var currentX = 0f
    private var currentY = 0f

    private val paint = Paint().also {
        it.color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        it.isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        // Dithering is the process of juxtaposing pixels of two colors to create the illusion that
        // a third color is present.
        it.isDither = true
        it.style = Paint.Style.STROKE
        it.strokeJoin = Paint.Join.ROUND
        it.strokeCap = Paint.Cap.ROUND
        it.strokeWidth = this.strokeWidth
    }

    private val framePaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    /**
     * This method is called by system whenever a view changes size. This method is also called after
     * the activity first creates and inflates it.
     *
     * a new bitmap and canvas are created every time the function executes
     */
    override fun onSizeChanged(width: Int, heigth: Int, oldWidth: Int, oldHeigth: Int) {
        super.onSizeChanged(width, heigth, oldWidth, oldHeigth)
//        extraBitmap?.recycle() // Avoid memory leaks since a bitmap is created each time this method is called

        //Setup Canvas and Bitmap
        //ARGB_8888 -> bitmap color configuration, stores each color in 4 bytes
//        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        extraBitmap?.let {
//            extraCanvas = Canvas(it).also { canvas -> canvas.drawColor(backgroundColor) }
//        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //The canvas that is passed by the system is different than the one we created and use to
        // draw the bitmap
//        extraBitmap?.let {
//
//            // The 2D coordinates used to draw on canvas is in pixels and the origin (0,0) is at
//            // top left corner of the canvas
//            canvas?.drawBitmap(it, 0f, 0f, null)
//        }

        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
        canvas?.apply {
            drawColor(backgroundColor)
            
            //Draw the drawing so far
            drawPath(drawing, paint)

            //Draw any current squiggle
            drawPath(currentPath, paint)

            drawRect(frame, framePaint)
            drawText("My Notes", width / 2f, inset + 60f, textPaint)
        }
    }

    /**
     * Here we will cache x and y coordinates
     *
     * Take a look to MotionEvent
     * https://developer.android.com/reference/kotlin/android/view/MotionEvent.html
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (event != null) {
            motionTouchEventX = event.x
            motionTouchEventY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStart()
                MotionEvent.ACTION_MOVE -> touchMove()
                MotionEvent.ACTION_UP -> touchUp()
            }

            true
        } else super.onTouchEvent(event)
    }

    /**
     * User first touches the screen
     */
    private fun touchStart() {
        currentPath.reset()
        currentPath.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    /**
     * User writing
     * There is no need to draw every pixel, so interpolate path between points (for performance)
     * - If the finger has barely moved, there is no need to draw.
     * - If the finger has moved less than the touchTolerance distance, don't draw.
     */
    private fun touchMove() {
        // Calculate traveled distance
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        //create a curve between two points and store it on path
        if (dx > touchTolerance || dy > touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            currentPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            // Draw the path in the extra bitmap to cache it.
//            extraCanvas.drawPath(path, paint)
        }

        //invalidate to force redraw
        invalidate()
    }

    /**
     * User finish writing
     */
    private fun touchUp() {
        // Add the current path to the drawing so far
        drawing.addPath(currentPath)
        // Rewind the current path for the next touch
        currentPath.reset()
    }
}