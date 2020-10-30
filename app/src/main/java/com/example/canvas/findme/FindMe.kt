package com.example.canvas.findme

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.canvas.R

class FindMe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_me)
        val dialog = createInstructionsDialog()
        dialog.show()
    }

    private fun createInstructionsDialog(): Dialog {
        return AlertDialog.Builder(this)
            .let {
                it.setIcon(R.drawable.android)
                    .setTitle(R.string.instructions_title)
                    .setMessage(R.string.instructions)
                    .setPositiveButtonIcon(
                        ContextCompat.getDrawable(
                            this,
                            android.R.drawable.ic_media_play
                        )
                    )
            }.create()
    }
}