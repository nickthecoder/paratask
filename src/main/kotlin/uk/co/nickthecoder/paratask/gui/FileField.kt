package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.FileValue
import uk.co.nickthecoder.paratask.parameter.Values

class FileField : LabelledField {

    val value: FileValue

    override val parameter: FileParameter

    constructor (parameter: FileParameter, values: Values) : super(parameter) {
        this.value = parameter.parameterValue(values)
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = value.stringValue
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(value.property, value);
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