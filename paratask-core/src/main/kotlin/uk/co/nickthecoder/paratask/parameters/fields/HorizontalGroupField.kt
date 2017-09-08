package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener

class HorizontalGroupField(val groupParameter: AbstractGroupParameter)
    : LabelledField(groupParameter), HasChildFields, FieldParent, ParameterListener {

    val hBox = HBox()

    override val spacing = 10.0

    override val columns = listOf(FieldColumn(0.0), FieldColumn(0.0), FieldColumn(1.0))

    override val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    override fun createControl(): Node {
        buildContent()
        hBox.styleClass.add("horizontal-group")
        return hBox
    }

    fun buildContent() {
        hBox.children.clear()
        groupParameter.children.forEach { child ->
            val field = child.createField()
            field.form = this

            val container: Node
            if (groupParameter.labelsAbove) {
                val vbox = VBox()
                container = vbox
                container.styleClass.add("vbox")
                container.children.addAll(Label(child.label), field.control)
            } else {
                val hbox = HBox()
                container = hbox
                container.styleClass.add("hbox")
                container.children.addAll(Label(child.label), field.control)
            }

            fieldSet.add(field)
            containers.add(container)

            if (!child.hidden) {
                hBox.children.add(container)
            }
        }
    }

    override fun updateVisibility(field: ParameterField) {
        var visibleIndex = 0
        fieldSet.forEachIndexed { index, f ->
            val container = containers[index]
            if (f.parameter.hidden) {
                if (hBox.children.contains(container)) {
                    hBox.children.remove(container)
                }
            } else {
                if (!hBox.children.contains(container)) {
                    hBox.children.add(visibleIndex, container)
                }
                visibleIndex++
            }
        }
    }


    override fun calculateColumnWidths() {}

    override fun calculateColumnPreferences() {
        columns.forEach {
            it.prefWidth = 0.0
            it.minWidth = 0.0
        }
    }

}
