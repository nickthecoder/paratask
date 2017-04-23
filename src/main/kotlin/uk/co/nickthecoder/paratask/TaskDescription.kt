package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter

abstract class TaskDescription(var name: String = "") {

    var description: String = ""

    val root: GroupParameter = GroupParameter("taskRoot")

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { root.add(it) }
    }

    fun removeParameters(vararg parameters: Parameter) {
        parameters.forEach { root.remove(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    /**
     * Check that the parameters are all valid. This must be called before a task is run.
     * If Task's have their own validation, above that supplied by the Parameters themselves, then override
     * this method, and throw a ParameterException if the Parameter are not acceptable.
     * <p>
     * In most cases, this method doesn't need to be overridden.
     * </p>
     */
    open fun check() {
        root.check()
    }

}