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

    private var dirty = false

    val whole = BorderPane()
    //val list = VBox()

    val addButton = Button("+")

    init {
        parameter.parameterListeners.add(this)

        addButton.onAction = EventHandler {
            extraValue()
        }
        addButton.setTooltip(Tooltip("Add"))
    }

    override fun buildContent() {
        super.buildContent()
        children.add(addButton)
    }

    override fun addParameter(parameter: Parameter, index: Int): Node {

        val result = super.addParameter(parameter, index)

        if (result is LabelledField) {
            val buttons = HBox()

            buttons.getStyleClass().add("multiple-line-buttons")

            if (multipleParameter.allowInsert) {
                val addButton = Button("+")
                addButton.onAction = EventHandler {
                    newValue(index + 1)
                }
                addButton.setTooltip(Tooltip("Insert Before"))
                buttons.children.add(addButton)
            }

            val removeButton = Button("-")
            removeButton.onAction = EventHandler {
                removeAt(index)
            }
            removeButton.setTooltip(Tooltip("Remove"))
            buttons.children.add(removeButton)

            result.replaceLabel(buttons)

        }
        return result
    }

    private fun rebuildList() {
        children.clear()
        fieldSet.clear()

        var index = 0
        for (innerParameter in multipleParameter.innerParameters) {
            addParameter(innerParameter, index)
            index++
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

