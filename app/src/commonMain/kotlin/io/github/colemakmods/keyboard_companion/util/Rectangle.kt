package io.github.colemakmods.keyboard_companion.util

import kotlin.math.abs

class Rectangle(private val x0: Double, private val y0: Double, private val x1: Double, private val y1: Double) {
    fun width(): Double {
        return abs(x1 - x0)
    }

    fun height(): Double {
        return abs(y1 - y0)
    }

    fun distanceFrom(xp: Double, yp: Double): Pair<Double, Double> {
        val dx = if (xp < x0) xp - x0 else if (xp > x1) xp - x1 else 0.0
        val dy = if (yp < y0) yp - y0 else if (yp > y1) yp - y1 else 0.0
        return Pair(dx, dy)
    }
}