package com.example.assignment3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

interface ScoreListener {
    fun updateScore(score: Int)
}

interface LivesListener {
    fun updateLives(lives: Int, gameState: GameState)
}

class GameManagerView: View, View.OnTouchListener {
    private lateinit var playerCircle: Player
    private lateinit var sListener: ScoreListener
    private lateinit var lifeListener: LivesListener
    private lateinit var curGameState: GameState
    private val circleStack:Stack<Circle>
    private var isInMotion = false
    private val screenSize: Point
    private var playerInMotion = false
    private var playerIsCreated = false
    private var furthestCircle: Circle
    private var circleSpeed = 2f
    private var score = 0
    private var scoreIncrementer = 1
    private var lives = 3
    private var resetLength = 0f


    enum class Direction {
        LEFT, RIGHT, CENTER
    }

    private var leftRightCenter: Direction = Direction.CENTER

    constructor(context: Context): super(context) {
        setOnTouchListener(this)
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {setOnTouchListener(this)}

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
        if (!playerIsCreated) {
            screenSize.set(this.width, this.height)
            furthestCircle.setY(this.height.toFloat())
            playerCircle = Player(screenSize.x * 0.50f, screenSize.y * 0.75f, 30f, black)
            playerIsCreated = true
        }
        for (circle in circleStack) {
            circle.drawOn(canvas)
        }
        playerCircle.drawOn(canvas)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        val actionCode = action?.and(MotionEvent.ACTION_MASK)

        if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            if (actionCode == MotionEvent.ACTION_DOWN) {
                if (curGameState == GameState.NewGame) {
                    if (!isInMotion) {
                        circleStack.push(Circle(event.x, event.y, 0f, white))
                        circleStack.peek().grow(this)
                        if (event.y < furthestCircle.getY()) {
                            furthestCircle = circleStack.peek()
                        }
                    }
                }
            }

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
            if(!isInMotion){
                if (curGameState == GameState.NewGame) {
                    circleStack.peek().stopGrowing()
                    if (circleStack.peek().getRadius() > resetLength) {
                        resetLength = circleStack.peek().getRadius()
                    }
                }
            }
            else {
                leftRightCenter = Direction.CENTER
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

    fun updateGameState(gameState: GameState) {
        curGameState = gameState
    }

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
            Direction.CENTER -> {}//Do nothing?
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
        if (isInMotion) {
            this.post{move()}
        }
    }

    private fun collision(circle: Circle): Boolean {
        val distance = sqrt((playerCircle.getX() - circle.getX()).pow(2) + (playerCircle.getY() - circle.getY()).pow(2))
        return distance <= playerCircle.getRadius() + circle.getRadius()
    }

    private fun resetOnOutOfBounds() {
        for (circle in circleStack) {
            if (yIsOutOfBounds(circle)) {
                circle.setY(0f - resetLength)
                score += scoreIncrementer
                sListener.updateScore(score)
                if (playerCircle.hasCollided(circle)){
                    playerCircle.resetCollision(circle)
                }

                if (circle == furthestCircle) {
                    circleSpeed += circleSpeed * 0.25f
                    scoreIncrementer++
                }
            }
        }
    }

    private fun yIsOutOfBounds(circle: Circle): Boolean {
        return circle.getY() > screenSize.y + resetLength
    }

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