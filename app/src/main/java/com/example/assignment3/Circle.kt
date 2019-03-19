package com.example.assignment3

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

open class Circle(private var startX: Float, private var startY: Float, private var radius: Float = 5f, private var circleColor: Paint){
    open lateinit var scheduler: ScheduledExecutorService
    open lateinit var task: ScheduledFuture<*>
    open fun drawOn(canvas: Canvas?) {
        canvas?.drawCircle(startX, startY, radius, circleColor)
    }

    open fun setX(newX:Float) {
        startX = newX
    }

    open fun setY(newY:Float) {
        startY = newY
    }

    open fun getX(): Float {
        return startX
    }

    open fun getY(): Float {
        return startY
    }

    open fun getRadius(): Float {
        return radius
    }

    open fun grow(view: View) {
        scheduler = Executors.newScheduledThreadPool(1)
        task = scheduler.scheduleAtFixedRate({radius++; view.invalidate()},0, 5,TimeUnit.MILLISECONDS)
    }

    open fun stopGrowing() {
        task.cancel(true)
        scheduler.shutdown()
    }

    open fun incrementX(amount: Float) {
        startX+=amount
    }

    open fun incrementY(amount: Float) {
        startY+=amount
    }

    open fun incrementRadius(amount: Float) {
        radius+=amount
    }

    open fun decrementX(amount: Float) {
        startX-=amount
    }

    open fun decrementY(amount: Float) {
        startY-=amount
    }

    open fun decrementRadius(amount: Float) {
        radius-=amount
    }
}