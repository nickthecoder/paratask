package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractParameter(
        override val name: String,
        override var label: String = name.uncamel())

        : Parameter {

}
