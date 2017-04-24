package uk.co.nickthecoder.paratask.gui

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.gui.ParametersForm.Column
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values

open class ParameterField : Region {

    lateinit var form: ParametersForm

    open val parameter: Parameter

    val label: Label

    val error = Label()

    var control: Node? = null
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

    constructor(parameter: Parameter) {

        this.parameter = parameter
        this.label = Label(parameter.name)

        getStyleClass().add("field");
        error.setVisible(false)
        error.getStyleClass().add("error");

        children.add(this.label)
        children.add(error)
    }

    override fun computeMinHeight(width: Double): Double {
        val both = Math.max(label.minHeight(width), control?.minHeight(width) ?: 0.0)
        val err = if (error.isVisible) error.minHeight(width) else 0.0

        return both + err
    }

    override fun computePrefHeight(width: Double): Double {
        val both = Math.max(label.prefHeight(width), control?.minHeight(width) ?: 0.0)
        val err = if (error.isVisible) error.prefHeight(width) else 0.0

        return both + err
    }

    override fun computeMinWidth(height: Double): Double {
        val both = label.minWidth(height) + form.spacing + (control?.minWidth(height) ?: 0.0)
        val err = if (error.isVisible) error.minWidth(height) else 0.0

        return Math.max(both, err)
    }

    override fun layoutChildren() {
        form.calculateColumnPreferences()
        form.calculateColumnWidths()

        var x = insets.left
        var y = insets.top

        // Label
        var h = Math.max(label.prefHeight(-1.0), control?.prefHeight(-1.0) ?: 0.0)
        var w = form.columns[0].width
        layoutInArea(label, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)

        // Control
        x += w + form.spacing
        w = if (parameter.isStretchy()) form.columns[1].width else control?.prefWidth(h) ?: 0.0
        layoutInArea(control, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)

        // Error message
        y += Math.max(label.prefHeight(-1.0), control?.prefHeight(-1.0) ?: 0.0)
        x = insets.left

        if (error.isVisible) {
            h = error.prefHeight(-1.0)
            w = width - insets.left - insets.right
            layoutInArea(error, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
        }
    }

    protected fun adjustColumnWidth(column: Column, node: Node) {
        val prefW = node.prefWidth(-1.0)
        val minW = node.minWidth(-1.0)
        if (column.prefWidth < prefW) {
            column.prefWidth = prefW
        }
        if (column.minWidth < minW) {
            column.minWidth = minW
        }
    }

    internal fun adjustColumnWidths(columns: List<ParametersForm.Column>) {
        adjustColumnWidth(columns[0], label)
        control?.let { adjustColumnWidth(columns[1], it) }
    }


    fun showOrClearError(message: String?) {
        if (message == null) {
            clearError()
        } else {
            showError(message)
        }
    }

    fun showError(message: String) {
        error.text = message
        error.visibleProperty().value = true
    }

    fun clearError() {
        error.visibleProperty().value = false
    }

    fun hasError(): Boolean = error.visibleProperty().value == true
}
