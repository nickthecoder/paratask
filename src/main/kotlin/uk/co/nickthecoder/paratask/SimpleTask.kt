package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.parameter.Values

abstract class SimpleTask() : Task {

    override fun check(values: Values) {
    }

    fun dumpValues(values: Values) {

        taskD.root.descendants().forEach { parameter ->
            if (parameter is ValueParameter<*>) {
                if (parameter !is GroupParameter) {
                    val value = values.get(parameter.name)
                    println("Parameter ${parameter.name} = ${value?.value} ('${value?.stringValue}')")
                }
            }
        }
    }
}
