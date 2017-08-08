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

class ParameterListeners {

    private val listeners = mutableListOf<ParameterListener>()

    val size: Int
        get() = listeners.size

    fun add(listener: ParameterListener) {
        listeners.add(listener)
    }

    fun remove(listener: ParameterListener) {
        listeners.remove(listener)
    }

    // LATER. Many ParameterEvent will be created, so it may be worth while creating a pool of reusable
    // ParameterEvents, rather than creating a new one every time.

    fun fireValueChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.VALUE)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireStructureChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.STRUCTURAL)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireInnerParameterChanged(parameter: Parameter, innerParameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.INNER, innerParameter)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireVisibilityChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.VISIBILITY)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun fireEnabledChanged(parameter: Parameter) {
        val event = ParameterEvent(parameter, ParameterEventType.ENABLED)
        listeners.forEach {
            it.parameterChanged(event)
        }
    }

    fun count(): Int = listeners.size
}