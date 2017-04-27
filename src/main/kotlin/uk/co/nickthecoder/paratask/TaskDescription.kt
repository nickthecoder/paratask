package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.uncamel

class TaskDescription(
        val name: String = "",
        val title: String = name.uncamel(),
        val description: String = "") {

    val root: GroupParameter = GroupParameter("taskRoot", label = "", description = description, isRoot = true)

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { root.add(it) }
    }

    fun removeParameters(vararg parameters: Parameter) {
        parameters.forEach { root.remove(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    fun createValues(): Values = root.createValues()

    fun copyValues(source: Values): Values = root.copyValues(source)
}