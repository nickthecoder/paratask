package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner

abstract class AbstractTask() : Task {

    override open val taskRunner = ThreadedTaskRunner(this)

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