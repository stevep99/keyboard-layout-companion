package io.github.colemakmods.keyboard_companion.model

import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.util.Constants
import kotlinx.serialization.Serializable

private val log = Logger.withTag("Geometry")

/**
 * Created by steve on 15/07/15.
 */
@Serializable
data class Geometry(
    val id: String,
    val title: String,
    val fingerConfig: FingerConfig,
    val keys: List<Key>,
    val split: Split = Split.SPLITTABLE,
) : Comparable<Geometry> {

    private val FIND_OPTIMAL_FINGER = false

    private val INDEX_OFFSET_10DEG = Pair(0.040, -0.324)
    private val MIDDLE_OFFSET_10DEG = Pair(-0.031, 0.164)
    private val RING_OFFSET_10DEG = Pair(0.0, 0.075)
    private val PINKY_OFFSET_10DEG = Pair(0.014, 0.085)
    private val INDEX_OFFSET_15DEG = Pair(0.082, -0.451)
    private val MIDDLE_OFFSET_15DEG = Pair(-0.035, 0.117)
    private val RING_OFFSET_15DEG = Pair(0.0, 0.118)
    private val PINKY_OFFSET_15DEG = Pair(0.009, 0.216)

    enum class FingerConfig {
        TRAD, ALT, ANGLE, MATRIX
    }

    enum class Split {
        NEVER, ALWAYS, SPLITTABLE
    }

    val width: Float = keys.maxOf { it.x + it.width }
    val height: Float = keys.maxOf { it.y + it.height }
    val homePositions = HashMap<Int, Pair<Double, Double>>()

    init {
        updateHomePosition()
        updateDistancesScores()
    }

    fun getKey(xp: Float, yp: Float): Key? {
        return keys.firstOrNull { xp > it.x && xp < (it.x + it.width)
                && yp > it.y && yp < (it.y + it.height) }
    }

    fun getKey(id: String?): Key? {
        return keys.firstOrNull { it.id == id }
    }

    private fun updateHomePosition() {
        for (key in keys) {
            if (key.highlight) {
                var deltax = 0.0
                var deltay = 0.0
                when (key.finger) {
                    0 -> {
                        deltax = -PINKY_OFFSET_10DEG.first
                        deltay = PINKY_OFFSET_10DEG.second
                    }
                    9 -> {
                        deltax = PINKY_OFFSET_10DEG.first
                        deltay = PINKY_OFFSET_10DEG.second
                    }
                    1 -> {
                        deltax = -RING_OFFSET_10DEG.first
                        deltay = RING_OFFSET_10DEG.second
                    }
                    8 -> {
                        deltax = RING_OFFSET_10DEG.first
                        deltay = RING_OFFSET_10DEG.second
                    }
                    2 -> {
                        deltax = -MIDDLE_OFFSET_10DEG.first
                        deltay = MIDDLE_OFFSET_10DEG.second
                    }
                    7 -> {
                        deltax = MIDDLE_OFFSET_10DEG.first
                        deltay = MIDDLE_OFFSET_10DEG.second
                    }
                    3 -> {
                        deltax = -INDEX_OFFSET_10DEG.first
                        deltay = INDEX_OFFSET_10DEG.second
                    }
                    6 -> {
                        deltax = INDEX_OFFSET_10DEG.first
                        deltay = INDEX_OFFSET_10DEG.second
                    }
                    Key.FINGER_UNASSIGNED -> throw IllegalArgumentException("Finger must be defined for highlighted key")
                }
                deltax *= Constants.COLUMN_STAGGER_FACTOR
                deltay *= Constants.COLUMN_STAGGER_FACTOR
                homePositions[key.finger] = Pair(
                    key.x + key.width / 2.0 + deltax,
                    key.y + key.height / 2.0 + deltay
                )
            }
        }
    }

    private fun updateDistancesScores() {
        log.d("updateDistancesScores")
        for (key in keys) {
            key.updateDistanceScore(this, FIND_OPTIMAL_FINGER)
        }
    }

    override fun toString(): String {
        return title
    }

    override fun compareTo(other: Geometry): Int {
        return if (fingerConfig.ordinal != other.fingerConfig.ordinal) {
            fingerConfig.ordinal.compareTo(other.fingerConfig.ordinal)
        } else {
            title.uppercase().compareTo(other.title.uppercase())
        }
    }

    companion object {
        fun filterByFingerConfig(allGeometries: List<Geometry>, fingerConfig: FingerConfig): List<Geometry> {
            return allGeometries.filter { it.fingerConfig == fingerConfig }
        }
    }

}