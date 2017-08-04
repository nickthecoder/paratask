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
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractGroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "")

    : AbstractParameter(name, description = description, label = label),
        ParentParameter {

    override val children = mutableListOf<Parameter>()

    fun descendants(): List<Parameter> {
        val result = mutableListOf<Parameter>()

        fun addAll(group: AbstractGroupParameter) {
            group.children.forEach { child ->
                result.add(child)
                if (child is AbstractGroupParameter) {
                    addAll(child)
                }
            }
        }

        addAll(this)
        return result
    }

    fun add(child: Parameter) {
        if (child === this) {
            throw ParameterException(this, "Cannot add to itself")
        }
        if (child.parent != null) {
            throw ParameterException(child, "Already in a group")
        }
        if (find(child.name) != null) {
            throw ParameterException(this, "Parameter with name '${child.name}' is already in this GroupParameter")
        }

        if (child is GroupParameter) {
            child.descendants().forEach { ancestor ->
                if (find(ancestor.name) != null) {
                    throw ParameterException(this,
                            "Duplicate parameter name '${ancestor.name}' in GroupParameter '${child.name}'")
                }
            }

        }

        // Check that the child isn't already an ancestor
        findRoot()?.let { root ->
            if (root.find(child.name) != null) {
                throw ParameterException(child, "Parameter already exists in the tree")
            }
        }

        children.add(child)
        child.parent = this

        child.parameterListeners.add(innerListener)
    }

    /**
     * Forwards change events for the children to the group's listeners
     */
    val innerListener = object : ParameterListener {
        override fun parameterChanged(event: ParameterEvent) {
            parameterListeners.fireInnerParameterChanged(this@AbstractGroupParameter, event.parameter)
        }
    }

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { add(it) }
    }

    fun remove(child: Parameter) {
        children.remove(child)
    }

    fun find(name: String): Parameter? {
        children.forEach { child ->
            if (child.name == name) {
                return child
            }
            if (child is BooleanParameter && child.oppositeName == name) {
                return child
            }
            if (child is GroupParameter) {
                child.find(name)?.let { return it }
            }
        }

        return null
    }

    override fun isStretchy(): Boolean = true

    protected fun copyChildren(copy: AbstractGroupParameter) {
        children.forEach { child ->
            copy.addParameters(child.copy())
        }
    }
}