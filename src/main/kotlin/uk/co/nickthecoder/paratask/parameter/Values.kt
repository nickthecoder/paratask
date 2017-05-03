package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.ParameterException

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

    /**
     * We need to copy values from one Tool to another (when duplicating the tool for example,
     * In which case, we only want to copy the values held within each ParameterValue. We do not
     * want to copy the ParameterValues themselves.
     */
    fun copyValuesFrom(source: Values) {
        source.parameterValues.forEach { (name, sourcePV) ->
            val destPV = get(name)
            if (destPV != null) {
                try {
                    destPV.copyValueFrom(sourcePV)
                } catch (e: ParameterException) {
                    // Parameters with invalid vales don't get copied over.
                }
                put(name, destPV)
            }
        }
    }

    fun copy(): Values {
        var copy = Values()
        parameterValues.forEach { (name, sourcePV) ->
            val copyPV = sourcePV.copy()
            copy.put(name, copyPV)
        }
        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (other is Values) {

            if (parameterValues.size != other.parameterValues.size) {
                return false
            }

            parameterValues.forEach { (name, pv) ->
                val otherPV = other.get(name)
                if (otherPV == null) {
                    return false
                }
                if (pv != otherPV) {
                    return false
                }
            }
            return true
        }
        return false
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("BeginValues\n")
        parameterValues.forEach { (name, parameterValue) ->
            builder.append("    '${name}' = ${parameterValue}")
            builder.append("\n")
        }
        builder.append("End Values\n")
        return builder.toString()
    }
}