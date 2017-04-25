package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.BooleanValue
import uk.co.nickthecoder.paratask.parameter.Values

class BooleanField : LabelledField {

    val value: BooleanValue

    override val parameter: BooleanParameter

    constructor (parameter: BooleanParameter, values: Values)
            : super(parameter, label = if (parameter.labelOnLeft) parameter.label else "") {

        this.value = parameter.valueFrom(values)
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val checkBox = CheckBox(if (parameter.labelOnLeft) "" else parameter.label)
        checkBox.setAllowIndeterminate(!parameter.required)
        if (value.value == null) {
            checkBox.setIndeterminate(true)
        } else {
            checkBox.setSelected(value.value == true)
        }
        checkBox.selectedProperty().bindBidirectional(value.property);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            value.value = when (value.value) {
                null -> true
                true -> false
                false -> if (parameter.required) true else null
            }
            checkBox.setIndeterminate( value.value == null)
        })

        return checkBox
    }
}