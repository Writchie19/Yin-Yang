/*
William Ritchie
CS 646
Assignment 3
3/19/19
 */
package com.example.assignment3

import android.graphics.Paint

class Player(startX: Float, startY: Float, radius: Float = 5f, circleColor: Paint) : Circle(startX, startY,radius,circleColor) {
    // There are two booleans used for blocking x direction movement, this is because you still want to allow the user to move
    // back the direction they came from when after they are blocked
    private var blockXDecrement = false // blocks movement beyond the left side of the screen
    private var blockXIncrement = false // blocks movement beyond the right side of the screen
    private var recentCollisions = ArrayList<Circle>() // Used to keep track of circles that have recently collided with the player

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

    // Check for collisions, if its a new collision then remember that circle
    // Where true in this case means there is a collision, and false no collision
    fun collide(circle: Circle): Boolean {
        if (!recentCollisions.contains(circle)) {
            recentCollisions.add(circle)
            return true
        }
        return false
    }

    fun hasCollided(circle: Circle): Boolean {
        return recentCollisions.contains(circle)
    }

    fun resetCollision(circle: Circle) {
        recentCollisions.remove(circle)
    }
}