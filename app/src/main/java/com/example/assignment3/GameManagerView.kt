/*
William Ritchie
CS 646
Assignment 3
3/19/19
 */
package com.example.assignment3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

// These interfaces are implemented by main activity, and serve as a means of communication between main and the game
// manager view
interface ScoreListener {
    fun updateScore(score: Int)
}

interface LivesListener {
    fun updateLives(lives: Int, gameState: GameState)
}

interface CircleCreationListener {
    fun creatingCircle(isTrue: Boolean)
}

class GameManagerView: View, View.OnTouchListener {
    private lateinit var playerCircle: Player // The black player dot
    private lateinit var sListener: ScoreListener // These listeners essentially becomes main
    private lateinit var lifeListener: LivesListener
    private lateinit var circleCreationListener: CircleCreationListener
    private lateinit var curGameState: GameState

    private val circleStack:Stack<Circle> // Data structure to hold all of the falling circles (not the player)
    private var isInMotion = false // Bool for checking if the circles are in motion
    private val screenSize: Point
    private var playerInMotion = false
    private var playerIsCreated = false
    private var furthestCircle: Circle // Some aspects of the game are contingent on when the circle highest up on the screen reaches the bottom of the screen
    private var circleSpeed = 2f // The amount the circles move, this increases by 25% each time the furthest circle reach the bottom of the screen
    private var score = 0
    private var scoreIncrementer = 1 // Use to increment the score because the value of circles increase with each pass
    private var lives = 3
    private var resetLength = 0f // Is the radius of the largest circle, used both in measuring when to reset a circle
    // to the top of the screen, and when this happens it also provides the standard starting point for all circles
    // This standard point for all circles is important to maintain the original placement of the circles relative to each other

    private var leftRightCenter: Direction = Direction.CENTER
    // Used to keep track of which side the user has touched, where center really serves as a do nothing state
    enum class Direction {
        LEFT, RIGHT, CENTER
    }

    constructor(context: Context): super(context) {
        setOnTouchListener(this)
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {setOnTouchListener(this)}

    // Used to provide color to the circles and player
    companion object {
        val black: Paint = Paint()
        val white: Paint = Paint()
    }

    init {
        black.color = Color.BLACK
        white.color = Color.WHITE
        circleStack = Stack()
        screenSize = Point()
        furthestCircle = Circle(0f,0f,0f,white)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // This check is because of the life cycle of a view, a views width and height doesn't seem to be known when it is
        // initialized, but is known at the onDraw point in its lifecycle
        if (!playerIsCreated) {
            screenSize.set(this.width, this.height)
            furthestCircle.setY(this.height.toFloat()) // This provides a (nonZero) ground zero for the furthest circle
            playerCircle = Player(screenSize.x * 0.50f, screenSize.y * 0.75f, 30f, black)
            playerIsCreated = true // controls this one time check
        }

        // Render the circles and player
        for (circle in circleStack) {
            circle.drawOn(canvas)
        }
        playerCircle.drawOn(canvas)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        val actionCode = action?.and(MotionEvent.ACTION_MASK)

        // This check is for when the player touches the screen with one finger, OR if the player had previously touched
        // the screen with multiple fingers and now only one finger remains (which allows the player object to continue
        // moving in the direction specified by the last remaining finger
        if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            // Only want to grow circles specifically on actionDown, not action move
            if (actionCode == MotionEvent.ACTION_DOWN) {
                if (curGameState == GameState.NewGame) {
                    if (!isInMotion) {
                        circleCreationListener.creatingCircle(true)
                        circleStack.push(Circle(event.x, event.y, 0f, white))
                        circleStack.peek().grow(this)

                        // Keep track of the circle closest to the top of the screen
                        if (event.y < furthestCircle.getY()) {
                            furthestCircle = circleStack.peek()
                        }
                    }
                }
            }

            // Handles if the user has touched the right or left side of the screen
            if (playerInMotion) {
                if (event.x > screenSize.x / 2) {
                    leftRightCenter = Direction.RIGHT
                }
                else {
                    leftRightCenter = Direction.LEFT
                }
            }

        }
        else if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_CANCEL || actionCode == MotionEvent.ACTION_POINTER_DOWN) {
            // Stop growing
            if(!isInMotion){
                if (curGameState == GameState.NewGame) {
                    circleCreationListener.creatingCircle(false)
                    circleStack.peek().stopGrowing()
                    if (circleStack.peek().getRadius() > resetLength) {
                        resetLength = circleStack.peek().getRadius()
                    }
                }
            }
            else {
                leftRightCenter = Direction.CENTER // puts the player object in a non moving state
            }
        }

        return true
    }

    fun setScoreListener(listener: ScoreListener) {
        sListener = listener
    }

    fun setLivesListener(listener: LivesListener) {
        lifeListener = listener
    }

    fun setCircleCreationListener(listener: CircleCreationListener) {
        circleCreationListener = listener
    }

    fun updateGameState(gameState: GameState) {
        curGameState = gameState
    }

    // Resets the necessary variables to put the game back into its initial state
    fun reset() {
        circleStack.clear()
        lives = 3
        score = 0
        scoreIncrementer = 1
        curGameState = GameState.NewGame
        lifeListener.updateLives(lives, curGameState)
        sListener.updateScore(score)
        playerCircle.setX(screenSize.x * 0.50f)
        playerCircle.setY(screenSize.y * 0.75f)
        leftRightCenter = Direction.CENTER
        circleSpeed = 2f
        furthestCircle = Circle(0f,this.height.toFloat(),0f,white)
        invalidate()
    }

    fun start() {
        isInMotion = true
        playerInMotion = true
        move()
    }

    fun stop() {
        isInMotion = false
        playerInMotion = false
    }

    private fun move() {
        when(leftRightCenter) {
            Direction.LEFT -> playerCircle.decrementX(5f)
            Direction.RIGHT -> playerCircle.incrementX(5f)
            Direction.CENTER -> {}//Do nothing
        }

        for (circle in circleStack) {
            circle.incrementY(circleSpeed)
            if (collision(circle)) {
                if (playerCircle.collide(circle)) {
                    lives--
                    if (0 == lives) {
                        stop()
                        curGameState = GameState.EndGame
                        val toast = Toast.makeText(context, "GAME OVER!", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0,5)
                        toast.show()
                    }
                    lifeListener.updateLives(lives, curGameState)
                }
            }
        }

        invalidate()
        resetOnOutOfBounds()
        checkPlayerBounds()
        // This is what causes the move function to repeatedly occur
        if (isInMotion) {
            this.post{move()}
        }
    }

    private fun collision(circle: Circle): Boolean {
        // distance formula between two points, where the two points are the center of a circle and the center of the player
        val distance = sqrt((playerCircle.getX() - circle.getX()).pow(2) + (playerCircle.getY() - circle.getY()).pow(2))
        return distance <= playerCircle.getRadius() + circle.getRadius()
    }

    private fun resetOnOutOfBounds() {
        for (circle in circleStack) {
            if (yIsOutOfBounds(circle)) {
                circle.setY(0f - resetLength) // This resets the circles to beyond the top of the screen
                if (playerCircle.hasCollided(circle)){
                    playerCircle.resetCollision(circle)
                }
                else {
                    score += scoreIncrementer
                    sListener.updateScore(score)
                }

                if (circle == furthestCircle) {
                    circleSpeed += circleSpeed * 0.25f // 25% faster
                    scoreIncrementer++
                }
            }
        }
    }

    private fun yIsOutOfBounds(circle: Circle): Boolean {
        return circle.getY() > screenSize.y + resetLength
    }

    // Blocks player from moving beyond the screen in the X direction
    private fun checkPlayerBounds() {
        if (playerCircle.getX() <= 0) {
            playerCircle.blockXDirection(Direction.LEFT)
        }
        else if (playerCircle.getX() >= screenSize.x) {
            playerCircle.blockXDirection(Direction.RIGHT)
        }
        else {
            playerCircle.unblockXDirection()
        }
    }
}