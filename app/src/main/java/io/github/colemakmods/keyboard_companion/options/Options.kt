package io.github.colemakmods.keyboard_companion.options

data class Options(
    var keyRenderOption: KeyRenderOption = keyRenderOptionRounded,
    var mode: Mode = Mode.MODE_DISPLAY,
    var showFingers: Boolean = true,
    var showStyles: Boolean = true,
    var showSplit: Boolean = false,
    var keyFilterOption: KeyFilter = KeyFilter.KEY_FILTER_ALL,
) {

    enum class Mode(val title: String) {
        MODE_DISPLAY("Display"),
        MODE_SCORE("Score"),
        MODE_DISTANCE("Distance");
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