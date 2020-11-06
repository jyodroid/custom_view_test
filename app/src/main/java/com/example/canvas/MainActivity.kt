package com.example.canvas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.clipping.ClippingActivity
import com.example.canvas.draw.DrawActivity
import com.example.canvas.findme.FindMe
import com.example.canvas.pdf.ActivityPdf

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.draw_button)
            .setOnClickListener { startActivity(DrawActivity::class.java) }

        findViewById<Button>(R.id.clipping)
            .setOnClickListener { startActivity(ClippingActivity::class.java) }

        findViewById<Button>(R.id.find_me).setOnClickListener {
            startActivity(FindMe::class.java)
        }

        findViewById<Button>(R.id.pdf_activity).setOnClickListener {
            startActivity(ActivityPdf::class.java)
        }
    }

    private fun startActivity(target: Class<*>) {
        Intent(this, target).also {
            startActivity(it)
        }
    }
}