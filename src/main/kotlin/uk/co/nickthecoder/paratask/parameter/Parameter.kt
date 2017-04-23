package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.Form

interface Parameter {

    val name: String

    fun addListener(l: ParameterListener)

    fun removeListener(l: ParameterListener)

    fun createField(): Field

    fun isStretchy(): Boolean

    fun lock()

    fun unlock()

    fun check()

}