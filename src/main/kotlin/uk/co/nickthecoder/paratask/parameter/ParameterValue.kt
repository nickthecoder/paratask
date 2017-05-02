package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty

interface ParameterValue<T> {

    val parameter: Parameter

    val valueListeners: ValueListeners

    var value: T

    var stringValue: String

    /**
     * Default implementation is a SHALLOW copy. Override this for complex types such as MultipleValues
     */
    fun copyValue(): T = value

    fun copy(): ParameterValue<T>

    fun copyValueFrom(source: ParameterValue<*>) {
        value = (source as ParameterValue<T>).copyValue()
    }
}
