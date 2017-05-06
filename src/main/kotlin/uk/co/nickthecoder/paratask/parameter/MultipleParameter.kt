package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.field.MultipleField
import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.gui.field.WrappableField

class MultipleParameter<T>(
        val prototype: ValueParameter<T>,
        name: String,
        label: String,
        description: String,
        value: MutableList<T>,
        val allowInsert: Boolean = false,
        val minItems: Int = 0,
        val maxItems: Int = Int.MAX_VALUE)

    : ValueParameter<MutableList<T>>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = true),
        WrappableField {

    // TODO Prevent the list from being altered from outside.
    // Declare as a List, and have a private reference to the MutableList?
    override val converter = object : StringConverter<MutableList<T>>() {

        override fun fromString(str: String): MutableList<T>? {
            // TODO Create from string
            return mutableListOf<T>()
        }

        override fun toString(obj: MutableList<T>?): String {
            return ""
            // TODO Create string
        }
    }

    override fun isStretchy() = true

    override fun errorMessage(v: MutableList<T>?): String? {

        if (v == null) {
            return "Expected a list of items"
        }

        if (v.size < minItems) {
            return "Must have at least ${minItems} items"
        }
        if (v.size > maxItems) {
            return "Cannot have more than ${maxItems} items"
        }

        var index = 0
        v.forEach { singleValue ->

            prototype.errorMessage(singleValue)?.let { return "Item #${index + 1} : ${it}" }
            index++
        }

        return null
    }

    override fun createField() = MultipleField(this)

    override fun wrap(parameterField: ParameterField): Node {
        val titledPane = TitledPane(label, parameterField)
        titledPane.setCollapsible(false)
        return titledPane
    }


    fun clear() {
        value.clear()
        valueListeners.fireChanged(this)
    }

    fun addValue(item: T, index: Int = value.size) {
        value.add(index, item)
        valueListeners.fireChanged(this)
    }

    fun removeValue(item: T) {
        value.remove(item)
        valueListeners.fireChanged(this)
    }

    fun removeAt(index: Int) {
        value.removeAt(index)
        valueListeners.fireChanged(this)
    }

}
