package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractParameter(
        override val name: String,
        override val label: String = name.uncamel(),
        override val description: String = "")
    : Parameter {

    override fun toString() = "Parmaeter ${name}"
}
