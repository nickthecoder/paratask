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
import uk.co.nickthecoder.paratask.parameters.fields.*
import uk.co.nickthecoder.paratask.util.uncamel

abstract class GroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "")

    : AbstractParameter(name, description = description, label = label),
        ParentParameter {

    var fieldFactory: (GroupParameter) -> ParameterField = {
        GroupField(this).build()
    }

    override val children = mutableListOf<Parameter>()

    abstract fun saveChildren(): Boolean


    fun descendants(): List<Parameter> {
        val result = mutableListOf<Parameter>()

        fun addAll(group: GroupParameter) {
            group.children.forEach { child ->
                result.add(child)
                if (child is GroupParameter) {
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

        if (child is SimpleGroupParameter) {
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
        parameterListeners.fireStructureChanged(this, child)
    }

    fun remove(child: Parameter) {
        children.remove(child)
        child.parent = null
        parameterListeners.fireStructureChanged(this, child)
    }

    fun find(name: String): Parameter? {
        return findWithoutAliases(name) ?: findWithAliases(name)
    }

    fun findWithoutAliases(name: String): Parameter? {
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

    fun findWithAliases(name: String): Parameter? {
        children.forEach { child ->
            if (child.aliases.contains(name)) {
                return child
            }
            if (child is GroupParameter) {
                child.findWithAliases(name)?.let { return it }
            }
        }

        return null
    }

    override fun isStretchy(): Boolean = true

    override fun createField(): ParameterField = fieldFactory(this)

    protected fun copyAbstractAttributes(copy: GroupParameter) {
        copy.fieldFactory = fieldFactory
        copyChildren(copy)
    }

    protected fun copyChildren(copy: GroupParameter) {
        children.forEach { child ->
            copy.addParameters(child.copy())
        }
    }
}


inline fun <reified T : GroupParameter> T.addParameters(vararg parameters: Parameter): T {
    parameters.forEach { add(it) }
    return this
}

inline fun <reified T : GroupParameter> T.asBox(): T {
    fieldFactory = {
        GroupField(this, isBoxed = true).build()
    }
    return this
}

inline fun <reified T : GroupParameter> T.asPlain(): T {
    fieldFactory = {
        GroupField(this, isBoxed = false).build()
    }
    return this
}

inline fun <reified T : GroupParameter> T.asHorizontal(labelPosition: LabelPosition = LabelPosition.LEFT, isBoxed: Boolean = false): T {
    fieldFactory = {
        HorizontalGroupField(this, labelPosition = labelPosition, isBoxed = isBoxed).build()
    }
    return this
}

inline fun <reified T : GroupParameter> T.asVertical(labelPosition: LabelPosition = LabelPosition.LEFT, isBoxed: Boolean = false): T {
    fieldFactory = {
        VerticalGroupField(this, labelPosition = labelPosition, isBoxed = isBoxed).build()
    }
    return this
}

inline fun <reified T : GroupParameter> T.asGrid(labelPosition: LabelPosition = LabelPosition.LEFT, columns: Int = children.size, isBoxed: Boolean = false): T {
    fieldFactory = {
        GridGroupField(this, labelPosition = labelPosition, columns = columns, isBoxed = isBoxed).build()
    }
    return this
}
