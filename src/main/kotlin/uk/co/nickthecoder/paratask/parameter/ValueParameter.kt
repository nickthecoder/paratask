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

    open fun getParameterValue(values: Values) = values.get(name)

    abstract fun createValue(): ParameterValue<*>

    abstract fun copyValue(source: Values): ParameterValue<*>

    fun value(values: Values): T? = getParameterValue(values)?.value as T?

    fun set(values: Values, v: T) {
        val value: ParameterValue<T>? = getParameterValue(values) as ParameterValue<T>
        value!!.value = v
    }

    override fun errorMessage(values: Values): String? = errorMessage(value(values))

    open fun errorMessage(v: T?): String? = if (v == null && required) "Required" else null

    fun multiple(allowInsert: Boolean = false): MultipleParameter<T> =
            MultipleParameter(
                    this, name = name,
                    label = label,
                    description = description,
                    value = listOf<T>(value),
                    allowInsert = allowInsert)

}
