package uk.co.nickthecoder.paratask.parameter

/**
 * The base class for all Parameters, which can hold a value.
 */
abstract class ValueParameter<T>(
        name: String,
        label: String,
        description: String,
        val value: T,
        var required: Boolean = false)

    : AbstractParameter(name, label = label, description = description) {

    open fun parameterValue(values: Values) : ParameterValue<T> = values.get(name) as ParameterValue<T>

    abstract fun createValue(): ParameterValue<T>

    abstract fun copyValue(source: Values): ParameterValue<T>

    fun value(values: Values): T = parameterValue(values).value

    fun set(values: Values, v: T) {
        val parameterValue: ParameterValue<T> = parameterValue(values)
        parameterValue.value = v
    }

    override fun errorMessage(values: Values): String? = errorMessage(value(values))

    open fun errorMessage(v: T?): String? = if (v == null && required) "Required" else null

    fun multiple(allowInsert: Boolean = false): MultipleParameter<T> {
        val singleParameterValue = createValue()
        singleParameterValue.value = value;

        return MultipleParameter(
                this, name = name,
                label = label,
                description = description,
                value = mutableListOf<ParameterValue<T>>(singleParameterValue),
                allowInsert = allowInsert)
    }

}
