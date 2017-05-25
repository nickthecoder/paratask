package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.ValueParameter

/**
 * Used to resolve parameter values, for example, a Tool that has a directory may resolve FileParameters, such that
 * "." is the tool's directory.
 */

interface ParameterResolver {

    fun resolve( parameter : ValueParameter<*>)
}
