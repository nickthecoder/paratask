package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener

class HorizontalGroupField(val groupParameter: AbstractGroupParameter)
    : ParameterField(groupParameter), FieldParent, ParameterListener {

    val vBox = VBox()

    val hBox = HBox()

    val fieldSet = mutableListOf<ParameterField>()
    val containers = mutableListOf<Node>()

    /**
     * A single error label beneath the fields. If more than one field is in error, only one error is displayed.
     * Named error2 to avoid clashing with the error for THIS ParameterField (which will never be used, as
     * GroupParameters are never in error).
     */
    var error2: Label? = null

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createControl(): Node {
        buildContent()
        hBox.styleClass.add("horizontal-group")
        vBox.children.add(hBox)
        vBox.styleClass.add("horizontal-container")
        return vBox
    }

    fun buildContent() {
        hBox.children.clear()
        groupParameter.children.forEach { child ->
            val field = child.createField()
            field.fieldParent = this

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

        if ((!field.parameter.hidden) && field.error.isVisible && field.error !== error2) {
            error2 = field.error
            vBox.children.add(error2)
        } else if (error2 === field.error) {
            vBox.children.remove(error2)
            error2 = null
        }
    }

}
