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
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.*

/**
 * Contains a list of {@link ParametersField}s layed out vertically, so that the controls line up (sharing the same x coordinate).
 * This is the base class for GroupParmetersForm and MultipleField.
 */
open class ParametersForm(val parentParameter: ParentParameter)
    : Region(), FieldParent, HasChildFields, ParameterListener {

    override val columns = mutableListOf<FieldColumn>()

    override val fieldSet = mutableListOf<ParameterField>()

    init {
        styleClass.add("parametersForm")
        parentParameter.parameterListeners.add(this)

        columns.add(FieldColumn(0.0)) // Label
        columns.add(FieldColumn(0.0)) // Expression button
        columns.add(FieldColumn()) // Main Control
        styleClass.add("form")
    }

    fun tidyUp() {
        parentParameter.parameterListeners.remove(this)
        fieldSet.forEach {
            if (it is ParametersForm) {
                it.tidyUp()
            }
        }
    }

    open fun buildContent() {
        buildTop()
        buildChildren()
    }

    open fun buildTop() {
        if (parentParameter.description.isNotEmpty()) {
            val textFlow = TextFlow(Text(parentParameter.description))
            textFlow.prefWidth = 500.0
            children.add(textFlow)
        }
    }

    open fun buildChildren() {
        var index = 0
        parentParameter.children.forEach { child ->
            addParameter(child, index)
            index++
        }
    }

    fun clear() {
        children.clear()
        fieldSet.clear()
    }

    fun add(node: Node) {
        children.add(node)
    }

    open fun addParameter(parameter: Parameter, index: Int): Node {

        if (parameter.parent == null) {
            throw ParameterException(parameter, "Does not have a parent so cannot be added to a form")
        }
        val parameterField = parameter.createField()

        val node = if (parameterField is WrappableField) {
            parameterField.wrap()
        } else {
            parameterField
        }

        children.add(node)

        if (parameter.hidden) {
            parameterField.isVisible = false
        }
        parameterField.styleClass.add("field-${parameter.name}")
        parameterField.form = this
        fieldSet.add(parameterField)

        return node
    }

    fun descendants(): List<ParameterField> {
        val list = mutableListOf<ParameterField>()

        fun addThem(form: HasChildFields) {
            form.fieldSet.forEach {
                list.add(it)
                if (it is HasChildFields) {
                    addThem(it)
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

    override val spacing: Double
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

    override fun calculateColumnPreferences() {
        columns.forEach {
            it.prefWidth = 0.0
            it.minWidth = 0.0
        }
        fieldSet.forEach { field ->
            if (field is LabelledField) {
                field.adjustColumnWidths(columns)
            }
        }
    }

    override fun calculateColumnWidths() {
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
        fieldSet.forEach { field ->
            if (field is LabelledField) {
                field.adjustColumnWidths(columns)
            }
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

        val mchildren: List<Node> = getManagedChildren()
        val otherWidths = mchildren.filter { it !is ParameterField }.map { it.prefWidth(height) }.max()
        return Math.max(w, otherWidths ?: 0.0) + insets.left + insets.right
    }

    override fun computeMinHeight(width: Double): Double {
        val mchildren: List<Node> = getManagedChildren()
        val sum = sum(mchildren, Node::minHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun computePrefHeight(w: Double): Double {
        val width = (if (w == -1.0) computePrefWidth(-1.0) else w) - insets.left - insets.right

        val mchildren: List<Node> = getManagedChildren()
        val sum = sum(mchildren, Node::prefHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun layoutChildren() {
        val x = insets.left
        var y = insets.top
        val w = width - insets.left - insets.right

        getManagedChildren<Node>().forEach {
            //if ( it !is ParameterField ) {
            //    println( "Laying out ${it} using width ${w}")
            //}
            if (it.isVisible) {
                val h = it.prefHeight(w)
                layoutInArea(it, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
                y += h + spacing
            }
        }
    }

    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
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

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.VISIBILITY) {
            val hidden = event.parameter.hidden
            val field = fieldSet.filter { it.parameter === event.parameter }.firstOrNull()
            if (field != null) {
                field.isVisible = !hidden
                requestLayout()
            }
        }
    }
}