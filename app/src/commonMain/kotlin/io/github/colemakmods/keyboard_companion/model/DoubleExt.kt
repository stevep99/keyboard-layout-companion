package io.github.colemakmods.keyboard_companion.model

import kotlin.math.log

fun Double.formatToOneDecimalPlace(): String {
    if (this.isNaN() || this.isInfinite()) {
        return this.toString()
    }

    val rounded = kotlin.math.round(this * 10) / 10.0
    val s = rounded.toString()

    return if (s.contains('.')) {
        s
    } else {
        "$s.0"
    }
}

fun Double.log2(): Double = log(this, 2.0)
