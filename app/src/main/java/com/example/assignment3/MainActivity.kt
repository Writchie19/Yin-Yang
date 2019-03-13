package com.example.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener{onClick(button1)}
        val display = windowManager.defaultDisplay
        display.getSize(game_manager.getScreenSize())
    }

    private fun onClick(button: Button) {
        // Toast.makeText(this, "press", Toast.LENGTH_SHORT).show()
        when(button.text) {
            "Start" -> {game_manager.start(); button.text = "Stop"}
            "Stop" -> {game_manager.stop(); button.text = "Start"}
        }
    }
}
