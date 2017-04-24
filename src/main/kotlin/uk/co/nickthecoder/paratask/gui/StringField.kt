package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.StringValue
import uk.co.nickthecoder.paratask.parameter.Values

class StringField(override val parameter: StringParameter,
                  values: Values)
    : LabelledField(parameter) {

    val value: StringValue

    init {
        this.value = parameter.valueFrom(values)
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = value.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(value.property);
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage(value.value)
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }
}