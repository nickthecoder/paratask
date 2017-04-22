package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter

abstract class Task(var name: String = "") : Runnable {
    var description: String = ""

    val root: GroupParameter = GroupParameter("taskRoot")

    fun addParameters(vararg parameters: Parameter) {
        parameters.forEach { root.add(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    override fun run() {
        try {
            pre()
            body()
            post()
        } finally {
            tidyUp()
        }
    }

    open fun pre() {}

    abstract fun body()

    open fun post() {}

    open fun tidyUp() {}

}