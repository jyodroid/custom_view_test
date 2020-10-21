package com.example.canvas.draw

import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.R

class DrawActivity : AppCompatActivity() {

    /**
     * We cannot determine the size of the view onCreate
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myCanvasView = MyCanvasView(this).also {
            it.contentDescription = getString(R.string.canvasContentDescription)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                it.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
            }
        }

        setContentView(myCanvasView)
    }
}