package uk.co.nickthecoder.paratask.parameters.fields

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener

open class HorizontalGroupField(
        groupParameter: AbstractGroupParameter,
        val labelsAbove: Boolean?,
        isBoxed: Boolean)

    : SingleErrorGroupField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val borderPane = BorderPane()

    val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createContent(): Node {

        var box = HBox()
        var foundStretchy: Boolean = false

        groupParameter.children.forEach { child ->
            val field = child.createField()
            field.fieldParent = this

            val container = createChild(field)

            fieldSet.add(field)
            containers.add(container)

            box.styleClass.add("box-group")
            if (!child.hidden) {
                if (child.isStretchy() && !foundStretchy) {
                    if (box.children.isNotEmpty()) {
                        borderPane.left = box
                        box.styleClass.add("right-spacing")
                    }
                    foundStretchy = true
                    borderPane.center = container
                    box = HBox()
                    box.styleClass.add("left-spacing")
                } else {
                    box.children.add(container)
                }
            }
        }

        if (box.children.isNotEmpty()) {
            if (borderPane.left == null && borderPane.center == null) {
                borderPane.left = box
            } else {
                borderPane.right = box
            }
        }

        return borderPane
    }

    fun createChild(childField: ParameterField): Node {

        val container: Node
        if (labelsAbove == true) {
            container = BorderPane()
            container.top = childField.label
            container.center = childField.control
            childField.label.styleClass.add("small-bottom-pad")
            BorderPane.setAlignment(childField.label, Pos.CENTER)
            BorderPane.setAlignment(childField.control, Pos.CENTER)
        } else if (labelsAbove == false) {
            container = BorderPane()
            container.left = childField.label
            container.center = childField.control
            childField.label.styleClass.add("right-pad")
            BorderPane.setAlignment(childField.label, Pos.CENTER_LEFT)
            BorderPane.setAlignment(childField.control, Pos.CENTER_LEFT)
        } else {
            container = childField.control!!
        }

        return container
    }

    override fun updateField(field: ParameterField) {
        borderPane.left = null
        borderPane.center = null
        borderPane.right = null
        createContent()

        super.updateField(field)
    }

}
