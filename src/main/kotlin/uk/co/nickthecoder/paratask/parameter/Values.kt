package uk.co.nickthecoder.paratask.parameter

class Values(override val parameter: GroupParameter) : ParameterValue<Unit>, ValueListener {

    override val valueListeners = ValueListeners()

    override var value
        get() = Unit
        set(u: Unit) {}

    override var stringValue
        get() = ""
        set(U: String) {}

    val parameterValues = mutableMapOf<String, ParameterValue<*>>()

    fun get(name: String) = parameterValues.get(name)

    fun put(name: String, value: ParameterValue<*>) {
        parameterValues.put(name, value)
        value.valueListeners.add(this)
    }

    override fun errorMessage(): String? {
        return parameter.errorMessage(this)
    }

    override fun valueChanged(parameterValue: ParameterValue<*>) {
        valueListeners.fireChanged(parameterValue)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Values name '${parameter.name}' =\n")
        parameterValues.forEach { (name, parameterValue) ->
            builder.append("'${name}' = ${parameterValue}")
            builder.append("\n")
        }
        return builder.toString()
    }
}