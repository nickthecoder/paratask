package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.AbstractValue
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values

class StringField : LabelledField {

    val value: AbstractValue<String>

    override val parameter : StringParameter

    constructor (parameter: StringParameter, values: Values) : super(parameter) {
        this.value = parameter.getValue(values)
        this.parameter = parameter
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