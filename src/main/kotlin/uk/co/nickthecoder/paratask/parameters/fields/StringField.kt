package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameters.StringParameter

class StringField(override val parameter: StringParameter) : LabelledField(parameter) {

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameter.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
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