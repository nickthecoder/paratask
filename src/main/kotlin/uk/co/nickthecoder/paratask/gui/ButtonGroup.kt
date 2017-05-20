package uk.co.nickthecoder.paratask.gui

import javafx.scene.control.Button
import javafx.scene.layout.HBox

/**
 * Used to style buttons so that they are joined together, with rounded corners on the first and
 * last buttons only.
 *
 * Note, this class isn't well developed. It cannot handle removing buttons, not making the first/last buttons
 * invisible.
 */
class ButtonGroup : HBox() {

    init {
        styleClass.add("button-group")
    }

    fun add(button: Button) {

        if (children.size == 0) {
            button.styleClass.add("first")
        } else {
            if (children.size > 1) {
                children[children.count() - 1].styleClass.add("middle")
                children[children.count() - 1].styleClass.remove("last")
            }
            button.styleClass.add("last")
        }
        children.add(button)
    }
}
