package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import uk.co.nickthecoder.paratask.parameters.StringParameter

class StringField(override val parameter: StringParameter) : LabelledField(parameter) {

    private fun createControl(): Node {
        val textField: TextInputControl

        if (parameter.rows > 1) {
            textField = TextArea()
            if (parameter.columns > 0) {
                textField.prefColumnCount = parameter.columns
            }
            textField.prefRowCount = parameter.rows
        } else {
            textField = TextField()
            if (parameter.columns > 0) {
                textField.prefColumnCount = parameter.columns
            }
        }
        parameter.style?.let { textField.styleClass.add(it) }

        textField.text = parameter.value
        textField.textProperty().bindBidirectional(parameter.valueProperty)
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }

    init {
        control = createControl()
    }
}