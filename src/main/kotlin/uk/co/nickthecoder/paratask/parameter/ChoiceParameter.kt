package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.field.ChoiceField
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel


open class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T,
        required: Boolean = true)

    : NullableValueParameter<T>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {


    override val converter = object : StringConverter<T?>() {

        override fun fromString(label: String): T? {
            return keyToValueMap.get(label)
        }

        override fun toString(obj: T?): String {
            val lab = valueToKeyMap.get(obj)
            return if (lab == null) "<unknown>" else lab
        }
    }

    fun choice(key: String, value: T?, label: String = key.uncamel()): ChoiceParameter<T> {
        addChoice(key, value, label)
        return this
    }

    override fun isStretchy() = false

    override fun createField(): ChoiceField<T> = ChoiceField<T>(this)

    override fun toString(): String = "Choice" + super.toString()

    private val valueToKeyMap = LinkedHashMap<T?, String>()
    private val keyToValueMap = LinkedHashMap<String, T?>()
    private val valueToLabelMap = LinkedHashMap<T?, String>()
    private val labelToValueMap = LinkedHashMap<String, T?>()

    fun choiceValues(): Collection<T?> {
        return keyToValueMap.values
    }

    fun getLabelForValue(value: T?): String? {
        return valueToLabelMap.get(value)
    }

    fun getValueForLabel(label: String?): T? {
        return labelToValueMap.get(label)
    }

    fun addChoice(key: String, value: T?, label: String = key.uncamel()) {
        keyToValueMap.put(key, value)
        valueToKeyMap.put(value, key)
        valueToLabelMap.put(value, label)
        labelToValueMap.put(label, value)
    }

    fun removeKey(key: String) {
        val value = keyToValueMap.get(key)
        val label = valueToLabelMap.get(value)

        keyToValueMap.remove(key)
        valueToKeyMap.remove(value)
        valueToLabelMap.remove(value)
        labelToValueMap.remove(label)
    }

    fun clearChoices() {
        keyToValueMap.clear()
        valueToLabelMap.clear()
        labelToValueMap.clear()
    }
}

inline fun <reified T : Enum<T>> ChoiceParameter<T>.enumChoices(): ChoiceParameter<T> {
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) item.label else item.name
        this.choice(key = item.name, value = item, label = label)
    }
    return this
}
