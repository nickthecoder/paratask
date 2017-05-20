package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.Parameter

class ParameterException(val parameter: Parameter, override val message: String)
    : RuntimeException(message) {

    override fun toString(): String {
        return "ParameterException '${parameter.name}' : $message"
    }
}