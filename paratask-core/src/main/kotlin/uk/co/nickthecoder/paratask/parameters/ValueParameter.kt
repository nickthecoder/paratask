/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.StringProperty
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
        set(v) {
            value = converter.fromString(v)
        }

    val expressionProperty: StringProperty

    var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    fun saveValue(): Boolean = true

    /**
     * Set the value based on an evaluated expression, and therefore the type T isn't known by the
     * caller.
     */
    fun evaluated(v: Any?) {
        val parent = parent
        if (v is Iterable<*> && parent is MultipleParameter<*, *>) {
            parent.evaluateMultiple(this, v)
        } else {
            coerce(v)
        }
        expression = null
    }

    /**
     * Used when evaluating expressions, the value is coerced to fit the required data type. For example,
     * DoubleParameter can coerce all numbers types to doubles. However, the default behaviour is to
     * convert the value to a string, and set the parameter via stringValue. Therefore DoubleParameter doesn't NEED
     * to override this method, but it would be more efficient if it did.
     */
    fun coerce(v: Any?) {
        stringValue = v.toString()
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

    override fun copy(): ValueParameter<T>

    fun copyBounded(): ValueParameter<T> {
        val copy = copy()
        copy.value = value
        copy.listen {
            this.value = copy.value
        }
        this.listen {
            copy.value = this.value
        }

        return copy
    }
}
