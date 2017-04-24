package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.Form

abstract class TextParameter<T : Value<*>>(name: String, required: Boolean, var columns: Int = 0)
    : ValueParameter<T>(name = name, required = required) {

    protected fun adjustNode(node: TextField) {
        if (columns != 0) {
            node.prefColumnCount = columns
        }
    }

    override fun isStretchy() = columns == 0
}