package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.gui.ParameterField

class GroupParameter(name: String) : AbstractParameter(name), Iterable<Parameter> {

    private val children = mutableListOf<Parameter>()

    fun add(child: Parameter) {
        children.add(child)
    }

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { add(it) }
    }

    fun remove(child: Parameter) {
        children.remove(child)
    }

    override fun iterator(): Iterator<Parameter> {
        return children.iterator()
    }

    fun find(name: String): Parameter? {
        children.forEach { child ->
            if (child.name == name) {
                return child
            }
            if (child is GroupParameter) {
                child.find(name)?.let { return it }
            }
        }

        return null
    }

    fun children(): List<Parameter> {
        return children
    }

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

    /**
     * Creates a box with the name of the group at the top, and all of the child parameters' Nodes
     * inside the box.
     * Note that {@link TaskPrompter} does NOT use this on the {@link Task}'s root.
     */
    override fun createField(values: Values): ParameterField {
        // TODO LATER Implement GroupParameter.createField()
        throw Exception("Not implemented")
    }

    override fun errorMessage(values: Values): String? = null

    fun check(values: Values) {
        descendants().forEach { parameter ->
            parameter.errorMessage(values)
        }
    }

    override fun isStretchy(): Boolean = true

    override fun createValue(): Values {
        val values = Values(this)
        children().forEach { parameter ->

            val value = parameter.createValue()
            values.put(parameter.name, value)

            if (parameter is GroupParameter) {
                (value as Values).values.forEach { (name, value) ->
                    values.put(name, value)
                }
            }
        }
        return values
    }

    fun copyValues(source: Values) = copyValue(source)

    override fun copyValue(source: Values): Values {
        val copy = Values(this)

        children().forEach { parameter ->
            val copySingleValue = parameter.copyValue(source)

            if (parameter is GroupParameter) {
                (copySingleValue as Values).values.forEach { (subName, subValue) ->
                    copy.put(subName, subValue)
                }
            }

            copy.put(parameter.name, copySingleValue)
        }
        return copy
    }
}