package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.ValueParameter

/**
 * Used to resolve parameter values, for example, a Tool that has a directory may resolve FileParameters, such that
 * "." is the tool's directory.
 */

interface ParameterResolver {

    /**
     * Sets the parameter's value, for example a FileParameter may replave "." and "~" with resolved values.
     */
    fun resolve(parameter: ValueParameter<*>)

    /**
     * If a parameter needs to resolve its value before validating it, then this will resolve the value with setting
     * the parameter's value.
     */
    fun resolveValue(parameter: ValueParameter<*>, value: Any?): Any? = value
}
