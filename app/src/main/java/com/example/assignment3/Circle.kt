package com.example.assignment3

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Circle(private var startX: Float, private var startY: Float, private var radius: Float = 5f, private var circleColor: Paint){
    private lateinit var scheduler: ScheduledExecutorService
    private lateinit var task: ScheduledFuture<*>
    fun drawOn(canvas: Canvas?) {
        canvas?.drawCircle(startX, startY, radius, circleColor)
    }

    fun setX(newX:Float) {
        startX = newX
    }

    fun setY(newY:Float) {
        startY = newY
    }

    fun getX(): Float {
        return startX
    }

    fun getY(): Float {
        return startY
    }

    fun getRadius(): Float {
        return radius
    }

    fun grow(view: View) {
        scheduler = Executors.newScheduledThreadPool(1)
        task = scheduler.scheduleAtFixedRate({radius++; view.invalidate()},0, 5,TimeUnit.MILLISECONDS)
    }

    fun stopGrowing() {
        task.cancel(true)
        scheduler.shutdown()
    }

    fun moveX(moveRight: Boolean, view: View) {
        scheduler = Executors.newScheduledThreadPool(1)
        if (moveRight) {
            task = scheduler.scheduleAtFixedRate({ startX+=2; view.invalidate() }, 0, 5, TimeUnit.MILLISECONDS)
        }
        else {
            task = scheduler.scheduleAtFixedRate({ startX-=2; view.invalidate() }, 0, 5, TimeUnit.MILLISECONDS)
        }
    }

    fun stopMovingX() {
        task.cancel(true)
        scheduler.shutdown()
    }
}