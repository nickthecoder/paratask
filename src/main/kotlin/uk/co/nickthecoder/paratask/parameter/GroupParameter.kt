package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.field.GroupParametersForm
import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.gui.field.WrappableField
import uk.co.nickthecoder.paratask.util.uncamel

class GroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "",
        val collapsable: Boolean = true,
        val expanded: Boolean = true,
        val taskD: TaskDescription? = null)

    : AbstractParameter(name, description = description, label = label),
        WrappableField, ParentParameter {

    override val children = mutableListOf<Parameter>()

    override fun findRoot(): GroupParameter? {
        return if (isRoot()) {
            this
        } else {
            super<AbstractParameter>.findRoot()
        }
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
            parameterListeners.fireInnerParameterChanged(this@GroupParameter, event.parameter)
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

    fun valueParameters(): List<ValueParameter<*>> {
        val result = mutableListOf<ValueParameter<*>>()

        fun addAll(group: GroupParameter) {
            group.children.forEach { child ->
                if (child is ValueParameter<*>) {
                    result.add(child)
                } else if (child is GroupParameter) {
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
    override fun createField(): GroupParametersForm {
        val result = GroupParametersForm(this)
        result.buildContent()
        return result
    }

    fun isRoot(): Boolean = parent == null

    override fun wrap(parameterField: ParameterField): Node {
        if (isRoot()) {
            return parameterField
        } else {
            val titledPane = TitledPane(label, parameterField)
            titledPane.setCollapsible(collapsable)
            if (collapsable) {
                titledPane.setExpanded(expanded)
            }
            return titledPane
        }
    }

    override fun errorMessage(): String? = null

    override fun isStretchy(): Boolean = true

    override fun findTaskD(): TaskDescription? {
        parent?.let {
            return it.findTaskD()
        }
        return taskD
    }

}
