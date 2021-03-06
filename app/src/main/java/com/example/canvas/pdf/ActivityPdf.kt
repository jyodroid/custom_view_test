package com.example.canvas.pdf

import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.R
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnDrawListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs


private const val STROKE_WIDTH = 12f
private const val DRAW_COLOR = Color.BLUE
private const val PDF_FILE = "sample.pdf"

class ActivityPdf : AppCompatActivity(), OnPageChangeListener, OnDrawListener {
    private val paint = Paint().apply {
        color = DRAW_COLOR
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    private var path = Path()

    var pdfView: PDFView? = null

    var drawMap = HashMap<Int, Bitmap>()

    private var currentX = 0f
    private var currentY = 0f

    private lateinit var extraCanvas: Canvas
    private var extraBitmap: Bitmap? = null

    private var writeMode = false
    private var excludedRect: RectF? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        pdfView = findViewById(R.id.pdfView)
        pdfView?.let { configurePdf(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.pdf_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.write -> {
                writeMode = !writeMode
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        extraBitmap?.recycle()
        extraBitmap = drawMap[page]
        pdfView?.let {
            Snackbar.make(it, "page ${page + 1}  of $pageCount", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onLayerDrawn(
        canvas: Canvas?,
        pageWidth: Float,
        pageHeight: Float,
        displayedPage: Int
    ) {
        if (canvas == null) return

        if (extraBitmap == null){
            extraBitmap = Bitmap.createBitmap(pageWidth.toInt(), pageHeight.toInt(), Bitmap.Config.ARGB_8888)
            extraBitmap?.let {
                extraCanvas = Canvas(it)
            }

            excludedRect = RectF(
                0f,
                0f,
                pageWidth,
                calculateExtraHeight().toFloat()
            )
        }

        extraBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
            drawMap[displayedPage] = Bitmap.createBitmap(it)
        }

        val controlBitmap =
            Bitmap.createBitmap(
                pageWidth.toInt(),
                pageHeight.toInt(),
                Bitmap.Config.ARGB_8888
            )
        Canvas(controlBitmap).also {
            val controlPaint = Paint().apply {
                isAntiAlias = true
                strokeWidth = resources.getDimension(R.dimen.strokeWidth)
                color = Color.RED
                textSize = 48f
            }
            it.drawText(
                "Eduac Couse: The course Page ${displayedPage + 1}",
                100f, 100f,
                controlPaint
            )

            controlPaint.color = Color.GREEN
            controlPaint.style = Paint.Style.STROKE
            it.drawCircle(500f, 500f, 100f, controlPaint)
        }
        canvas.drawBitmap(controlBitmap, 0f, 0f, null)
    }

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return if (event != null && writeMode && excludedRect?.contains(event.x, event.y) != true) {
            motionTouchEventX = event.x
            motionTouchEventY = event.y - calculateExtraHeight()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStart()
                MotionEvent.ACTION_MOVE -> touchMove()
                MotionEvent.ACTION_UP -> touchUp()
            }
            true
        } else super.dispatchTouchEvent(event)
    }

    /**
     * User first touches the screen
     */
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

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
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        val touchTolerance = ViewConfiguration.get(this).scaledTouchSlop

        if (dx > touchTolerance || dy > touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            extraCanvas.drawPath(path, paint)
        }

        pdfView?.invalidate()
    }

    /**
     * User finish writing
     */
    private fun touchUp() {
        path.reset()
    }

    private fun configurePdf(pdfView: PDFView) {
        pdfView.fromAsset(PDF_FILE)
            .onPageChange(this)
            .enableAnnotationRendering(true)
//            .onLoad(this)
//            .scrollHandle(DefaultScrollHandle(this))
//                .spacing(10) // in dp
            .onDraw(this)
//            .onPageError(this)
//            .onRender(onRenderListener) // called after document is rendered for the first time could help to initialize canvas state
            .enableAnnotationRendering(true)
            .pageFitPolicy(FitPolicy.BOTH)
//            .enableSwipe(true) // allows to block changing pages using swipe
//            .swipeHorizontal(true)//Add Extra space
//            .pageFling(true)
//            .fitEachPage(false)
//            .pageSnap(true)//Add Extra space
            .pageFling(true)
//            .autoSpacing(true)
//            .autoSpacing(true)//Add Extra space
//            .fitEachPage(false)
            .load()
    }

    private fun calculateExtraHeight(): Int {
        var actionBarHeight = 0
        val styledAttributes: TypedArray = this.theme
            .obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        var navigationBarHeight = 0
        var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }

        var statusBarHeight = 0
        resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }

        return actionBarHeight + navigationBarHeight + statusBarHeight
    }
}