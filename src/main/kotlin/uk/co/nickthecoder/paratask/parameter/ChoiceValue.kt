package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.util.uncamel

class ChoiceValue<T>(override val parameter: ChoiceParameter<T>, initialValue: T?)
    : StringConverter<T?>(), Value<T?> {

    override val valueListeners = ValueListeners()

    var property = object : SimpleObjectProperty<T>() {
        override fun set(v: T) {
            val changed = v != get()
            if (changed) {
                valueListeners.fireChanged(this@ChoiceValue)
                super.set(v)
            }
        }
    }

    override var value: T?
        set(v: T?) {
            property.set(v)
        }
        get() = property.get()

    init {
        value = initialValue
    }

    val keyToValueMap = LinkedHashMap<String, T?>()
    val valueToLabelMap = LinkedHashMap<T?, String>()
    val labelToValueMap = LinkedHashMap<String, T?>()

    fun addChoice(key: String, value: T?, label: String = key.uncamel()) {
        keyToValueMap.put(key, value)
        valueToLabelMap.put(value, label)
        labelToValueMap.put(label, value)
    }

    fun removeKey(key: String) {
        val value = keyToValueMap.get(key)
        val label = valueToLabelMap.get(value)

        keyToValueMap.remove(key)
        valueToLabelMap.remove(value)
        labelToValueMap.remove(label)
    }

    fun clearChoices() {
        keyToValueMap.clear()
        valueToLabelMap.clear()
        labelToValueMap.clear()
    }

    override var stringValue: String
        get() = toString(value)
        set(v: String) {
            value = fromString(v)
        }

    override fun fromString(label: String): T? {
        return labelToValueMap.get(label)
    }

    override fun toString(obj: T?): String {
        val label = valueToLabelMap.get(obj)
        return if (label == null) "<unknown>" else label
    }

    override fun errorMessage() = errorMessage(value)

    fun errorMessage(v: T?) = parameter.errorMessage(v)

    fun copy(): ChoiceValue<T> {
        val result = ChoiceValue<T>(parameter, value)
        keyToValueMap.forEach { (key, value) ->
            val label = valueToLabelMap.get(value) ?: ""
            result.addChoice(key, value, label)
        }
        return result
    }

    override fun toString(): String = "ChoiceValue name '${parameter.name}' = ${value}"

}