package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.field.GroupParametersForm
import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.gui.field.WrappableField
import uk.co.nickthecoder.paratask.util.uncamel

open class GroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "",
        val collapsable: Boolean = true,
        val expanded: Boolean = true)

    : AbstractParameter(name, description = description, label = label),
        WrappableField, ParentParameter {

    override val children = mutableListOf<Parameter>()

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

    /**
     * Creates a GroupParametersForm, which contains fields for each of this group's children
     * When used to group parameters it is wrapped in a box, but when used as the root,
     * it is not wrapped in a box.
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
}
