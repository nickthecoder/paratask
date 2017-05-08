package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.project.TaskRunner
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner

abstract class AbstractTask() : Task {

    override open var taskRunner: TaskRunner = ThreadedTaskRunner(this)

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