package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import uk.co.nickthecoder.paratask.gui.MultipleField

class MultipleParameter<T>(
        val prototype: ValueParameter<T>,
        name: String,
        label: String,
        description: String,
        val value: T?)

    : ValueParameter<List<T>>(
        name = name,
        label = label,
        description = description,
        required = true) {

    override fun isStretchy() = true

    override fun getValue(values: Values) = super.getValue(values) as MultipleValue<T>

    override fun createValue(): MultipleValue<T> {
        val result = MultipleValue<T>(this)

        if (value != null) {
            result.addItem(value)
        }
        return result
    }

    override fun copyValue(source: Values): MultipleValue<T> {
        val copy = MultipleValue<T>(this)
        val from = getValue(source)

        val fromList: MutableList<Value<T>> = from.value
        val copyList: MutableList<Value<T>> = copy.value

        fromList.forEach { item ->
            copyList.add(item)
        }

        return copy
    }

    override fun createField(values: Values): Node = MultipleField(this, values)
}
