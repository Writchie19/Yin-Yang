package com.example.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

// Address bug: if player loses, can still resume, should be put into endgame state, and auto put end to new?

enum class GameState {
    NewGame, Running, EndGame
}

class MainActivity : AppCompatActivity(), ScoreListener, LivesListener{


    private var gameState = GameState.EndGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resume_button.setOnClickListener{onResumeClick(resume_button)}
        new_button.setOnClickListener{onNewClick(new_button)}
        game_manager.setScoreListener(this)
        game_manager.setLivesListener(this)
        game_manager.updateGameState(gameState)
    }

    private fun onResumeClick(button: Button) {
        if (gameState == GameState.Running) {
            when (button.text) {
                getString(R.string.pause) -> {
                    game_manager.stop(); button.text = getString(R.string.resume)
                }

                getString(R.string.resume) -> {
                    game_manager.start(); button.text = getString(R.string.pause)
                }
            }
        }
    }

    private fun onNewClick(button: Button) {
        when(button.text) {
            getString(R.string.newbutton) -> {
                gameState = GameState.NewGame
                game_manager.reset()
                button.text = getString(R.string.start)
                resume_button.text = getString(R.string.pause)
            }
            getString(R.string.start) -> {
                gameState = GameState.Running
                game_manager.start()
                button.text = getString(R.string.end)
            }
            getString(R.string.end) -> {
                gameState = GameState.EndGame
                game_manager.stop()
                button.text = getString(R.string.newbutton)
            }
        }
        game_manager.updateGameState(gameState)
    }

    override fun updateScore(score: Int) {
        score_view.text = String.format(getString(R.string.score) + "\n     " + score.toString())
    }

    override fun updateLives(lives: Int, gameState: GameState) {
        lives_view.text = String.format(getString(R.string.lives) + "\n     " + lives.toString())
        this.gameState = gameState
    }
}
