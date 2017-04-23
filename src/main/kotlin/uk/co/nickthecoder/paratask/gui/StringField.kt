package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.StringParameter

class StringField : Field {

    val parameter: StringParameter

    constructor(parameter: StringParameter) : super(parameter.name, parameter.isStretchy()) {
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameter.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(parameter.property);
        textField.textProperty().addListener({ _, _, newValue: String ->
            val error = parameter.errorMessage(newValue)
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }
}