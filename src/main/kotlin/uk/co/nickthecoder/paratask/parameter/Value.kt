package uk.co.nickthecoder.paratask.parameter

interface Value<T> {

    val valueListeners: ValueListeners

    val parameter: Parameter

    var value: T

    var stringValue: String

    fun errorMessage(): String?

}