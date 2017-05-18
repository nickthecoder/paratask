package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.StringParameter

class StringField : LabelledField {

    override val parameter : StringParameter

    constructor (parameter: StringParameter) : super(parameter) {
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameter.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(parameter.valueProperty);
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
}