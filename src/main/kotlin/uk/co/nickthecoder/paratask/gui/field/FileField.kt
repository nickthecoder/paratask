package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.FileParameter
import java.io.File

class FileField : LabelledField {

    override val parameter: FileParameter

    constructor (parameter: FileParameter) : super(parameter) {
        this.parameter = parameter
        control = createControl()
    }

    private fun createControl(): Node {
        val textField = TextField()
        textField.text = parameter.stringValue

        textField.textProperty().bindBidirectional(parameter.property, parameter.converter);
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