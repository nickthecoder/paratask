package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.parameter.Parameter

open class ParameterField(open val parameter: Parameter) : Region() {

    lateinit var form: ParametersForm

    val error = Label()

    init {
        error.setVisible(false)
        error.getStyleClass().add("error");

        children.add(error)
    }

    open var control: Node? = null
        set(v) {
            if (field != null) {
                children.remove(field)
            }
            field = v
            if (v != null) {
                v.getStyleClass().add("control")
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
