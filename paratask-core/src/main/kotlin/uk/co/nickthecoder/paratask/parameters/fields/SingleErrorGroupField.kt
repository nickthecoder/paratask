package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener


abstract class SingleErrorGroupField(
        val groupParameter: AbstractGroupParameter,
        isBoxed: Boolean)

    : ParameterField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val vBox = VBox()

    /**
     * A single error label beneath the fields. If more than one field is in error, only one error is displayed.
     * Named error2 to avoid clashing with the error for THIS ParameterField (which will never be used, as
     * GroupParameters are never in error).
     */
    var error2: Label? = null

    override fun createControl(): Node {
        vBox.children.add(createContent())
        vBox.styleClass.add("horizontal-container")
        return vBox
    }

    abstract fun createContent(): Node

    override fun updateField(field: ParameterField) {

        if ((!field.parameter.hidden) && field.error.isVisible && field.error !== error2) {
            error2 = field.error
            vBox.children.add(error2)
        } else if (error2 === field.error) {
            vBox.children.remove(error2)
            error2 = null
        }
    }

}
