package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameters.BooleanParameter

class BooleanField : LabelledField {

    override val parameter: BooleanParameter

    constructor (parameter: BooleanParameter)
            : super(parameter, label = if (parameter.labelOnLeft) parameter.label else "") {

        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val checkBox = CheckBox(if (parameter.labelOnLeft) "" else parameter.label)
        checkBox.setAllowIndeterminate(!parameter.required)
        if (parameter.value == null) {
            checkBox.setIndeterminate(true)
        } else {
            checkBox.setSelected(parameter.value == true)
        }
        checkBox.selectedProperty().bindBidirectional(parameter.valueProperty);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            parameter.value = when (parameter.value) {
                null -> true
                true -> false
                false -> if (parameter.required) true else null
            }
            checkBox.setIndeterminate(parameter.value == null)
        })

        return checkBox
    }

}