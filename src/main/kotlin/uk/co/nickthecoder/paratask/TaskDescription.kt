package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values

class TaskDescription(var name: String = "") {

    var description: String = ""

    val root: GroupParameter = GroupParameter("taskRoot", isRoot = true)

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { root.add(it) }
    }

    fun removeParameters(vararg parameters: Parameter) {
        parameters.forEach { root.remove(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    fun createValues(): Values = root.createValue()

    fun copyValues(source: Values): Values = root.copyValues(source)
}