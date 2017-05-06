package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.ParameterException

abstract class NullableValueParameter<T>(
        name: String,
        label: String,
        description: String,
        value: T,
        required: Boolean)

    : ValueParameter<T?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    /**
     * A conienince method for required parameters, which avoids putting !! in you code.
     */
    fun requiredValueX(): T =
            if (!required) {
                throw ParameterException(this, "Not a required parameter")
            } else {
                value!!
            }

}