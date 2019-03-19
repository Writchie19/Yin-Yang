package com.example.assignment3

import android.graphics.Paint

class Player(startX: Float, startY: Float, radius: Float = 5f, circleColor: Paint) : Circle(startX, startY,radius,circleColor) {
    private var blockXDecrement = false
    private var blockXIncrement = false
    private var recentCollisions = ArrayList<Circle>()

    override fun decrementX(amount: Float) {
        if (!blockXDecrement) {
            super.decrementX(amount)
        }
    }

    override fun incrementX(amount: Float) {
        if (!blockXIncrement) {
            super.incrementX(amount)
        }
    }

    fun blockXDirection(directionToBlock: GameManagerView.Direction) {
        when(directionToBlock) {
            GameManagerView.Direction.LEFT -> blockXDecrement = true
            GameManagerView.Direction.RIGHT -> blockXIncrement = true
        }
    }

    fun unblockXDirection() {
        blockXDecrement = false
        blockXIncrement = false
    }

    fun collide(circle: Circle): Boolean {
        if (!recentCollisions.contains(circle)) {
            recentCollisions.add(circle)
            return true
        }
        return false
    }

    fun resetCollision(circle: Circle) {
        recentCollisions.remove(circle)
    }
}