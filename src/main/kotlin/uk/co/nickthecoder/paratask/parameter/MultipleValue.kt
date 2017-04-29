package uk.co.nickthecoder.paratask.parameter

class MultipleValue<T>(
        override val parameter: MultipleParameter<T>)

    : AbstractValue<MutableList<Value<T>>>(mutableListOf<Value<T>>()) {

    fun values(): List<T> {
        val result = mutableListOf<T>()

        value.forEach {
            result.add(it.value)
        }
        return result
    }

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

    fun newValue(index: Int = value.size): Value<T> {
        val singleValue = parameter.prototype.createValue() as Value<T>
        addValue(singleValue, index)

        return singleValue
    }

    fun addValue(singleValue: Value<T>, index: Int = value.size) {
        value.add(index, singleValue)
        valueListeners.fireChanged(this)
    }

    fun removeValue(singleValue: Value<T>) {
        value.remove(singleValue)
        valueListeners.fireChanged(this)
    }

    fun removeAt(index: Int) {
        value.removeAt(index)
        valueListeners.fireChanged(this)
    }

    override fun toString(): String = "Multiple" + super.toString()
}
