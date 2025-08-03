package io.github.colemakmods.keyboard_companion.model

import io.github.colemakmods.keyboard_companion.platform.Common

fun String.formattedLabel() =
    if (this.length == 1 && this.first().isLetter()) {
        //show uppercase labels on alpha keys
        this.uppercase()
    } else {
        Common.platform.formatLabel(this)
    }