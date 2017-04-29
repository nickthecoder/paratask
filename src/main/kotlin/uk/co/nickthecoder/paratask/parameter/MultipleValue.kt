package uk.co.nickthecoder.paratask.parameter

class MultipleValue<T>(
        override val parameter: MultipleParameter<T>)

    : AbstractValue<MutableList<ParameterValue<T>>>(mutableListOf<ParameterValue<T>>()) {

    fun values(): List<T> {
        val result = mutableListOf<T>()

        value.forEach {
            result.add(it.value)
        }
        return result
    }

    override fun fromString(str: String): MutableList<ParameterValue<T>> {
        val result = mutableListOf<ParameterValue<T>>()
        // TODO Create from string
        return result
    }

    override fun toString(obj: MutableList<ParameterValue<T>>): String {
        return ""
        // TODO Create string
    }

    override fun errorMessage(v: MutableList<ParameterValue<T>>): String? {
        // TODO Check if the number of items given is in the allowed range
        return null
    }

    fun addItem(item: T): ParameterValue<T> {
        val singleValue = newValue()
        singleValue.value = item

        return singleValue
    }

    fun newValue(index: Int = value.size): ParameterValue<T> {
        val singleValue = parameter.prototype.createValue() as ParameterValue<T>
        addValue(singleValue, index)

        return singleValue
    }

    fun addValue(singleValue: ParameterValue<T>, index: Int = value.size) {
        value.add(index, singleValue)
        valueListeners.fireChanged(this)
    }

    fun removeValue(singleValue: ParameterValue<T>) {
        value.remove(singleValue)
        valueListeners.fireChanged(this)
    }

    fun removeAt(index: Int) {
        value.removeAt(index)
        valueListeners.fireChanged(this)
    }

    override fun toString(): String = "Multiple" + super.toString()
}
