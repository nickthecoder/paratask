package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractParameter(
        override val name: String,
        override val label: String,
        override val description: String)
    : Parameter {

    override val parameterListeners = ParameterListeners()

    override fun listen(listener: (event: ParameterEvent) -> Unit) {
        parameterListeners.add(object : ParameterListener {
            override fun parameterChanged(event: ParameterEvent) {
                listener(event)
            }
        })
    }

    override var parent: GroupParameter? = null

    override fun toString() = "Parameter ${name}"
}
