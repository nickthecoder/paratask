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

abstract class AbstractParameter(
        override val name: String,
        override val label: String,
        override val description: String,
        override val hint: String = "",
        override val isBoxed: Boolean = false)

    : Parameter {

    override var isExpanded = true

    override val aliases = mutableListOf<String>()

    override val parameterListeners = ParameterListeners()

    override fun listen(listener: (event: ParameterEvent) -> Unit) {
        parameterListeners.add(object : ParameterListener {
            override fun parameterChanged(event: ParameterEvent) {
                listener(event)
            }
        }, false)
    }

    override var hidden: Boolean = false
        set(v) {
            val old = field
            field = v
            if (old != v) {
                parameterListeners.fireVisibilityChanged(this)
            }
        }

    override var enabled: Boolean = true
        set(v) {
            val old = field
            field = v
            if (old != v) {
                parameterListeners.fireEnabledChanged(this)
            }
        }

    override var parent: Parameter? = null


    override fun toString() = "Parameter name='$name'"
}

inline fun <reified T : AbstractParameter> T.addAliases(vararg alias: String): T {
    aliases.addAll(alias)
    return this
}
