package uk.co.nickthecoder.paratask.parameter

class Values() : ValueListener {

    val valueListeners = ValueListeners()

    val parameterValues = mutableMapOf<String, ParameterValue<*>>()

    fun get(name: String) = parameterValues.get(name)

    fun put(name: String, value: ParameterValue<*>) {
        parameterValues.put(name, value)
        value.valueListeners.add(this)
    }

    override fun valueChanged(parameterValue: ParameterValue<*>) {
        valueListeners.fireChanged(parameterValue)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Values ' =\n")
        parameterValues.forEach { (name, parameterValue) ->
            builder.append("'${name}' = ${parameterValue}")
            builder.append("\n")
        }
        return builder.toString()
    }
}