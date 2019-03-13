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

    constructor(context: Context): super(context) {
        setOnTouchListener(this)
    }
    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {setOnTouchListener(this)}

    companion object {
        val black: Paint = Paint()
    }

    init {
        black.color = Color.BLACK
        circleStack = Stack()
        screenSize = Point()
    }

    fun getScreenSize(): Point {
        return screenSize
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.RED)

        for (circle in circleStack) {
            circle.drawOn(canvas)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        val actionCode = action?.and(MotionEvent.ACTION_MASK)

        if (actionCode == MotionEvent.ACTION_DOWN) {
            circleStack.push(Circle(event.x, event.y, 0f, black))
            circleStack.peek().grow(this)
        }
        else if (actionCode == MotionEvent.ACTION_UP) {
            circleStack.peek().stopGrowing()
        }
        return true
    }

    fun start() {
        isInMotion = true
        move()
    }

    fun stop() {
        isInMotion = false
    }

    private fun move() {
        for (circle in circleStack) {
            circle.setY(circle.getY() + 1f)
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