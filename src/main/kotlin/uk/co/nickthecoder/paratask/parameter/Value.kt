package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty

interface ParameterValue<T> {

    val parameter: Parameter

    val valueListeners: ValueListeners

    var value: T

    var stringValue: String

    fun errorMessage(): String?

}