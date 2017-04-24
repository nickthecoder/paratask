package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.ParameterField

interface Parameter {

    val name: String

    val label: String

    fun createField(values: Values): ParameterField

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createValue(): Value<*>

    fun copyValue(source: Values): Value<*>

}