package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.ParameterException

interface ParentParameter : Parameter {

    val children: List<Parameter>

    fun check() {
        for (parameter in children) {
            if (parameter is ParentParameter) {
                parameter.check()
            } else {
                val error = parameter.errorMessage()
                if (error != null) {
                    throw ParameterException(parameter, error)
                }
            }
        }
    }

}