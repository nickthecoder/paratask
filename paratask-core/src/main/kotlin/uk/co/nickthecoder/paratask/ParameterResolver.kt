/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
