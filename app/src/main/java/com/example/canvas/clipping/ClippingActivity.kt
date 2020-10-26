package com.example.canvas.clipping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ClippingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ClippingView(this))
    }
}