package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter

/**
 * Parameters, which can hold a value.
 */
interface ValueParameter<T>
    : Parameter {

    val converter: StringConverter<T>

    var value: T

    var stringValue: String
        get() = converter.toString(value)
        set(v: String) {
            value = converter.fromString(v)
        }

    override fun errorMessage(): String? = errorMessage(value)

    fun errorMessage(v: T?): String?
}
