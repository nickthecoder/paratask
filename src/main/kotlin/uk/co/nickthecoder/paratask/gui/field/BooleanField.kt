package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.BooleanValue
import uk.co.nickthecoder.paratask.parameter.ParameterValue
import uk.co.nickthecoder.paratask.parameter.ValueListener
import uk.co.nickthecoder.paratask.parameter.Values

class BooleanField : LabelledField {

    val booleanValue: BooleanValue

    override val parameter: BooleanParameter

    constructor (parameter: BooleanParameter, booleanValue: BooleanValue)
            : super(parameter, label = if (parameter.labelOnLeft) parameter.label else "") {

        this.booleanValue = booleanValue
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val checkBox = CheckBox(if (parameter.labelOnLeft) "" else parameter.label)
        checkBox.setAllowIndeterminate(!parameter.required)
        if (booleanValue.value == null) {
            checkBox.setIndeterminate(true)
        } else {
            checkBox.setSelected(booleanValue.value == true)
        }
        checkBox.selectedProperty().bindBidirectional(booleanValue.property);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            booleanValue.value = when (booleanValue.value) {
                null -> true
                true -> false
                false -> if (parameter.required) true else null
            }
            checkBox.setIndeterminate(booleanValue.value == null)
        })

        return checkBox
    }

}