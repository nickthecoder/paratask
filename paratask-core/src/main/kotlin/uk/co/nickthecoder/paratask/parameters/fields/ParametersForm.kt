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

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.gui.ScriptVariables
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.ParentParameter

/**
 * Contains a list of {@link ParametersField}s layed out vertically, so that the controls line up (sharing the same x coordinate).
 * This is the base class for GroupParmetersForm and MultipleField.
 */
class ParametersForm(val parentParameter: ParentParameter)
    : Region(), FieldParent {

    private val columns = mutableListOf<FieldColumn>()

    val formFields = mutableListOf<FormField>()

    var scriptVariables: ScriptVariables? = null

    init {
        styleClass.add("parametersForm")

        columns.add(FieldColumn(0.0)) // Label
        columns.add(FieldColumn()) // Main Control
        styleClass.add("form")
    }

    override fun findParametersForm() = this

    override fun iterator(): Iterator<ParameterField> {
        return formFields.map { it.parameterField }.iterator()
    }

    fun buildContent() {
        buildTop()
        buildChildren()
    }

    fun buildTop() {
        if (parentParameter.description.isNotEmpty()) {
            val textFlow = TextFlow(Text(parentParameter.description))
            textFlow.prefWidth = 500.0
            children.add(textFlow)
        }
    }

    fun buildChildren() {
        parentParameter.children.forEach { child ->
            addParameter(child)
        }
    }

    fun clear() {
        children.clear()
        formFields.clear()
    }

    fun add(node: Node) {
        children.add(node)
    }

    fun addParameter(parameter: Parameter): ParameterField {

        val parameterField = parameter.createField()
        val formField = FormField(parameterField)

        children.add(formField)

        if (parameter.hidden && !parameter.isProgrammingMode()) {
            parameterField.control?.isVisible = false
        }
        parameterField.fieldParent = this
        formFields.add(formField)

        updateField(parameterField)
        return parameterField
    }

    fun descendants(): List<ParameterField> {
        val list = mutableListOf<ParameterField>()

        fun addThem(fieldParent: FieldParent) {
            fieldParent.forEach { parameterField ->
                list.add(parameterField)
                if (parameterField is FieldParent) {
                    addThem(parameterField)
                }
            }

        }
        addThem(this)
        return list
    }

    fun findField(parameter: Parameter): ParameterField? {
        descendants().forEach { field ->
            if (field.parameter === parameter) {
                return field
            }
        }
        return null
    }

    val spacing: Double
        get() = spacingProperty.get()

    /**
     * Gets the -fx-spacing property from css.
     */
    var spacingProperty: StyleableDoubleProperty = object : StyleableDoubleProperty(5.0) {
        override fun getName() = "spacingProperty"
        override fun getBean() = this@ParametersForm

        override fun getCssMetaData(): CssMetaData<ParametersForm, Number> {
            return SPACING
        }

        override fun invalidated() {
            requestLayout()
        }
    }

    fun calculateColumnPreferences() {
        columns.forEach {
            it.prefWidth = 0.0
            it.minWidth = 0.0
        }
        formFields.forEach { formField ->
            formField.adjustColumnWidths(columns)
        }
    }

    fun calculateColumnWidths() {
        var totalStretch: Double = 0.0
        var prefWidth = -spacing
        columns.forEach {
            totalStretch += it.stretch
            prefWidth += it.prefWidth + if (it.prefWidth == 0.0) 0.0 else spacing
        }

        val extra = width - insets.left - insets.right - prefWidth
        if (totalStretch == 0.0) {
            columns.forEach {
                it.width = it.prefWidth
            }
        } else {
            columns.forEach {
                it.width = it.prefWidth + extra * it.stretch / totalStretch
            }
        }
        formFields.forEach { formField ->
            formField.adjustColumnWidths(columns)
        }
    }

    override fun computeMaxWidth(height: Double): Double = Double.MAX_VALUE

    override fun computeMaxHeight(height: Double): Double = Double.MAX_VALUE

    override fun computeMinWidth(height: Double): Double {
        calculateColumnPreferences()

        var w = spacing

        columns.forEach {
            w += it.minWidth
        }

        return w + insets.left + insets.right
    }

    override fun computePrefWidth(height: Double): Double {
        calculateColumnPreferences()

        var w = -spacing

        columns.forEach {
            w += it.prefWidth + spacing
        }

        val mchildren: List<Node> = getManagedChildren<Node>().filter { it.isVisible }
        val otherWidths = mchildren.filter { it !is ParameterField }.map { it.prefWidth(height) }.max()
        return Math.max(w, otherWidths ?: 0.0) + insets.left + insets.right
    }

    override fun computeMinHeight(width: Double): Double {
        val mchildren: List<Node> = getManagedChildren<Node>().filter { it.isVisible }
        val sum = sum(mchildren, Node::minHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun computePrefHeight(w: Double): Double {
        val width = (if (w == -1.0) computePrefWidth(-1.0) else w) - insets.left - insets.right

        val mchildren: List<Node> = getManagedChildren<Node>().filter { it.isVisible }
        val sum = sum(mchildren, Node::prefHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun layoutChildren() {
        val x = insets.left
        var y = insets.top
        val w = width - insets.left - insets.right

        getManagedChildren<Node>().filter { it.isVisible }.forEach {
            val h = it.prefHeight(w)
            layoutInArea(it, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            y += h + spacing
        }
    }

    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
    }

    override fun updateField(field: ParameterField) {
        requestLayout()
        findFormField(field)?.requestLayout()
    }

    fun findFormField(parameterField: ParameterField): FormField? {
        return formFields.firstOrNull { it.parameterField === parameterField }
    }

    companion object {

        internal val cssMetaDataList = mutableListOf<CssMetaData<out Styleable, *>>()

        internal val SPACING = object : CssMetaData<ParametersForm, Number>("-fx-spacing", StyleConverter.getSizeConverter(), 0.0) {
            override fun isSettable(form: ParametersForm): Boolean = true

            override fun getStyleableProperty(form: ParametersForm): StyleableDoubleProperty {
                return form.spacingProperty
            }
        }

        init {
            Pane.getClassCssMetaData().forEach {
                cssMetaDataList.add(it)
            }
            cssMetaDataList.add(SPACING)
        }

        internal fun sum(nodes: List<Node>, f: (Node, Double) -> Double, other: Double): Double {
            var sum = 0.0

            var count = 0
            nodes.forEach {
                count++
                sum += f(it, other)
            }
            return sum
        }

    }

    inner class FormField(val parameterField: ParameterField) : Region() {

        init {
            if (parameterField.hasLabel) {
                children.add(parameterField.labelNode)
            }
            children.addAll(parameterField.controlContainer, parameterField.error)
        }

        override fun computeMinHeight(width: Double): Double {
            val controlHeight = parameterField.controlContainer?.minHeight(width) ?: 0.0
            val both = if (parameterField.hasLabel) {
                Math.max(parameterField.labelNode.minHeight(width), controlHeight)
            } else {
                controlHeight
            }
            val err = if (parameterField.error.isVisible) parameterField.error.minHeight(width) else 0.0

            return both + err
        }

        override fun computePrefHeight(width: Double): Double {
            val controlHeight = parameterField.controlContainer?.prefHeight(width) ?: 0.0
            val both = if (parameterField.hasLabel) {
                Math.max(parameterField.labelNode.prefHeight(width), controlHeight)
            } else {
                controlHeight
            }
            val err = if (parameterField.error.isVisible) parameterField.error.prefHeight(width) else 0.0
            return both + err
        }

        override fun computeMinWidth(height: Double): Double {

            val lab = if (parameterField.hasLabel) {
                if (parameterField.labelNode.isVisible) {
                    parameterField.labelNode.minWidth(height) + spacing
                } else {
                    0.0
                }
            } else {
                0.0
            }
            val main = lab + spacing + (parameterField.controlContainer?.minWidth(height) ?: 0.0)
            val err = if (parameterField.error.isVisible) parameterField.error.minWidth(height) else 0.0
            return Math.max(main, err)
        }

        override fun computePrefWidth(height: Double): Double {

            val lab = if (parameterField.hasLabel) {
                if (parameterField.labelNode.isVisible) {
                    parameterField.labelNode.prefWidth(height) + spacing
                } else {
                    0.0
                }
            } else {
                0.0
            }
            val main = lab + spacing + (parameterField.controlContainer?.prefWidth(height) ?: 0.0)
            val err = if (parameterField.error.isVisible) parameterField.error.prefWidth(height) else 0.0
            return Math.max(main, err)
        }

        override fun layoutChildren() {
            val controlContainer = parameterField.controlContainer!!

            calculateColumnPreferences()
            calculateColumnWidths()

            var x = insets.left
            var y = insets.top

            var h: Double
            var w: Double

            // Label
            val labelHeight: Double
            if (parameterField.hasLabel) {
                if (parameterField.labelNode.isVisible) {
                    h = Math.max(parameterField.labelNode.prefHeight(-1.0), controlContainer.prefHeight(-1.0))
                    w = columns[0].width
                    layoutInArea(parameterField.labelNode, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)
                    x += w + spacing
                    labelHeight = parameterField.labelNode.prefHeight(-1.0)
                } else {
                    labelHeight = 0.0
                }
            } else {
                labelHeight = 0.0
            }

            // Control
            val stretchy = parameterField.parameter.isStretchy() || parameterField.expressionButton?.isSelected == true
            h = controlContainer.prefHeight(-1.0)
            w = if (stretchy) {
                if (parameterField.hasLabel) {
                    columns[1].width
                } else {
                    width - insets.left - insets.right
                }
            } else {
                controlContainer.prefWidth(h)
            }
            layoutInArea(controlContainer, x, y, w, h, 0.0, HPos.LEFT, VPos.CENTER)

            // Error message
            y += Math.max(labelHeight, controlContainer.prefHeight(-1.0))
            x = insets.left

            if (parameterField.error.isVisible) {
                h = parameterField.error.prefHeight(-1.0)
                w = width - insets.left - insets.right
                layoutInArea(parameterField.error, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            }
        }

        private fun adjustColumnWidth(column: FieldColumn, node: Node) {
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
            if (parameterField.hasLabel) {
                adjustColumnWidth(columns[0], parameterField.labelNode)
            }
            adjustColumnWidth(columns[1], parameterField.controlContainer!!)
        }
    }

}
