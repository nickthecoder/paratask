package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.Form

class GroupParameter(name: String) : AbstractParameter(name), Iterable<Parameter>, ParameterListener {
    private val children = mutableListOf<Parameter>()

    fun add(child: Parameter) {
        children.add(child)
        child.addListener(this)
    }

    fun add(vararg children: Parameter) {
        children.forEach { add(it) }
    }

    fun remove(child: Parameter) {
        children.remove(child)
        child.removeListener(this)
    }

    override fun iterator(): Iterator<Parameter> {
        return children.iterator()
    }

    fun find(name: String): Parameter? {
        return children.find { it.name == name }
    }

    override fun parameterChanged(parameter: Parameter) {
        fireChanged(parameter)
    }

    /**
     * Creates a box with the name of the group at the top, and all of the child parameters' Nodes
     * inside the box.
     * Note that {@link TaskPrompter} does NOT use this on the {@link Task}'s root.
     */
    override fun createField(): Field {
        // TODO Implement correctly
        return Field(name, Label())
    }

    override fun isStretchy(): Boolean = true
}