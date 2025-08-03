package io.github.colemakmods.keyboard_companion.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CoordTransform {
    /**
     * Transformation of coordinates - rotate then translate.
     * Represent change of keyboard coordinate system from straight home-row to a column-staggered arrangement
     */
    fun main(args: Array<String>) {
        val angle = -10 * PI / 180
        val points: MutableList<Coord> = ArrayList()
        points.add(Coord(-2.0, -0.0533))
        points.add(Coord(-1.0, +0.2667))
        points.add(Coord(0.0, -0.0))
        points.add(Coord(1.0, -0.1667))

        //rotate
        println("Rotate:")
        for (pt in points) {
            pt.rotate(angle)
            println("point: $pt")
        }
        val ctr = getCentre(points)

        //translate to centre vertically
        println("Translate:")
        for (pt in points) {
            pt.translate(0.0, -ctr.y)
            println("point: $pt")
        }
    }

    private fun getCentre(points: List<Coord>): Coord {
        var totalx = 0.0
        var totaly = 0.0
        for (pt in points) {
            totalx += pt.x
            totaly += pt.y
        }
        return Coord(totalx / points.size, totaly / points.size)
    }

    inner class Coord(var x: Double, var y: Double) {

        fun translate(dx: Double, dy: Double) {
            x += dx
            y += dy
        }

        fun rotate(angle: Double) {
            val sintheta = sin(angle)
            val costheta = cos(angle)
            val x1 = x * costheta + y * sintheta
            val y1 = -x * sintheta + y * costheta
            x = x1
            y = y1
        }

        override fun toString(): String {
            return "($x,$y)"
        }
    }
}