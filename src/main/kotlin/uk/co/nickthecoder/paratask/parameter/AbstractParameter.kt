package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractParameter(
        override val name: String,
        override val label: String,
        override val description: String)
    : Parameter {

    val parameterListeners = ParameterListeners()

    override var parent: GroupParameter? = null

    override fun toString() = "Parameter ${name}"
}
