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

package uk.co.nickthecoder.paratask.project

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.defaultWhileFocusWithin
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.fields.FieldParent
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.fireTabToFocusNext

class HeaderRow(vararg parameters: Parameter)
    : Region(), FieldParent {

    val boxedFields = mutableListOf<BoxedField>()

    private var goButton: Button? = null

    /**
     * A single error label beneath the fields. If more than one field is in error, only one error is displayed.
     */
    var error: Label? = null

    init {
        styleClass.add("header-row")
        addAll(*parameters)
    }

    fun addAll(vararg parameters: Parameter): HeaderRow {
        parameters.forEach { add(it) }
        return this
    }

    fun add(parameter: Parameter): HeaderRow {

        if (parameter.parent == null) {
            throw ParameterException(parameter, "Does not have a parent")
        }
        val parameterField = parameter.createField()
        parameterField.fieldParent = this
        val boxedField = BoxedField(parameterField)

        boxedFields.add(boxedField)
        children.add(boxedField)

        return this
    }

    fun addRunButton(tool: Tool) {
        val button = ParataskActions.TOOL_RUN.createButton { tool.toolPane!!.parametersPane.run() }
        with(button) {
            goButton = this
        }
        children.add(goButton)
        goButton?.defaultWhileFocusWithin(this.parent, "HeaderRow")
    }

    fun detaching() {
    }

    val spacing: Double
        get() = spacingProperty.get()

    val ySpacing = 4.0

    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
    }

    fun focus() {
        ParaTaskApp.logFocus("HeaederRow. focus. fireTabToFocusNext()")
        fireTabToFocusNext()
    }

    override fun updateField(field: ParameterField) {
        if ((!field.parameter.hidden) && field.error.isVisible && field.error !== error) {
            error = field.error
            children.add(error)
        } else if (error === field.error) {
            children.remove(error)
            error = null
        }
        requestLayout()
    }

    override fun iterator(): Iterator<ParameterField> {
        return boxedFields.map { it.parameterField }.iterator()
    }

    /**
     * Gets the -fx-spacing property from css.
     */
    var spacingProperty: StyleableDoubleProperty = object : StyleableDoubleProperty(5.0) {
        override fun getName() = "spacingProperty"
        override fun getBean() = this@HeaderRow

        override fun getCssMetaData(): CssMetaData<HeaderRow, Number> {
            return SPACING
        }

        override fun invalidated() {
            requestLayout()
        }
    }


    override fun computeMinWidth(height: Double): Double = 0.0

    override fun computePrefWidth(height: Double): Double = 0.0

    override fun computeMinHeight(width: Double): Double {
        return boxedFields.map { it.minHeight(-1.0) }.max() ?: 0.0
    }

    override fun computePrefHeight(w: Double): Double {
        val err = error?.prefHeight(-1.0) ?: -ySpacing
        return err + ySpacing + (boxedFields.map { it.prefHeight(-1.0) }.max() ?: 0.0)
    }

    override fun layoutChildren() {

        var x = insets.left
        var y = insets.top

        var maxHeight = 0.0

        var stretchies = 0
        var slack = width - insets.left - insets.right + spacing
        goButton?.let { slack -= spacing + it.prefWidth(-1.0) }

        boxedFields.forEach { boxedField ->

            slack -= boxedField.prefWidth(-1.0) + spacing
            if (boxedField.parameterField.parameter.isStretchy()) {
                stretchies++
            }
        }
        var gap = slack / 2
        var extra = 0.0
        if (stretchies > 0) {
            gap = 0.0
            extra = slack / stretchies
        }

        x += gap

        boxedFields.forEach { boxedField ->
            var w = boxedField.prefWidth(-1.0)
            val h = boxedField.prefHeight(w)

            if (boxedField.parameterField.parameter.isStretchy()) {
                w += extra
            }

            layoutInArea(boxedField, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            x += w + spacing
            if (h > maxHeight) {
                maxHeight = h
            }
        }

        goButton?.let {
            layoutInArea(it, x, y, it.prefWidth(-1.0), prefHeight(-1.0), 0.0, HPos.LEFT, VPos.TOP)
        }

        error?.let {
            y += maxHeight + ySpacing
            x = insets.left
            layoutInArea(it, x, y, it.prefWidth(-1.0), it.prefHeight(-1.0), 0.0, HPos.LEFT, VPos.TOP)
        }
    }


    companion object {

        internal val cssMetaDataList = mutableListOf<CssMetaData<out Styleable, *>>()

        internal val SPACING = object : CssMetaData<HeaderRow, Number>("-fx-spacing", StyleConverter.getSizeConverter(), 0.0) {
            override fun isSettable(form: HeaderRow): Boolean = true

            override fun getStyleableProperty(form: HeaderRow): StyleableDoubleProperty {
                return form.spacingProperty
            }
        }

        init {
            Pane.getClassCssMetaData().forEach {
                cssMetaDataList.add(it)
            }
            cssMetaDataList.add(SPACING)
        }
    }


    class BoxedField(val parameterField: ParameterField) : BorderPane() {

        init {
            if (parameterField is LabelledField) {
                left = parameterField.labelNode
                setAlignment(left, Pos.CENTER_LEFT)
                left.styleClass.add("right-pad-label")
            }
            center = parameterField.control
            setAlignment(center, Pos.CENTER_LEFT)
        }
    }
}
