package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.StringValue

class StringField : LabelledField {

    val stringValue: StringValue

    override val parameter : StringParameter

    constructor (parameter: StringParameter, stringValue : StringValue) : super(parameter) {
        this.stringValue = stringValue
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = stringValue.value
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(stringValue.property);
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage(stringValue.value)
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }
}