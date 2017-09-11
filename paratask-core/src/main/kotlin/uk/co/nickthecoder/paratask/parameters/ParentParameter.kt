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

package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField

interface ParentParameter : Parameter {

    val children: List<Parameter>

    fun check() {
        errorMessage()?.let { throw ParameterException(this, it) }
        children.forEach { checkChild(it) }
    }

    fun checkChild(parameter: Parameter) {
        if (parameter.hidden) {
            return
        }
        if (parameter is ParentParameter) {
            parameter.check()
        } else {
            if (parameter is ValueParameter<*> && parameter.expression != null) {
                if (parameter.expression == "") {
                    throw ParameterException(parameter, "Expression is required")
                }
            } else {
                parameter.errorMessage()?.let { throw ParameterException(parameter, it) }
            }
        }

    }
}