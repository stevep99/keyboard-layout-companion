package io.github.colemakmods.keyboard_companion.util

object StringUtil {
    fun nameToId(name: String): String {
        return name.lowercase()
                .replace(' ', '_')
                .replace(':', '_')
                .replace('-', '_')
    }
}