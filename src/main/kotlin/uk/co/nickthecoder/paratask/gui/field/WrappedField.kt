package uk.co.nickthecoder.paratask.gui.field

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameter.ValueParameter

class WrappedField(val parameterField: ParameterField) : TitledPane() {

    val parameter = parameterField.parameter

    val expressionButton: ToggleButton?

    val expressionField = TextField()

    val box = VBox()

    init {
        setCollapsible(false)

        text = parameterField.parameter.label
        if (parameter is ValueParameter<*> && parameter.isProgrammingMode()) {
            expressionField.getStyleClass().add("expression")
            expressionButton = ToggleButton("=")
            box.children.add(expressionButton)
            expressionField.textProperty().bindBidirectional(parameter.expressionProperty)
            if (parameter.expression != null) {
                expressionButton.setSelected(true)
                box.children.add(expressionField)
            } else {
                box.children.add(parameterField)
            }
            expressionButton.addEventHandler(ActionEvent.ACTION) { onExpression() }

            content = box
        } else {
            content = parameterField
            expressionButton = null
        }
    }

    fun onExpression() {
        if (expressionButton?.isSelected == true) {
            box.children.add(expressionField)
            box.children.remove(parameterField)
            expressionField.text = ""
        } else {
            box.children.remove(expressionField)
            box.children.add(parameterField)
            expressionField.text = null
        }
    }

}
