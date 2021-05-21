package io.github.colemakmods.keyboard_companion.view

class Options {
    var keyRenderOptions: KeyRenderOptions = KeyRenderOptionsRounded()
    var mode = Mode.MODE_DISPLAY
    var showFingers = false
    var showStyles = false
    var showSplit = false
    var keyFilterOption = 0

    enum class Mode(private val title: String) {
        MODE_DISPLAY("Display"),
        MODE_SCORE("Score"),
        MODE_DISTANCE("Distance");

        override fun toString(): String {
            return title
        }
    }
}