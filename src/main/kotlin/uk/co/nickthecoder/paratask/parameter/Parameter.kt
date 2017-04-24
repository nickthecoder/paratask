package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.Form

interface Parameter<T : Value<*>> {

    val name: String

    fun createField(values: Values): Field

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createValue(): Value<*>

}