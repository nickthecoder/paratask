package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.MultipleValue
import uk.co.nickthecoder.paratask.parameter.ParameterValue
import uk.co.nickthecoder.paratask.parameter.ValueListener
import uk.co.nickthecoder.paratask.parameter.Values

class MultipleField<T> : ParametersForm, ValueListener {

    override val parameter: MultipleParameter<T>

    val multipleValue: MultipleValue<T>

    private var dirty = false

    val whole = BorderPane()
    val list = VBox()

    constructor(parameter: MultipleParameter<T>, values: Values) : super(parameter) {
        this.parameter = parameter
        this.multipleValue = parameter.parameterValue(values)

        val addButton = Button("+")
        addButton.onAction = EventHandler {
            newValue()
        }
        addButton.setTooltip(Tooltip("Add"))

        whole.center = list
        whole.bottom = addButton

        buildList()

        whole.getStyleClass().add("multiple-field")
        list.getStyleClass().add("multiple-list")

        control = whole
        multipleValue.valueListeners.add(this)
    }

    private fun buildList() {
        list.children.clear()
        fieldSet.clear()

        val values = Values()

        var index = 0
        multipleValue.value.forEach { item ->
            values.put(parameter.name, item)
            val field = parameter.prototype.createField(values)
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
                newValue(index)
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

    fun newValue(index: Int = multipleValue.value.size) {
        multipleValue.addValue(parameter.prototype.createValue() as ParameterValue<T>, index)
    }

    fun removeAt(index: Int) {
        multipleValue.removeAt(index)
    }

    override fun valueChanged(parameterValue: ParameterValue<*>) {
        buildList()
    }
}

