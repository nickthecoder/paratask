package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.ParameterField
import uk.co.nickthecoder.paratask.gui.ParametersForm

interface Parameter {

    val name: String

    fun createField(values: Values): ParameterField

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createValue(): Value<*>

}