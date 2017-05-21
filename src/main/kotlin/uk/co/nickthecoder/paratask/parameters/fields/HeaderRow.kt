package uk.co.nickthecoder.paratask.parameters.fields

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.Parameter

class HeaderRow() : Region() {

    val fieldPositions = mutableListOf<FieldPosition>()

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
        //parameterField.form = this
        fieldPositions.add(FieldPosition(this, parameterField))
        children.add(parameterField)

        return this
    }


    val spacing: Double
        get() = spacingProperty.get()


    override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
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
                extra
            } else {
                it.columns[2].width = it.columns[2].prefWidth
            }

            layoutInArea(field, x, y, w, h, 0.0, HPos.LEFT, VPos.TOP)
            x += w + spacing
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