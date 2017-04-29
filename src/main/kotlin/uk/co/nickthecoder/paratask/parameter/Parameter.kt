package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.util.Labelled

interface Parameter : Labelled {

    val name: String

    val description: String

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createField(values: Values): ParameterField

}