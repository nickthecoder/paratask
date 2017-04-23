package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.Form

abstract class TextParameter<T>(name: String, required: Boolean, var columns: Int = 0)
    : ValueParameter<T>(name = name, required = required) {

    protected abstract fun bind(textField: TextField)

    protected fun adjustNode(node: TextField) {
        node.text = getStringValue()
        if (columns != 0) {
            node.prefColumnCount = columns
        }
    }

    override fun createField(): Field {
        val node = TextField()
        adjustNode(node)

        return Field(name, node)
    }

    override fun isStretchy() = columns == 0
}