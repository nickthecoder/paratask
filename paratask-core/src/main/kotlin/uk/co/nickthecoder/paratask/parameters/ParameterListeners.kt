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

import uk.co.nickthecoder.paratask.util.Listeners

class ParameterListeners : Listeners<ParameterListener>() {

    fun fireValueChanged(parameter: Parameter, oldValue: Any?) {
        val event = ParameterEvent(parameter, ParameterEventType.VALUE, oldValue)
        forEach {
            it.parameterChanged(event)
        }
        parameter.parent?.let { parent ->
            parent.parameterListeners.fireInnerParameterChanged(parent, parameter, oldValue)
        }
    }

    fun fireStructureChanged(parameter: Parameter, child: Parameter? = null) {
        val event = ParameterEvent(parameter, ParameterEventType.STRUCTURAL, child)
        forEach {
            it.parameterChanged(event)
        }
        fireValueChanged(parameter, null)
    }

    fun fireInnerParameterChanged(parameter: Parameter, innerParameter: Parameter, oldValue: Any?) {
        val event = ParameterEvent(parameter, ParameterEventType.INNER, oldValue, innerParameter)
        forEach {
            it.parameterChanged(event)
        }
        parameter.parent?.let { parent ->
            parent.parameterListeners.fireInnerParameterChanged(parent, innerParameter, oldValue)
        }
    }

    fun fireVisibilityChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.VISIBILITY, null)
        forEach {
            it.parameterChanged(event)
        }
    }

    fun fireEnabledChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.ENABLED, null)
        forEach {
            it.parameterChanged(event)
        }
    }
}
