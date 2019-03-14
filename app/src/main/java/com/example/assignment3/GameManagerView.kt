package com.example.assignment3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class GameManagerView: View, View.OnTouchListener {
    private val circleStack:Stack<Circle>
    private var isInMotion = false
    private val screenSize: Point
    private lateinit var playerCircle: Circle
    private var playerInMotion = false

    constructor(context: Context): super(context) {
        setOnTouchListener(this)
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {setOnTouchListener(this)}

    companion object {
        val black: Paint = Paint()
    }

    init {
        black.color = Color.WHITE
        circleStack = Stack()
        screenSize = Point()
    }

    fun getScreenSize(): Point {
        return screenSize
    }

    fun initializePlayer(){
        playerCircle = Circle(screenSize.x * 0.50f, screenSize.y * 0.5f, 30f, black)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas?.drawColor(Color.RED)

        for (circle in circleStack) {
            circle.drawOn(canvas)
        }
        playerCircle.drawOn(canvas)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        val actionCode = action?.and(MotionEvent.ACTION_MASK)

        if (actionCode == MotionEvent.ACTION_DOWN) {
            if (!isInMotion){
                circleStack.push(Circle(event.x, event.y, 0f, black))
                circleStack.peek().grow(this)
            }
            else{
                if (playerInMotion) {
                    if (event.x > screenSize.x / 2) {
                        playerCircle.moveX(true,this)
                    }
                    else {
                        playerCircle.moveX(false, this)
                    }
                }
            }
        }
        else if (actionCode == MotionEvent.ACTION_UP) {
            if(!isInMotion){
                circleStack.peek().stopGrowing()
            }
            else {
                playerCircle.stopMovingX()
            }
        }
        // action cancel is the bug
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

    private fun move() {
        for (circle in circleStack) {
            circle.setY(circle.getY() + 5f)
        }
        invalidate()
        resetOnOutOfBounds()
        if (isInMotion) {
            this.post{move()}
        }
    }

    private fun resetOnOutOfBounds() {
        for (circle in circleStack) {
            if (yIsOutOfBounds(circle)) circle.setY(0f - circle.getRadius())
        }
    }

    private fun yIsOutOfBounds(circle: Circle): Boolean {
        return circle.getY() > screenSize.y
    }
}