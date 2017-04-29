package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import uk.co.nickthecoder.paratask.gui.MultipleField
import uk.co.nickthecoder.paratask.gui.ParameterField

class MultipleParameter<T>(
        val prototype: ValueParameter<T>,
        name: String,
        label: String,
        description: String,
        value: MutableList<T>,
        val allowInsert: Boolean = false)

    : ValueParameter<MutableList<T>>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = true),
        WrappableField {

    override fun isStretchy() = true

    override fun parameterValue(values: Values) = super.parameterValue(values) as MultipleValue<T>

    fun values(values: Values): List<T> {
        val mvalue = parameterValue( values )
        return mvalue.values()
    }

    override fun createValue(): MultipleValue<T> {
        val result = MultipleValue<T>(this)

        value.forEach { item ->
            result.addItem(item)
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

    override fun createField(values: Values) = MultipleField(this, values)

    override fun wrap(parameterField: ParameterField): Node {
        val titledPane = TitledPane(label, parameterField)
        titledPane.setCollapsible(false)
        return titledPane
    }

}
