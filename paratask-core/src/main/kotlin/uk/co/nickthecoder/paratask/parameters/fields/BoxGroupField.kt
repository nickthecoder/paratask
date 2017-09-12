package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener

class BoxGroupField(
        groupParameter: AbstractGroupParameter,
        val labelsAbove: Boolean,
        isBoxed: Boolean)

    : SingleErrorGroupField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val hBox = HBox()

    val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createContent(): Node {
        hBox.styleClass.add("box-group")

        groupParameter.children.forEach { child ->
            val field = child.createField()
            field.fieldParent = this

            val container = createChild(field)

            fieldSet.add(field)
            containers.add(container)

            if (!child.hidden) {
                hBox.children.add(container)
            }
        }

        return hBox
    }

    fun createChild(childField: ParameterField): Node {

        val container: Node
        if (labelsAbove) {
            val vbox = VBox()
            container = vbox
            container.styleClass.add("vbox")
            container.children.addAll(childField.label, childField.control)
        } else {
            val hbox = HBox()
            container = hbox
            container.styleClass.add("hbox")
            container.children.addAll(childField.label, childField.control)
        }
        return container
    }

    override fun updateField(field: ParameterField) {
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

        super.updateField(field)
    }

}