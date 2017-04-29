package uk.co.nickthecoder.paratask.gui

import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.MultipleValue
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.parameter.Values

class MultipleField<T> : ParametersForm {

    override val parameter: MultipleParameter<T>

    val value: MultipleValue<T>

    private var dirty = false

    val whole = BorderPane()
    val list = VBox()

    constructor(parameter: MultipleParameter<T>, values: Values) : super(parameter) {
        this.parameter = parameter
        this.value = parameter.getValue(values)

        whole.top = Label("Hello world")
        whole.bottom = Label("Add button goes here, which will be nice")
        whole.center = list

        buildList()

        control = whole
    }

    // TODO listen for change events and rebuild the list.
    // Need to ignore the event when it is ME that caused it

    private fun buildList() {
        list.children.clear()

        val values = Values(GroupParameter("dummy"))

        value.value.forEach { item ->
            println("Creating inner parameter")
            values.put(parameter.name, item)
            val field = parameter.prototype.createField(values)
            if (field is LabelledField) {
                field.label.setVisible(false)
            }
            field.form = this

            list.children.add(field)
        }
    }

}
