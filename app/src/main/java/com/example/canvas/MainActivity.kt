package com.example.canvas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.clipping.ClippingActivity
import com.example.canvas.draw.DrawActivity
import com.example.canvas.findme.FindMe

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.draw_button).setOnClickListener {
            Intent(this, DrawActivity::class.java).also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.clipping).setOnClickListener {
            Intent(this, ClippingActivity::class.java).also {
                startActivity(it)
            }
        }

        findViewById<Button>(R.id.find_me).setOnClickListener {
            Intent(this, FindMe::class.java).also {
                startActivity(it)
            }
        }
    }
}