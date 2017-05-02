package uk.co.nickthecoder.paratask.gui.field

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values

/**
 * Contains a list of {@link ParametersField}s layed out vertically, so that the controls line up (sharing the same x coordinate).
 */
open class ParametersForm(parameter: Parameter)
    : ParameterField(parameter) {

    internal val columns = mutableListOf<Column>()

    internal val fieldSet = mutableListOf<ParameterField>()

    fun descendants(): List<ParameterField> {
        val list = mutableListOf<ParameterField>()

        fun addThem(form: ParametersForm) {
            form.fieldSet.forEach {
                list.add(it)
                if (it is ParametersForm) {
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

    val spacing: Double
        get() {
            return spacingProperty.get()
        }

    /**
     * Gets the -fx-spacing property from css.
     */
    var spacingProperty: StyleableDoubleProperty = object : StyleableDoubleProperty(5.0) {
        override fun getName() = "spacingProperty"
        override fun getBean() = this@ParametersForm

        override fun getCssMetaData(): CssMetaData<ParametersForm, Number> {
            return SPACING;
        }

        override fun invalidated() {
            requestLayout();
        }
    }

    init {
        columns.add(Column(0.0))
        columns.add(Column())
        getStyleClass().add("form");
    }

    internal fun calculateColumnPreferences() {
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

    internal fun calculateColumnWidths() {
        var totalStretch: Double = 0.0
        var prefWidth = spacing
        columns.forEach {
            totalStretch += it.stretch
            prefWidth += it.prefWidth
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

        return w + insets.left + insets.right
    }

    override fun computeMinHeight(width: Double): Double {
        val mchildren: List<Node> = getManagedChildren()
        val sum = sum(mchildren, Node::minHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun computePrefHeight(w: Double): Double {
        val width = if (w == -1.0) computePrefWidth(1.0) else w

        val mchildren: List<Node> = getManagedChildren()
        val sum = sum(mchildren, Node::prefHeight, width)
        return sum + (mchildren.size - 1) * spacing + insets.top + insets.bottom
    }

    override fun layoutChildren() {
        val x = insets.left
        var y = insets.top
        val w = width - insets.left - insets.right

        getManagedChildren<Node>().forEach {
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
                return form.spacingProperty;
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


    class Column(var stretch: Double = 1.0) {
        var prefWidth: Double = 0.0
        var minWidth: Double = 0.0
        var width: Double = 0.0
    }

}