package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

class TaskDescription(
        val name: String = "",
        override val label: String = name.uncamel(),
        val description: String = "") : Labelled {

    val root: GroupParameter = GroupParameter("taskRoot", label = "", description = description)

    /**
     * When used on a command line, this parameter can be used without using its parameter name.
     * It is good practice to place a "--" argument before the unnamed arguments to avoid
     * unnamed arguments beginning with "--" being interpreted as regular (named) arguments.
     */
    var unnamedParameter: ValueParameter<*>? = null

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { root.add(it) }
    }

    fun removeParameters(vararg parameters: Parameter) {
        parameters.forEach { root.remove(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    fun copyValuesFrom(source: TaskDescription) {
        for (sourceParameter in source.root.descendants()) {
            if (sourceParameter is ValueParameter<*>) {
                val stringValue = sourceParameter.stringValue
                val destParameter = root.find(sourceParameter.name)
                if (destParameter is ValueParameter<*>) {
                    destParameter.stringValue = stringValue
                }
            }
        }
    }

    override fun toString(): String {
        return "TaskDescription ${name}"
    }

    fun toVerboseString(): String {
        val result = StringBuilder()
        result.appendln(toString())
        for (parameter in root.descendants()) {
            result.appendln(parameter)
        }
        return result.toString()
    }
}