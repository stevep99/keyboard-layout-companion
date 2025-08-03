package io.github.colemakmods.keyboard_companion.model

import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.util.Constants
import io.github.colemakmods.keyboard_companion.util.Rectangle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val log = Logger.withTag("Key")

/**
 * Created by steve on 15/07/15.
 */
@Serializable
data class Key(
    val id: String,
    val x: Float,
    val y: Float,
    val width: Float = 1f,
    val height: Float = 1f,
    var finger: Int,
    var highlight: Boolean = false,
) {
    @Transient
    val type: KeyType = KeyType.fromId(id)
    @Transient
    var bestFinger: Int = finger

    @Transient
    var touchRect: Rectangle? = null
    @Transient
    var score = 0.0
    @Transient
    var distance = 0.0

    fun toggleHighlight() {
        highlight = !highlight
    }

    private fun ensureTouchRect() {
        if (touchRect == null) {
            touchRect = Rectangle(x + 0.3, y + 0.3, x + width - 0.3, y + height - 0.3)
        }
    }

    override fun toString(): String {
        return "$id~$finger~($x,$y)~$width"
    }

    private fun isThisKey(xp: Double, yp: Double): Boolean {
        return xp > x && xp < x + width && yp > y && yp < y + height
    }

    /**
     * Set the score and distance of this key by using the calculated optimal finger
     */
    fun updateDistanceScore(geometry: Geometry, findOptimalFinger: Boolean) {
        val split = geometry.split == Geometry.Split.ALWAYS
        val homePos = geometry.homePositions
        var bestScore = -1.0
        var bestDistance = -1.0
        var bestFinger = FINGER_UNASSIGNED
        for (f in 0..9) {
            if (findOptimalFinger || finger == FINGER_UNASSIGNED || finger == f) {
                val homeCoords = homePos[f]
                if (homeCoords != null) {
                    val thisScore = totalPenalty(homeCoords.first, homeCoords.second, split, f)
                    if (bestScore < 0.0 || thisScore < bestScore) {
                        bestScore = thisScore
                        bestDistance = distanceFrom(homeCoords.first, homeCoords.second)
                        bestFinger = f
                    }
                }
            }
        }
        score = bestScore
        distance = bestDistance
        if (findOptimalFinger) {
            this.bestFinger = bestFinger
        }
        log.d("updateDistanceScore ${geometry.id} Key $id : distance=$bestDistance score=$bestScore finger=$bestFinger")
    }

    private fun distanceFrom(xp: Double, yp: Double): Double {
        ensureTouchRect()
        val delta: Pair<Double, Double> = touchRect!!.distanceFrom(xp, yp)
        //log.d("distanceFrom $keyId $finger ${delta.first} ${delta.second}")

        //zero distance penalty if same key
        return if (isThisKey(xp, yp)) {
            0.0
        } else {
            sqrt(delta.first * delta.first + delta.second * delta.second)
        }
    }

    private fun distancePenalty(xp: Double, yp: Double, split: Boolean, finger: Int): Double {
        //zero distance penalty if same key
        if (isThisKey(xp, yp)) {
            return 0.0
        }

        //get distance from corresponding home key
        ensureTouchRect()
        val delta: Pair<Double, Double> = touchRect!!.distanceFrom(xp, yp)
        val deltax = delta.first
        val deltay = delta.second

        // simulate the arm approaching the keyboard at an angle
        val xr: Double
        val yr: Double
        if (!split && finger >= 0 && finger <= 3) {
            // rotate the movement vector to align with the left hand's approach angle
            xr = deltax * costheta - deltay * sintheta
            yr = -deltax * sintheta - deltay * costheta
        } else if (!split && finger >= 6 && finger <= 9) {
            // rotate the movement vector to align with the right hand's approach angle
            xr = deltax * costheta + deltay * sintheta
            yr = deltax * sintheta - deltay * costheta
        } else {
            xr = deltax
            yr = deltay
        }
        //Apply Fitt's Law
        var xf = (1 + Constants.DISTANCE_PENALTY_FACTOR * abs(xr)).log2()
        var yf = (1 + Constants.DISTANCE_PENALTY_FACTOR * abs(yr)).log2()
        //add penalty depending on whether lateral motion and which finger
        if (finger in 0..3 || finger in 6..9) {
            // simulate lateral hand movement move expensive than vertical
            xf *= Constants.LATERAL_MOVEMENT_PENALTY_FACTOR
            // mesial movement penalty is dependent on finger
            yf *= Constants.fingerPenalty(finger)
        }
        //log.d("distancePenalty $keyId $finger ($deltax,$deltay) ($xr,$yr) ($xf,$yf)");
        return sqrt(xf * xf + yf * yf)
    }

    private fun totalPenalty(xp: Double, yp: Double, split: Boolean, finger: Int): Double {
        return Constants.fingerPenalty(finger) + distancePenalty(xp, yp, split, finger)
    }

    companion object {
        const val theta = Constants.ANGLE * PI / 180.0
        val sintheta = sin(theta)
        val costheta = cos(theta)
        const val FINGER_UNASSIGNED = -1
    }

}