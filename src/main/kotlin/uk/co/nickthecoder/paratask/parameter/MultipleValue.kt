package uk.co.nickthecoder.paratask.parameter

class MultipleValue<T>(
        override val parameter: MultipleParameter<T>)

    : AbstractValue<MutableList<ParameterValue<T>>>(mutableListOf<ParameterValue<T>>()) {

    fun list(): List<T> {
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

    fun addItem(item: T): ParameterValue<T> {
        val singleValue = newValue()
        singleValue.value = item

        return singleValue
    }

    fun newValue(index: Int = value.size): ParameterValue<T> {
        val singleValue = parameter.prototype.createValue()
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

    override fun errorMessage(v: MutableList<ParameterValue<T>>): String? {
        return parameter.errorMessage(v)
    }

    override fun copy(): MultipleValue<T> {
        val copy = MultipleValue(parameter)
        copy.value = copyValue()
        return copy
    }

    override fun copyValue(): MutableList<ParameterValue<T>> {
        val copyValue = mutableListOf<ParameterValue<T>>()
        value.forEach { singlePV ->
            copyValue.add(singlePV.copy())
        }
        return copyValue
    }

    override fun toString(): String = "Multiple" + super.toString()
}
