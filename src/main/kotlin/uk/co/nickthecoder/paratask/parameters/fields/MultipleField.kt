package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*

class MultipleField<T>(val multipleParameter: MultipleParameter<T>)
    : ParametersForm(multipleParameter), ParameterListener {

    val whole = BorderPane()

    val addButton = Button("+")

    init {
        parameter.parameterListeners.add(this)

        addButton.onAction = EventHandler {
            extraValue()
        }
        addButton.tooltip = Tooltip("Add")
    }

    override fun buildContent() {
        super.buildContent()
        children.add(addButton)
    }

    override fun addParameter(parameter: Parameter, index: Int): Node {

        val result = super.addParameter(parameter, index)

        if (result is LabelledField) {
            val buttons = HBox()

            buttons.styleClass.add("multiple-line-buttons")

            if (multipleParameter.allowInsert) {
                val addButton = Button("+")
                addButton.onAction = EventHandler {
                    newValue(index + 1)
                }
                addButton.tooltip = Tooltip("Insert Before")
                buttons.children.add(addButton)
            }

            val removeButton = Button("-")
            removeButton.onAction = EventHandler {
                removeAt(index)
            }
            removeButton.tooltip = Tooltip("Remove")
            buttons.children.add(removeButton)

            result.replaceLabel(buttons)

        }
        return result
    }

    private fun rebuildList() {
        children.clear()
        fieldSet.clear()

        for ((index, innerParameter) in multipleParameter.innerParameters.withIndex()) {
            addParameter(innerParameter, index)
        }

        children.add(addButton)
    }

    private fun newValue(index: Int) {
        multipleParameter.newValue(index)
    }

    private fun extraValue() {
        newValue(multipleParameter.children.size)
    }

    fun removeAt(index: Int) {
        multipleParameter.removeAt(index)
    }

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.STRUCTURAL) {
            rebuildList()
        }
    }
}
