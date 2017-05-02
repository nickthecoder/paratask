package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import uk.co.nickthecoder.paratask.gui.field.MultipleField
import uk.co.nickthecoder.paratask.gui.field.ParameterField

class MultipleParameter<T>(
        val prototype: ValueParameter<T>,
        name: String,
        label: String,
        description: String,
        value: MutableList<ParameterValue<T>>,
        val allowInsert: Boolean = false,
        val minItems: Int = 0,
        val maxItems: Int = Int.MAX_VALUE)

    : ValueParameter<MutableList<ParameterValue<T>>>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = true),
        WrappableField {

    override fun isStretchy() = true

    override fun parameterValue(values: Values) = super.parameterValue(values) as MultipleValue<T>

    fun list(values: Values): List<T> {
        val mvalue = parameterValue(values)
        return mvalue.list()
    }

    override fun errorMessage(v: MutableList<ParameterValue<T>>?): String? {

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
        v.forEach { singleParameterValue ->

            prototype.errorMessage(singleParameterValue.value)?.let { return "Item #${index + 1} : ${it}" }
            index++
        }

        return null
    }

    override fun createValue(): MultipleValue<T> {
        val result = MultipleValue<T>(this)

        value.forEach { item ->
            result.addItem(item.value)
        }
        return result
    }

    override fun copyValue(source: Values): MultipleValue<T> {
        val copy = MultipleValue<T>(this)
        val from = parameterValue(source)

        val fromList: MutableList<ParameterValue<T>> = from.value
        val copyList: MutableList<ParameterValue<T>> = copy.value

        fromList.forEach { item ->
            copyList.add(item)
        }

        return copy
    }

    override fun createField(values: Values) = MultipleField(this, parameterValue(values))

    override fun wrap(parameterField: ParameterField): Node {
        val titledPane = TitledPane(label, parameterField)
        titledPane.setCollapsible(false)
        return titledPane
    }

}
