package io.github.colemakmods.keyboard_companion.util

import java.util.*

object StringUtil {
    fun nameToId(name: String): String {
        return name.lowercase(Locale.getDefault())
                .replace(' ', '_')
                .replace(':', '_')
                .replace('-', '_')
    }
}