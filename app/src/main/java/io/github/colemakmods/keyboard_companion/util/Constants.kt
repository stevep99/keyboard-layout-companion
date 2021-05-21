package io.github.colemakmods.keyboard_companion.util

object Constants {
    const val ANGLE = 10.0
    const val COLUMN_STAGGER_FACTOR = 1.0
    const val DISTANCE_PENALTY_FACTOR = 1.12
    const val LATERAL_MOVEMENT_PENALTY_FACTOR = 2.0

    fun fingerPenalty(finger: Int): Double {
        return when (finger) {
            0, 9 ->                 //pinky
                1.6
            1, 8 ->                 //ring
                1.3
            2, 7 ->                 //middle
                1.1
            3, 6 ->                 //index
                1.0
            else -> 0.0
        }
    }

    //https://forum.colemak.com/topic/2724-engram-layout/#p23938
    fun fingerPenaltyAlt(finger: Int): Double {
        return when (finger) {
            0 ->                 //l-pinky
                2.0625
            9 ->                 //r-pinky
                1.6875
            1 ->                 //l-ring
                1.8125
            8 ->                 //r-ring
                1.4375
            2 ->                 //l-middle
                1.4375
            7 ->                 //r-middle
                1.25
            3 ->                 //l-index
                1.25
            6 ->                 //r-index
                1.0
            else -> 0.0
        }
    }
}
