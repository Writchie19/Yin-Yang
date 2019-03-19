package com.example.assignment3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class GameManagerView: View, View.OnTouchListener {
    private val circleStack:Stack<Circle>
    private var isInMotion = false
    private val screenSize: Point
    private lateinit var playerCircle: Player
    private var playerInMotion = false
    private var playerIsCreated = false
    private var furthestCircle: Circle
    private var circleSpeed = 5f

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

        if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_MOVE && event?.pointerCount == 1) {
            if (actionCode == MotionEvent.ACTION_DOWN) {
                if (!isInMotion){
                    circleStack.push(Circle(event.x, event.y, 0f, white))
                    circleStack.peek().grow(this)
                    if (event.y < furthestCircle.getY()) {
                        furthestCircle = circleStack.peek()
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
                circleStack.peek().stopGrowing()
            }
            else {
                leftRightCenter = Direction.CENTER
            }
        }

        return true
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

    var count = 0
    lateinit var sListen: ScoreListener

    interface ScoreListener {
        fun updateScore(score: Int)
    }

    fun setScoreListener(event: ScoreListener) {
        sListen = event
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
                    count++
                    sListen.updateScore(count)
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
                circle.setY(0f - circle.getRadius())
                if (!playerCircle.collide(circle)){
                    playerCircle.resetCollision(circle)
                }

                if (circle == furthestCircle) {
                    circleSpeed += circleSpeed * 0.25f
                }
            }
        }
    }

    private fun yIsOutOfBounds(circle: Circle): Boolean {
        return circle.getY() > screenSize.y + circle.getRadius()
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