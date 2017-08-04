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
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.FocusHelper
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.util.fireTabToFocusNext

class HeaderRow() : Region(), FocusListener {

    val fieldPositions = mutableListOf<FieldPosition>()

    private var goButton: Button? = null

    var focusHelper: FocusHelper? = null

    init {
        styleClass.add("header-row")
    }

    fun addAll(vararg parameters: Parameter): HeaderRow {
        parameters.forEach { add(it) }
        return this
    }

    fun add(parameter: Parameter): HeaderRow {

        if (parameter.parent == null) {
            throw ParameterException(parameter, "Does not have a parent so cannot be added to a form")
        }
        val parameterField = parameter.createField()

        parameterField.styleClass.add("field-${parameter.name}")
        fieldPositions.add(FieldPosition(this, parameterField))
        children.add(parameterField)

        return this
    }

    fun addRunButton(tool: Tool, scene: Scene) {
        val button = ParataskActions.TOOL_RUN.createButton() { tool.toolPane!!.parametersPane.run() }
        with(button) {
            button.setDefaultButton(true)
            goButton = this
        }
        children.add(goButton)
        focusHelper = FocusHelper(this.parent, this, scene = scene, name = "HeaderRow")
    }

    override fun focusChanged(gained: Boolean) {
        goButton?.setDefaultButton(gained)
    }

    fun detaching() {
        focusHelper?.let { it.remove() }
    }

    val spacing: Double
        get() = spacingProperty.get()


    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
    }

    fun focus() {
        fireTabToFocusNext()
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
        return fieldPositions.map { it.field.minHeight(-1.0) }.max() ?: 0.0
    }

    override fun computePrefHeight(w: Double): Double {
        return fieldPositions.map { it.field.prefHeight(-1.0) }.max() ?: 0.0
    }

    override fun layoutChildren() {

        var x = insets.left
        val y = insets.top

        var stretchies = 0
        var slack = width - insets.left - insets.right + spacing
        goButton?.let { slack -= spacing + it.prefWidth(-1.0) }

        fieldPositions.forEach() {
            val field = it.field

            it.calculateColumnPreferences()

            slack -= field.prefWidth(-1.0) + spacing
            if (field.parameter.isStretchy()) {
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

        fieldPositions.forEach {
            val field = it.field

            var w = field.prefWidth(-1.0)
            val h = field.prefHeight(w)

            it.columns[0].width = it.columns[0].prefWidth
            if (field.parameter.isStretchy()) {
                w += extra
                it.columns[2].width = it.columns[2].prefWidth + extra
            } else {
                it.columns[2].width = it.columns[2].prefWidth
            }

            layoutInArea(field, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            x += w + spacing
        }

        goButton?.let {
            layoutInArea(it, x, y, it.prefWidth(-1.0), prefHeight(-1.0), 0.0, HPos.LEFT, VPos.TOP)
        }
    }


    data class FieldPosition(val headerRow: HeaderRow, val field: ParameterField) : FieldParent {

        override val spacing: Double
            get() = headerRow.spacing

        override val columns = mutableListOf(FieldColumn(0.0), FieldColumn(0.0), FieldColumn(1.0))

        init {
            field.form = this
        }

        override fun calculateColumnWidths() {}

        override fun calculateColumnPreferences() {
            columns.forEach {
                it.prefWidth = 0.0
                it.minWidth = 0.0
            }
            if (field is LabelledField) {
                field.adjustColumnWidths(columns)
            }
        }

    }

}