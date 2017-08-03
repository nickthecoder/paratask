package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.ActionEvent
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.parameters.ButtonParameter

class ButtonField(val buttonParameter: ButtonParameter) : LabelledField(buttonParameter) {

    init {
        control = createButton()
    }

    private fun createButton(): Button {
        val button = Button(buttonParameter.buttonText)
        button.addEventHandler(ActionEvent.ACTION) { buttonParameter.action() }
        return button
    }
}
