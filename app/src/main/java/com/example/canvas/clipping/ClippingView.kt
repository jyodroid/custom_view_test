package com.example.canvas.clipping

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.example.canvas.R

class ClippingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path = Path()

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    //Row and column location
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    //Coordinates for each Row
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)
    private val rejectRow = rowFour + rectInset + 2 * clipRectBottom

    private val rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    /**
     * when use android views views are already clipped
     * when override this method clipping what you draw becomes your responsibility.
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //The basic flow of each method is:
        // 1. Save the current state of the canvas so you can reset to that initial state
        // 2. Translate the origin of the canvas to the location where you want to draw (column, row)
        // 3. Apply clipping shapes and paths
        // 4. Draw the rectangle or text
        // 5. Restore the state of the canvas

        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawTranslatedTextExample(canvas)
        drawSkewedTextExample(canvas)

        //the quickReject() method from canvas return a boolean indicating if the area is visible
        //at all on the screen. we still have to check manually for partial overlaps. The EdgeType
        //parameter, indicates how should be treat edges.
        drawQuickRejectedExample(canvas)
    }

    private fun drawQuickRejectedExample(canvas: Canvas?) {
        if (canvas == null) return

        val inClipRectangle = RectF(
            clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2
        )

        val notInClipRectangle = RectF(
//            RectF(
            clipRectRight + 1,
            clipRectBottom + 1,
            clipRectRight * 2,
            clipRectBottom * 2
//            )
        )

        canvas.apply {
            save()
            translate(columnOne, rejectRow)
            clipRect(
                clipRectLeft, clipRectTop,
                clipRectRight, clipRectBottom
            )

            //with inClipRectangle:
            // Because the two rectangles overlaps so the will returns false and rectangle will be drawn
            //with notInClipRectangle:
            // The rectangle will not be drawn and the rectangle will be fill with a white color
            val rejected =
                //Canvas.EdgeType.AA This enum was deprecated in API level 30. quickReject no longer uses this.
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//                    quickReject(inClipRectangle, Canvas.EdgeType.AA)
                    quickReject(notInClipRectangle, Canvas.EdgeType.AA)
                } else {
//                    quickReject(inClipRectangle)
                    quickReject(notInClipRectangle)
                }

            if (rejected) {
                drawColor(Color.WHITE)
            } else {
                drawColor(Color.BLACK)
                drawRect(inClipRectangle, paint)
            }
            restore()
        }
    }

    private fun drawTranslatedTextExample(canvas: Canvas?) {
        if (canvas == null) return

        // A text can be translated by translating the canvas and drawing the text
        canvas.apply {
            save()
            paint.color = Color.GREEN
            paint.textAlign = Paint.Align.LEFT

            translate(columnTwo, textRow)

            //Draw text
            drawText(context.getString(R.string.translated), clipRectLeft, clipRectTop, paint)
            restore()
        }
    }

    /**
     * distort the text in various ways
     */
    private fun drawSkewedTextExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()
            paint.color = Color.YELLOW
            paint.textAlign = Paint.Align.RIGHT

            translate(columnTwo, textRow)

            skew(0.2f, 0.3f)
            drawText(context.getString(R.string.skewed), clipRectLeft, clipRectTop, paint)

            restore()
        }
    }

    private fun drawOutsideClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()
            translate(columnOne, rowFour)
            clipRect(
                2 * rectInset,
                2 * rectInset,
                clipRectRight - 2 * rectInset,
                clipRectBottom - 2 * rectInset
            )
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()
            translate(columnTwo, rowThree)
            path.rewind()

            // Takes a rectangle, values for x and y, values for the corner radius and and the
            // direction to wind the rounded-rectangle's contour. Path direction specifies how
            // closed shapes are oriented when they are added to the path. CCW stands for
            // counter-clockwise
            path.addRoundRect(
                rectF,
                clipRectRight / 4,
                clipRectRight / 4,
                Path.Direction.CCW
            )
            clipPath(path)
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawCombinedClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()
            translate(columnOne, rowThree)

            path.rewind()
            path.addCircle(
                clipRectLeft + rectInset + circleRadius,
                clipRectTop + circleRadius + rectInset,
                circleRadius, Path.Direction.CCW
            )
            path.addRect(
                clipRectRight / 2 - circleRadius,
                clipRectTop + circleRadius + rectInset,
                clipRectRight / 2 + circleRadius,
                clipRectBottom - rectInset,
                Path.Direction.CCW
            )
            clipPath(path)
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawIntersectionClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()

            translate(columnTwo, rowTwo)
            clipRect(
                clipRectLeft, clipRectTop,
                clipRectRight - smallRectOffset,
                clipRectBottom - smallRectOffset
            )

            val rect = RectF(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight, clipRectBottom
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipRect(rect, Region.Op.INTERSECT)
            } else {
                clipRect(rect)
            }
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawCircularClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()
            translate(columnOne, rowTwo)

            //Clears any lines and curves from the path, but unlike reset(), keeps the internal data
            //structure for faster reuse
            path.rewind()
            path.addCircle(
                circleRadius, clipRectBottom - circleRadius,
                circleRadius, Path.Direction.CCW
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipPath(path, Region.Op.DIFFERENCE)
            } else {
                clipOutPath(path)
            }
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawDifferenceClippingExample(canvas: Canvas?) {
        if (canvas == null) return

        canvas.apply {
            save()

            //Move the origin to the right for the next rectangle
            translate(columnTwo, rowOne)

            // Use the subtraction of two clipping rectangles to create a frame
            clipRect(
                2 * rectInset, 2 * rectInset,
                clipRectRight - 2 * rectInset,
                clipRectBottom - 2 * rectInset
            )

            val rect = RectF(
                4 * rectInset, 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
            // lipRect(float, float, float, float, Region.Op.DIFFERENCE) was deprecated in API level 26
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipRect(rect, Region.Op.DIFFERENCE)
            } else {
                clipOutRect(rect)
            }

            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawBackAndUnclippedRectangle(canvas: Canvas?) {
        if (canvas == null) return

        //fill the canvas with the background color and draw the original shapes
        canvas.apply {
            drawColor(Color.GRAY)
            // Save the current state of the canvas. The activity context maintains a stack of
            // drawing states which is the current transformation matrix and the current clipping region
            // "When your drawing includes transformations, chaining and undoing transformations by
            // reversing them is error-prone. For example, if you translate, stretch, and then
            // rotate, it gets complex quickly. Instead, save the state of the canvas, apply your
            // transformations, draw, and then restore the previous state."
            save()
            translate(columnOne, rowOne)
            drawClippedRectangle(this)
            restore()
        }

    }

    private fun drawClippedRectangle(canvas: Canvas?) {
        if (canvas == null) return

        //Set boundaries of the clipped rectangle
        //This method reduces the region of the screen that future draw opperations can write to
        canvas.apply {
            clipRect(
                clipRectLeft, clipRectTop,
                clipRectRight, clipRectBottom
            )

            //Background of rect
            canvas.drawColor(Color.WHITE)

            //Draw diagonal line
            paint.color = Color.RED
            drawLine(
                clipRectLeft, clipRectTop,
                clipRectRight, clipRectBottom,
                paint
            )

            //Draw Circle
            paint.color = Color.GREEN
            drawCircle(
                circleRadius, clipRectBottom - circleRadius, circleRadius,
                paint
            )

            //Draw text
            paint.color = Color.BLUE
            paint.textSize = textSize
            paint.textAlign = Paint.Align.RIGHT

            drawText(
                context.getString(R.string.clipping),
                clipRectRight, textOffset,
                paint
            )
        }
    }
}