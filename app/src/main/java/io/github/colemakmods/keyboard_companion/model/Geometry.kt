package io.github.colemakmods.keyboard_companion.model

import android.util.Pair
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.colemakmods.keyboard_companion.util.Constants
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by steve on 15/07/15.
 */
class Geometry : Comparable<Geometry> {
    private val INDEX_OFFSET_10DEG = Pair(0.040, 0.324)
    private val MIDDLE_OFFSET_10DEG = Pair(-0.031, -0.164)
    private val RING_OFFSET_10DEG = Pair(0.0, -0.075)
    private val PINKY_OFFSET_10DEG = Pair(0.014, -0.085)
    private val INDEX_OFFSET_15DEG = Pair(0.082, 0.451)
    private val MIDDLE_OFFSET_15DEG = Pair(-0.035, -0.117)
    private val RING_OFFSET_15DEG = Pair(0.0, -0.118)
    private val PINKY_OFFSET_15DEG = Pair(0.009, -0.216)

    companion object {
        @Throws(IOException::class)
        fun parse(ins: InputStream?): Geometry {
            val mapper = ObjectMapper()
            return mapper.readValue(ins, Geometry::class.java)
        }

        fun findByFingers(geometryList: List<Geometry>, fingerConfig: FingerConfig): List<Geometry> {
            val supportedGeometryList: MutableList<Geometry> = ArrayList()
            for (geometry in geometryList) {
                if (fingerConfig == geometry.fingerConfig) {
                    supportedGeometryList.add(geometry)
                }
            }
            return supportedGeometryList
        }
    }

    enum class ROW {
        number_row, upper_row, middle_row, lower_row, modifier_row
    }

    enum class FingerConfig {
        TRAD, ALT, ANGLE, MATRIX
    }

    enum class Split {
        NEVER, ALWAYS, SPLITTABLE
    }

    lateinit var id: String
    lateinit var title: String

    @JsonProperty("keys")
    private lateinit var keys: Map<ROW, List<Key>>

    val split = Split.SPLITTABLE

    @JsonProperty("config")
    val fingerConfig: FingerConfig = FingerConfig.TRAD

    val homePositions = HashMap<Int, Pair<Double, Double>>()
    private var scoresCalculated = false

    fun getRows(): Int {
        return keys.size
    }

    fun getRowLength(row: ROW): Int {
        val rowKeys = keys[row]
        return rowKeys?.size ?: 0
    }

    fun getKey(row: ROW, col: Int): Key? {
        val rowKeys = keys[row]
        return if (rowKeys != null && col < rowKeys.size) {
            rowKeys[col]
        } else {
            null
        }
    }

    fun getKey(id: String?): Key? {
        for (row in keys.keys) {
            for (key in keys[row] ?: error("invalid key")) {
                if (key.keyId == id) return key
            }
        }
        return null
    }

    fun updateKeyIds() {
        for (row in keys.keys) {
            for (key in keys[row] ?: error("updateKeyId error")) {
                key.updateKeyId()
            }
        }
    }

    fun updateKeyCoordinates() {
        for (row in keys.keys) {
            for (key in keys[row] ?: error("updateKeyCoordinates error")) {
                key.y = row.ordinal.toDouble()
            }
        }
    }

    fun updateHomePosition() {
        for (row in keys.keys) {
            for (key in keys[row] ?: error("updateHomePosition error")) {
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
                    homePositions[key.finger] = Pair(key.x + key.width / 2.0 + deltax,
                            key.y + 0.5 + deltay)
                }
            }
        }
    }

    fun updateDistancesScores(findOptimalFinger: Boolean) {
        if (!scoresCalculated) {
            for (row in keys.keys) {
                for (key in keys[row] ?: error("updateDistancesScores error")) {
                    key.updateDistanceScore(this, findOptimalFinger)
                }
            }
            scoresCalculated = true
        }
    }

    override fun toString(): String {
        return title
    }

    override fun compareTo(other: Geometry): Int {
        return if (fingerConfig.ordinal != other.fingerConfig.ordinal) {
            fingerConfig.ordinal.compareTo(other.fingerConfig.ordinal)
        } else {
            title.compareTo(other.title)
        }
    }

}