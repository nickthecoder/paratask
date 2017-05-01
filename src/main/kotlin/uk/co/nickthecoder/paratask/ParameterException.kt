package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.Parameter

class ParameterException(val parameter: Parameter, message: String)
    : RuntimeException(message) {

    override fun toString(): String {
        return "ParameterException '${parameter.name}' : message"
    }
}