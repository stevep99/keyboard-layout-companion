package io.github.colemakmods.keyboard_companion.model

/**
 * Created by steve on 18/01/18.
 */
enum class KeyType {
    NUMBER_ROW,
    MAIN_SECTION,
    NON_CHARACTER,
    UNKNOWN;

    companion object {
        fun fromId(id: String): KeyType {
            return if (id >= "AE00" && id <= "AE12") {
                NUMBER_ROW
            } else if (id == "LCT" || id == "RCT" || id == "LWIN" || id == "RWIN" || id == "LSPC" || id == "SPC" || id == "RSPC" || id == "LALT" || id == "RALT" || id == "MNU" || id == "LSH" || id == "RSH" || id == "CAP" || id == "RET" || id == "RET2" || id == "TAB" || id == "BKS" || id == "JROM" || id == "JHIR") {
                NON_CHARACTER
            } else if (id.startsWith("AD") || id.startsWith("AC") || id.startsWith("AB") || id == "BSL") {
                MAIN_SECTION
            } else {
                UNKNOWN
            }
        }
    }
}