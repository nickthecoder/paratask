package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.AbstractValue
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.FileValue
import java.io.File

class FileField : LabelledField {

    val parameterValue: AbstractValue<File?>

    override val parameter: FileParameter

    constructor (parameter: FileParameter, fileValue : FileValue) : super(parameter) {
        this.parameterValue = fileValue
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameterValue.stringValue
        if (parameter.columns > 0) {
            textField.prefColumnCount = parameter.columns
        }
        textField.textProperty().bindBidirectional(parameterValue.property, parameterValue);
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