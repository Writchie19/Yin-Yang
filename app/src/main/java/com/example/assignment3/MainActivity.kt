package com.example.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener{onClick(button1)}
    }

    private fun onClick(button: Button) {
        when(button.text) {
            "Start" -> {game_manager.start(); button.text = "Stop"} //change this
            "Stop" -> {game_manager.stop(); button.text = "Start"} // change this
        }
    }
}
