package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.KeyCode

object ApplicationActions {
    val SPINNER_INCREMENT = ApplicationAction("spinner.increment", KeyCode.UP)
    val SPINNER_DECREMENT = ApplicationAction("spinner.decrement", KeyCode.DOWN)
    val ENTER = ApplicationAction("enter", KeyCode.ENTER)


    val UP_DIRECTORY = ApplicationAction("directory-up", KeyCode.UP, alt = true)
    val COMPLETE_FILE = ApplicationAction("directory-complete", KeyCode.DOWN, alt = true)
}
