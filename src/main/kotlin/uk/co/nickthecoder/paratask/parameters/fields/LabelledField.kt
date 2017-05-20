package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.ActionEvent
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import uk.co.nickthecoder.paratask.parameters.ValueParameter

open class LabelledField(parameter: ValueParameter<*>, label: String = parameter.label) : ParameterField(parameter) {

    var label: Node

    val expressionButton: ToggleButton?

    val expressionField = TextField()

    val valueParameter = parameter

    init {
        val lab = Label(label)
        this.label = lab
        if (parameter.description != "") {
            lab.tooltip = Tooltip(parameter.description)
        }

        children.add(this.label)
        getStyleClass().add("field");

        expressionField.getStyleClass().add("expression")
        if (parameter.isProgrammingMode()) {
            expressionButton = ToggleButton("=")
            children.add(expressionButton)
            expressionField.textProperty().bindBidirectional(parameter.expressionProperty)
            if (parameter.expression != null) {
                expressionButton.setSelected(true)
                children.add(expressionField)
            }
            expressionButton.addEventHandler(ActionEvent.ACTION) { onExpression() }

        } else {
            expressionButton = null
        }

    }

    /**
     * When placed in a MultipleField, the label is replaced by "+" and "-" buttons
     */
    fun replaceLabel(node: Node) {
        children.remove(label)
        label = node
        children.add(label)
    }

    override var control: Node?
        get() = super.control
        set(v) {
            super.control = v
            if (expressionButton?.isSelected() == true) {
                v?.setVisible(false)
            }
        }

    private fun controlOrExpression(): Node? = if (expressionButton?.isSelected() == true) expressionField else control

    override fun computeMinHeight(width: Double): Double {
        val both = Math.max(label.minHeight(width), controlOrExpression()?.minHeight(width) ?: 0.0)
        val err = if (error.isVisible) error.minHeight(width) else 0.0

        return both + err
    }

    override fun computePrefHeight(width: Double): Double {
        val both = Math.max(label.prefHeight(width), controlOrExpression()?.minHeight(width) ?: 0.0)
        val allThree = Math.max(both, expressionButton?.prefHeight(width) ?: 0.0)
        val err = if (error.isVisible) error.prefHeight(width) else 0.0

        return allThree + err
    }

    override fun computeMinWidth(height: Double): Double {

        val lab = if (label.isVisible) label.minWidth(height) + form.spacing else 0.0
        val button = expressionButton?.minWidth(height) ?: 0.0
        val allThree = lab + button + (controlOrExpression()?.minWidth(height) ?: 0.0)
        val err = if (error.isVisible) error.minWidth(height) else 0.0
        return Math.max(allThree, err)
    }

    override fun layoutChildren() {
        val controlOrExp = controlOrExpression()

        form.calculateColumnPreferences()
        form.calculateColumnWidths()

        var x = insets.left
        var y = insets.top

        var h = Math.max(label.prefHeight(-1.0), controlOrExp?.prefHeight(-1.0) ?: 0.0)
        var w: Double

        // Label
        if (label.isVisible) {
            w = form.columns[0].width
            layoutInArea(label, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)
            x += w + form.spacing
        }

        // Expression Button
        expressionButton?.let {
            w = form.columns[1].width
            h = it.prefHeight(-1.0)
            layoutInArea(it, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            x += w + form.spacing
        }

        // Control
        val stretchy = parameter.isStretchy() || expressionButton?.isSelected == true
        w = if (stretchy) form.columns[2].width else controlOrExp?.prefWidth(h) ?: 0.0
        layoutInArea(controlOrExp, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)
        x += w + form.spacing

        // Error message
        y += Math.max(label.prefHeight(-1.0), controlOrExp?.prefHeight(-1.0) ?: 0.0)
        x = insets.left

        if (error.isVisible) {
            h = error.prefHeight(-1.0)
            w = width - insets.left - insets.right
            layoutInArea(error, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
        }
    }

    protected fun adjustColumnWidth(column: ParametersForm.FormColumn, node: Node) {
        val prefW = node.prefWidth(-1.0)
        val minW = node.minWidth(-1.0)
        if (column.prefWidth < prefW) {
            column.prefWidth = prefW
        }
        if (column.minWidth < minW) {
            column.minWidth = minW
        }
    }

    internal fun adjustColumnWidths(columns: List<ParametersForm.FormColumn>) {
        adjustColumnWidth(columns[0], label)
        expressionButton?.let { adjustColumnWidth(columns[1], it) }
        controlOrExpression()?.let { adjustColumnWidth(columns[2], it) }
    }

    fun showOrClearError(message: String?) {
        if (message == null) {
            clearError()
        } else {
            showError(message)
        }
    }

    fun onExpression() {
        if (expressionButton?.isSelected == true) {
            children.add(expressionField)
            expressionField.text = ""
        } else {
            children.remove(expressionField)
            expressionField.text = null
        }
        control?.setVisible(expressionButton?.isSelected == false)
    }
}
