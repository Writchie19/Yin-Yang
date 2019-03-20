/*
William Ritchie
CS 646
Assignment 3
3/19/19
 */
package com.example.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

// Used to help manage different states of the game
enum class GameState {
    NewGame, Running, EndGame
}

class MainActivity : AppCompatActivity(), ScoreListener, LivesListener, CircleCreationListener{
    private var gameState = GameState.EndGame
    private var circleCreation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resume_button.setOnClickListener{onResumeClick(resume_button)}
        new_button.setOnClickListener{onNewClick(new_button)}
        game_manager.setScoreListener(this)
        game_manager.setLivesListener(this)
        game_manager.setCircleCreationListener(this)
        game_manager.updateGameState(gameState) //Notifies the game manager view of the current state
    }

    override fun onStop() {
        super.onStop()
        game_manager.stop()
    }

    override fun onRestart() {
        super.onRestart()
        if (gameState == GameState.Running && resume_button.text != getString(R.string.resume)) {
            game_manager.start()
        }
    }

    private fun onResumeClick(button: Button) {
        // Pause and Resume should only work if the game is in the running state, and be rendered useless in the event of
        // any other state
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
                game_manager.reset() // Resets the game to its initial state
                button.text = getString(R.string.start)
                resume_button.text = getString(R.string.pause)
            }
            getString(R.string.start) -> {
                if (!circleCreation) {
                    gameState = GameState.Running
                    game_manager.start()
                    button.text = getString(R.string.end)
                }
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
        // updates the score_view with the current score
        score_view.text = String.format(getString(R.string.score) + "\n     " + score.toString())
    }

    override fun updateLives(lives: Int, gameState: GameState) {
        // updates the lives view with the current number of lives
        lives_view.text = String.format(getString(R.string.lives) + "\n     " + lives.toString())
        this.gameState = gameState //this addresses when the state of game is changed due to the number of lives reaching zero
    }

    // Setting this boolean addresses a bug where the user could create a circle and simultaneously press start, which
    // would cause the circle to grow infinitely, therefore this bool is used ot block start from being pressed while the user
    // is creating a circle
    override fun creatingCircle(isTrue: Boolean) {
        circleCreation = isTrue
    }
}
