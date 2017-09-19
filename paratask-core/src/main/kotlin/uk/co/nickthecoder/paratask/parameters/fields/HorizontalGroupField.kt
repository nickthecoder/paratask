package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.LabelPosition
import uk.co.nickthecoder.paratask.parameters.ParameterListener
import uk.co.nickthecoder.paratask.util.focusNext

open class HorizontalGroupField(
        groupParameter: GroupParameter,
        val labelPosition: LabelPosition,
        isBoxed: Boolean)

    : SingleErrorGroupField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val borderPane = BorderPane()

    val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    /**
     * Holds a cache of all child fields, so that when we re-build the content (due to a field changing visibility),
     * we do not need to rebuild any fields (which is expensive, because each field will have listeners!)
     */
    val fieldMap = mutableMapOf<String, ParameterField>()

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createContent(): Node {

        // Create a cache of all ParameterFields, so that if this is called again, we don't need to create them again.
        if (fieldMap.isEmpty()) {
            groupParameter.children.forEach { child ->
                val field = child.createField()
                fieldMap.put(child.name, field)
            }
        }

        var box = HBox()
        var foundStretchy: Boolean = false

        groupParameter.children.forEach { child ->
            val field = fieldMap[child.name]

            if (field != null) {

                field.fieldParent = this

                val container = createChild(field)

                fieldSet.add(field)
                containers.add(container)

                box.styleClass.add("horizontal-group")
                if (!child.hidden) {
                    if (child.isStretchy() && !foundStretchy) {
                        if (box.children.isNotEmpty()) {
                            borderPane.left = box
                            box.styleClass.add("right-spacing")
                        }
                        foundStretchy = true
                        borderPane.center = container
                        box = HBox()
                        box.styleClass.addAll("horizontal-group", "left-spacing")
                    } else {
                        box.children.add(container)
                    }
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

        if (labelPosition == LabelPosition.TOP) {
            container = BorderPane()
            container.top = childField.label
            container.center = childField.control
            childField.label.styleClass.add("small-bottom-pad")
            BorderPane.setAlignment(childField.label, Pos.CENTER)
            BorderPane.setAlignment(childField.control, Pos.CENTER)

        } else if (labelPosition == LabelPosition.LEFT) {
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

        // Remember the focus owner, and set it back later
        // This is a bodge because sometimes the focus owner will be removed from the scene and put back again,
        // in which case things can go wrong. In particular, ResourceParameter's "File" or "URL", combobox
        // doesn't work without losing and gaining focus.
        val focusOwner = borderPane.scene?.focusOwner
        createContent()

        focusOwner?.scene?.root?.requestFocus()
        Platform.runLater {
            focusOwner?.focusNext()
        }
    }

}
