package io.github.colemakmods.keyboard_companion.model

import android.util.Pair
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.colemakmods.keyboard_companion.util.Constants
import io.github.colemakmods.keyboard_companion.util.Maths
import io.github.colemakmods.keyboard_companion.util.Maths.Rectangle
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Created by steve on 15/07/15.
 */
class Key {
    companion object {
        const val theta = Constants.ANGLE * Math.PI / 180.0
        val sintheta = Math.sin(theta)
        val costheta = Math.cos(theta)
        const val FINGER_UNASSIGNED = -1
    }

    @JsonProperty("id")
    lateinit var keyId: String

    var finger = FINGER_UNASSIGNED
    var score = 0.0
    var distance = 0.0
    var highlight = false
    var type = KeyType.UNKNOWN
    val width = 1.0
    var x = 0.0
    var y = 0.0
    private var touchRect: Rectangle? = null

    fun toggleHighlight() {
        highlight = !highlight
    }

    fun updateKeyId() {
        type = KeyType.fromId(keyId)
    }

    private fun ensureTouchRect() {
        if (touchRect == null) {
            touchRect = Rectangle(x + 0.3, y + 0.3, x + width - 0.3, y + 0.7)
        }
    }

    override fun toString(): String {
        return "$keyId~$finger~($x,$y)~$width"
    }

    private fun isThisKey(xp: Double, yp: Double): Boolean {
        return xp > x && xp < x + width && yp > y && yp < y + 1
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
            finger = bestFinger
        }
        Timber.d("updateDistanceScore ${geometry.id} Key $keyId : distance=$bestDistance score=$bestScore finger=$bestFinger")
    }

    private fun distanceFrom(xp: Double, yp: Double): Double {
        ensureTouchRect()
        val delta: Pair<Double, Double> = touchRect!!.distanceFrom(xp, yp)
        //Timber.d("distanceFrom $keyId $finger ${delta.first} ${delta.second}")

        //zero distance penalty if same key
        return if (isThisKey(xp, yp)) {
            0.0
        } else Math.sqrt(delta.first * delta.first + delta.second * delta.second)
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
            xr = deltax * costheta + deltay * sintheta
            yr = -deltax * sintheta + deltay * costheta
        } else if (!split && finger >= 6 && finger <= 9) {
            // rotate the movement vector to align with the right hand's approach angle
            xr = deltax * costheta - deltay * sintheta
            yr = deltax * sintheta + deltay * costheta
        } else {
            xr = deltax
            yr = deltay
        }
        //Apply Fitt's Law
        var xf: Double = Maths.log2(1 + Constants.DISTANCE_PENALTY_FACTOR * abs(xr))
        var yf: Double = Maths.log2(1 + Constants.DISTANCE_PENALTY_FACTOR * abs(yr))
        //add penalty depending on whether lateral motion and which finger
        if (finger in 0..3 || finger in 6..9) {
            // simulate lateral hand movement move expensive than vertical
            xf *= Constants.LATERAL_MOVEMENT_PENALTY_FACTOR
            // mesial movement penalty is dependent on finger
            yf *= Constants.fingerPenalty(finger)
        }
        //Timber.d("distancePenalty $keyId $finger ($deltax,$deltay) ($xr,$yr) ($xf,$yf)");
        return sqrt(xf * xf + yf * yf)
    }

    private fun totalPenalty(xp: Double, yp: Double, split: Boolean, finger: Int): Double {
        return Constants.fingerPenalty(finger) + distancePenalty(xp, yp, split, finger)
    }

}