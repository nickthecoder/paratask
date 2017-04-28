package uk.co.nickthecoder.paratask.parameter

class MultipleValue<T>(
        override val parameter: MultipleParameter<T>)

    : AbstractValue<MutableList<Value<T>>>(mutableListOf<Value<T>>()) {

    override fun fromString(str: String): MutableList<Value<T>> {
        val result = mutableListOf<Value<T>>()
        // TODO Create from string
        return result
    }

    override fun toString(obj: MutableList<Value<T>>): String {
        return ""
        // TODO Create string
    }

    override fun errorMessage(v: MutableList<Value<T>>): String? {
        // TODO Check if the number of items given is in the allowed range
        return null
    }

    fun addItem(item: T): Value<T> {
        val singleValue = newValue()
        singleValue.value = item

        return singleValue
    }

    fun newValue(): Value<T> {
        val singleValue = parameter.prototype.createValue() as Value<T>
        addValue(singleValue)

        return singleValue
    }

    fun addValue(singleValue: Value<T>) {
        value.add(singleValue)
        // TODO fire a change event
    }

    fun removeValue(singleValue: Value<T>) {
        value.remove(singleValue)
        // TODO fire a change event
    }

    override fun toString(): String = "Multiple" + super.toString()
}
