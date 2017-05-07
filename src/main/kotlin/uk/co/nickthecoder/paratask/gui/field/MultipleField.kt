package uk.co.nickthecoder.paratask.gui.field

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.ParameterListener
import uk.co.nickthecoder.paratask.parameter.ValueParameter

class MultipleField<T>(parameter: MultipleParameter<T>)
    : ParametersForm(parameter), ParameterListener {

    override val parameter: MultipleParameter<T>

    private var dirty = false

    val whole = BorderPane()
    val list = VBox()

    init {
        this.parameter = parameter

        val addButton = Button("+")
        addButton.onAction = EventHandler {
            newValue(parameter.value.size)
        }
        addButton.setTooltip(Tooltip("Add"))

        whole.center = list
        whole.bottom = addButton

        buildList()

        whole.getStyleClass().add("multiple-field")
        list.getStyleClass().add("multiple-list")

        control = whole
        parameter.parameterListeners.add(this)
    }

    private fun buildList() {
        list.children.clear()
        fieldSet.clear()

        println( "Building MF list ${parameter.innerParameters.size}")
        var index = 0
        for (innerParameter in parameter.innerParameters) {

            println( "MF Adding ${innerParameter.value}")

            val field = innerParameter.createField()
            if (field is LabelledField) {
                field.label.setVisible(false)
            }
            field.form = this

            fieldSet.add(field)
            list.children.add(createLine(field, index))

            index++
        }
    }

    private fun createLine(field: ParameterField, index: Int): Node {
        val line = HBox()
        val buttons = HBox()

        line.children.add(buttons)
        line.children.add(field)

        buttons.getStyleClass().add("multiple-line-buttons")
        line.getStyleClass().add("multiple-line")

        if (parameter.allowInsert) {
            val addButton = Button("+")
            addButton.onAction = EventHandler {
                newValue(index + 1)
            }
            addButton.setTooltip(Tooltip("Insert Before"))
        }

        val removeButton = Button("-")
        removeButton.onAction = EventHandler {
            removeAt(index)
        }
        removeButton.setTooltip(Tooltip("Remove"))
        buttons.children.add(removeButton)

        return line
    }

    private fun newValue(index: Int) {
        parameter.newValue(index)
    }

    fun removeAt(index: Int) {
        parameter.removeAt(index)
    }

    override fun parameterChanged(parameter: Parameter) {
        buildList()
    }
}

