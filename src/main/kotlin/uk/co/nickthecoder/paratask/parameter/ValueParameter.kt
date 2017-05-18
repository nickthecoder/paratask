package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.util.StringConverter
import kotlin.reflect.KProperty

/**
 * Parameters, which can hold a value.
 */
interface ValueParameter<T>
    : Parameter {

    val converter: StringConverter<T>

    var value : T

    var stringValue: String
        get() = converter.toString(value)
        set(v: String) {
            value = converter.fromString(v)
        }

    val expressionProperty: StringProperty

    var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    /**
     * Set the value based on an evaluated expression, and therefore the type T isn't known by the
     * caller.
     * Throws an exception if the type is not correct.
     */
    fun evaluated(v: Any?) {
        @Suppress("UNCHECKED_CAST")
        value = v as T
    }

    override fun errorMessage(): String? = errorMessage(value)

    fun errorMessage(v: T?): String?

    /**
     * For delegation :
     *
     * val fooP = IntParameter( "foo" )
     *
     * val foo by fooP
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    /**
     * For delegation :
     *
     * val fooP = IntParameter( "foo" )
     *
     * var foo by fooP
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
