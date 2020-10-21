package com.example.canvas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.draw.DrawActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.draw_button).setOnClickListener {
            Intent(this, DrawActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}