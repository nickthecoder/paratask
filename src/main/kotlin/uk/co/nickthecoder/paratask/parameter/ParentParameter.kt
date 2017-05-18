package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.ParameterException

interface ParentParameter : Parameter {

    val children: List<Parameter>

    fun check() {
        for (parameter in children) {
            if (parameter is ParentParameter) {
                parameter.check()
            } else {
                if (parameter is ValueParameter<*> && parameter.expression != null) {
                    if ( parameter.expression == "") {
                        throw ParameterException( parameter, "Expression is required")
                    }
                } else {
                    val error = parameter.errorMessage()
                    if (error != null) {
                        throw ParameterException(parameter, error)
                    }
                }
            }
        }
    }

}