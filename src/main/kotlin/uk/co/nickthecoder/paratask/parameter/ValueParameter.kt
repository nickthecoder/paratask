package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import kotlin.reflect.KProperty

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
