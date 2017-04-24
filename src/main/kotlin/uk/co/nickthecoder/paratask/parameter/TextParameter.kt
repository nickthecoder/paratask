package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.gui.ParameterField
import uk.co.nickthecoder.paratask.gui.ParametersForm

abstract class TextParameter<T : Value<*>>(
        name: String,
        label: String,
        required: Boolean,
        var columns: Int = 0)
    : ValueParameter<T>(name = name, label = label, required = required) {

    protected fun adjustNode(node: TextField) {
        if (columns != 0) {
            node.prefColumnCount = columns
        }
    }

    override fun isStretchy() = columns == 0
}