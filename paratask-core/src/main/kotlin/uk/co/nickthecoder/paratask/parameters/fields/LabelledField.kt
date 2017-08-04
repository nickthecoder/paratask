/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.ActionEvent
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter

open class LabelledField(parameter: Parameter, label: String = parameter.label) : ParameterField(parameter) {

    var label: Node

    val expressionButton: ToggleButton?

    val expressionField = TextField()

    init {
        val lab = Label(label)
        this.label = lab
        if (parameter.description != "") {
            lab.tooltip = Tooltip(parameter.description)
        }

        children.add(this.label)
        styleClass.add("field")

        expressionField.styleClass.add("expression")
        if (parameter.isProgrammingMode() && parameter is ValueParameter<*>) {
            expressionButton = ToggleButton("=")
            children.add(expressionButton)
            expressionField.textProperty().bindBidirectional(parameter.expressionProperty)
            if (parameter.expression != null) {
                expressionButton.isSelected = true
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
            if (expressionButton?.isSelected == true) {
                v?.isVisible = false
            }
        }

    private fun controlOrExpression(): Node? = if (expressionButton?.isSelected == true) expressionField else control

    override fun computeMinHeight(width: Double): Double {
        val both = Math.max(label.minHeight(width), controlOrExpression()?.minHeight(width) ?: 0.0)
        val err = if (error.isVisible) error.minHeight(width) else 0.0

        return both + err
    }

    override fun computePrefHeight(width: Double): Double {
        val both = Math.max(label.prefHeight(width), controlOrExpression()?.prefHeight(width) ?: 0.0)
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

    override fun computePrefWidth(height: Double): Double {

        val lab = if (label.isVisible) label.prefWidth(height) + form.spacing else 0.0
        val button = expressionButton?.prefWidth(height) ?: 0.0
        val allThree = lab + button + (controlOrExpression()?.prefWidth(height) ?: 0.0)
        val err = if (error.isVisible) error.prefWidth(height) else 0.0
        return Math.max(allThree, err)
    }

    override fun layoutChildren() {
        val controlOrExp = controlOrExpression()

        form.calculateColumnPreferences()
        form.calculateColumnWidths()

        var x = insets.left
        var y = insets.top

        var h: Double
        var w: Double

        // Label
        if (label.isVisible) {
            h = Math.max(label.prefHeight(-1.0), controlOrExp?.prefHeight(-1.0) ?: 0.0)
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
        controlOrExp?.let {
            val stretchy = parameter.isStretchy() || expressionButton?.isSelected == true
            h = it.prefHeight(-1.0)
            w = if (stretchy) form.columns[2].width else it.prefWidth(h)
            layoutInArea(it, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)
        }

        // Error message
        y += Math.max(label.prefHeight(-1.0), controlOrExp?.prefHeight(-1.0) ?: 0.0)
        x = insets.left

        if (error.isVisible) {
            h = error.prefHeight(-1.0)
            w = width - insets.left - insets.right
            layoutInArea(error, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
        }
    }

    protected fun adjustColumnWidth(column: FieldColumn, node: Node) {
        val prefW = node.prefWidth(-1.0)
        val minW = node.minWidth(-1.0)
        if (column.prefWidth < prefW) {
            column.prefWidth = prefW
        }
        if (column.minWidth < minW) {
            column.minWidth = minW
        }
    }

    fun adjustColumnWidths(columns: List<FieldColumn>) {
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
        control?.isVisible = expressionButton?.isSelected == false
    }
}