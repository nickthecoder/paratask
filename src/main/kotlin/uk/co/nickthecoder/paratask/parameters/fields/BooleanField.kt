package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameters.BooleanParameter

class BooleanField(override val parameter: BooleanParameter)
    : LabelledField(parameter, label = if (parameter.labelOnLeft) parameter.label else "") {

    private fun createControl(): Node {
        val checkBox = CheckBox(if (parameter.labelOnLeft) "" else parameter.label)
        checkBox.isAllowIndeterminate = !parameter.required
        if (parameter.value == null) {
            checkBox.isIndeterminate = true
        } else {
            checkBox.isSelected = parameter.value == true
        }
        checkBox.selectedProperty().bindBidirectional(parameter.valueProperty)

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            parameter.value = when (parameter.value) {
                null -> true
                true -> false
                false -> if (parameter.required) true else null
            }
            checkBox.isIndeterminate = parameter.value == null
        })

        return checkBox
    }

    init {
        control = createControl()
    }

}