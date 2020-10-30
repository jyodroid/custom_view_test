package com.example.canvas.findme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.canvas.R
import kotlin.math.floor
import kotlin.random.Random

class SpotLightImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val spotlight = BitmapFactory.decodeResource(resources, R.drawable.mask)
    private var shader: Shader
    private var paint = Paint()

    private var shouldDrawSpotLight = false
    private var gameOver = false

    private lateinit var winnerRect: RectF
    private var androidBitmapX = 0f
    private var androidBitmapY = 0f

    private val shaderMatrix = Matrix()

    //Android image and mask
    private val bitmapAndroid = BitmapFactory.decodeResource(resources, R.drawable.android)

    init {
        val bitmap = Bitmap.createBitmap(spotlight.width, spotlight.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val sharedPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw in the paint a black rectangle that will be composed with spotlight
        sharedPaint.color = Color.BLACK
        canvas.drawRect(
            0.0f,
            0.0f,
            spotlight.width.toFloat(),
            spotlight.height.toFloat(),
            sharedPaint
        )

        // PorterDuff.Mode: Provides several Alpha composing and blending modes.
        // Alpha composing is the process of combining a source of image with destination image to create
        // the appearance of partial or full transparency of a color. that is, for its rgb channels

        //Compose the rectangle and the spotlight with DST_OUT mode
        sharedPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(spotlight, 0.0f, 0.0f, sharedPaint)

        //The tiling mode defined in the shader, specifies how the bitmap drawable is repeated or
        // mirrored in the x or y position
        // Android provides :
        // REPEAT
        // CLAMP(The edge colors will be used to fill the extra space outside of the shader's image bounds)
        // MIRROR
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Experiments: (mirror and repeat are similar since image is symmetric)
        //1. TileMode Repeat on y
//        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT)
        //2. TileMode Repeat on x
//        shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        //3. TileMode Repeat on x and y
//        shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        //4. TileMode Mirror on y seems equals to Repeat on y
//        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.MIRROR)

        // A Paint contains an instance of shader
        // A shader can be:
        // - LinearGradient
        // - RadialGradient
        // - SweepGradient
        // - ComposeShader
        // - BitmapShader
        paint.shader = shader
    }

    override fun onSizeChanged(width: Int, heigth: Int, oldWidth: Int, oldHeigth: Int) {
        super.onSizeChanged(width, heigth, oldWidth, oldHeigth)
        setupWinnerRect()
    }

    //Test for drawing the created texture
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawColor(Color.WHITE)
            drawBitmap(bitmapAndroid, androidBitmapX, androidBitmapY, paint)

            if (!gameOver) {
                if (shouldDrawSpotLight) {
                    drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
                } else {
                    drawColor(Color.BLACK)
                }
            }
        }

//        canvas?.apply {
//            drawColor(Color.YELLOW)
        //Paint the texture the same size as image
//            drawRect(0.0f, 0.0f, spotlight.width.toFloat(), spotlight.height.toFloat(), paint)

        //Translate
//            shaderMatrix.setTranslate(
//                100f,
//                550f
//            )
//            shader.setLocalMatrix(shaderMatrix)

        //Experiments
//            canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat()/2, paint)
//            drawColor(Color.CYAN)
//            canvas.drawCircle(width.toFloat() / 2, height.toFloat() / 2, width.toFloat() / 2, paint)

        //If the painted rectangle is larger than the texture
//            canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
//        }
    }

    private fun setupWinnerRect() {
        androidBitmapX = floor(Random.nextFloat() * (width - bitmapAndroid.width))
        androidBitmapY = floor(Random.nextFloat() * (height - bitmapAndroid.height))

        winnerRect = RectF(
            androidBitmapX,
            androidBitmapY,
            androidBitmapX + bitmapAndroid.width,
            androidBitmapY + bitmapAndroid.height
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val motionEventX = event?.x ?: 0f
        val motionEventY = event?.y ?: 0f

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                shouldDrawSpotLight = true
                if (gameOver) {
                    gameOver = false
                    setupWinnerRect()
                }
            }
            MotionEvent.ACTION_UP -> {
                shouldDrawSpotLight = false
                gameOver = winnerRect.contains(motionEventX, motionEventY)
            }
        }

        shaderMatrix.setTranslate(
            motionEventX - spotlight.width / 2.0f,
            motionEventY - spotlight.height / 2.0f
        )
        shader.setLocalMatrix(shaderMatrix)
        invalidate()
        return true
    }
}