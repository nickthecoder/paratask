package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.parameters.Parameter

open class ParameterField(open val parameter: Parameter) : Region() {

    lateinit var form: FieldParent

    val error = Label()

    init {
        error.isVisible = false
        error.styleClass.add("error")

        children.add(error)
    }

    open var control: Node? = null
        set(v) {
            if (field != null) {
                children.remove(field)
            }
            field = v
            if (v != null) {
                v.styleClass.add("control")
                children.add(v)
            }
        }

    fun showError(message: String) {
        error.text = message
        error.visibleProperty().value = true
    }

    fun clearError() {
        error.visibleProperty().value = false
    }

    open fun isDirty(): Boolean = false

}
