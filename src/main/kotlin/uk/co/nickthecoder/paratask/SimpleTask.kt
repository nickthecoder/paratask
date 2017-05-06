package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.ValueParameter

abstract class SimpleTask() : Task {

    override fun check() {
    }

    fun dumpValues() {

        taskD.root.descendants().forEach { parameter ->
            if (parameter is ValueParameter<*>) {
                println("Parameter ${parameter.name} = ${parameter.value} ('${parameter.stringValue}')")
            }
        }
    }
}
