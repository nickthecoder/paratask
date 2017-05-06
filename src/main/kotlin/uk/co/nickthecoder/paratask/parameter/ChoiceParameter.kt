package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.field.ChoiceField
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

open class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T,
        required: Boolean = true)

    : ValueParameter<T>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {


    // TODO I think we need two converters, one for the GUI (which uses the labels)
    // and another for the command line, which uses the keys.
    override val converter = object : StringConverter<T>() {

        override fun fromString(label: String): T {
            return labelToValueMap.get(label)!!
        }

        override fun toString(obj: T?): String {
            val lab = valueToLabelMap.get(obj)
            return if (lab == null) "<unknown>" else label
        }

    }

    fun choice(key: String, value: T?, label: String = key.uncamel()): ChoiceParameter<T> {
        addChoice(key, value, label)
        return this
    }

    override fun isStretchy() = false

    override fun createField(): ChoiceField<T> = ChoiceField<T>(this)

    override fun toString(): String = "Choice" + super.toString()

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
}

inline fun <reified T : Enum<T>> ChoiceParameter<T>.enumChoices(): ChoiceParameter<T> {
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) item.label else item.name
        this.choice(key = item.name, value = item, label = label)
    }
    return this
}
