package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.project.TaskRunner
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner

abstract class AbstractTask() : Task {

    override open var taskRunner: TaskRunner = ThreadedTaskRunner(this)

    override fun check() {
    }

    override fun toString(): String {
        return "Task : ${taskD.toString()}"
    }
}
