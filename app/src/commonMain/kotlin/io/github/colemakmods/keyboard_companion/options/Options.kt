package io.github.colemakmods.keyboard_companion.options

data class Options(
    var keyRenderOption: KeyRenderOption = KeyRenderOptionRounded,
    var modeSelectorVisible: Boolean = false,
    var mode: Mode = Mode.MODE_LAYOUT,
    var keyColorScheme: KeyColorScheme = KeyColorScheme.FINGERS,
    var showStyles: Boolean = true,
    var showSplit: Boolean = false,
    var keyFilterOption: KeyFilter = KeyFilter.KEY_FILTER_ALL,
) {

    enum class Mode(val title: String) {
        MODE_LAYOUT("Layout"),
        MODE_SCORE("Score"),
        MODE_DISTANCE("Distance"),
        MODE_KEYID("Key ID"),
    }

    enum class KeyColorScheme {
        FINGERS,
        FUNCTION,
    }

    enum class KeyFilter(val title: String) {
        KEY_FILTER_ALL("All keys"),
        KEY_FILTER_EXCLUDE_BOTTOM("All keys except bottom row"),
        KEY_FILTER_CHAR_KEYS("Character keys"),
        KEY_FILTER_MAIN_ZONE("Main zone"),
        KEY_FILTER_LETTERS_PUNCTUATION("Letters & punctuation"),
        KEY_FILTER_LETTERS_ONLY("Letters only"),
    }
}