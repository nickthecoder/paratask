package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.StringValue

class StringField : Field {

    val parameter: StringParameter

    val value: StringValue

    constructor(parameter: StringParameter, value: StringValue) : super(parameter.name, parameter.isStretchy()) {
        this.parameter = parameter
        this.value = value
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