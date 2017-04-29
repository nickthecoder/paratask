package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.AbstractValue
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values

class StringField : LabelledField {

    val parameterValue: AbstractValue<String>

    override val parameter : StringParameter

    constructor (parameter: StringParameter, values: Values) : super(parameter) {
        this.parameterValue = parameter.parameterValue(values)
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameterValue.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(parameterValue.property);
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage(parameterValue.value)
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }
}