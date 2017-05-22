package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.project.TaskRunner
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner

abstract class AbstractTask : Task {

    override val taskRunner : TaskRunner by lazy { ThreadedTaskRunner(this) }

    override fun check() {
        taskD.root.check()
        customCheck()
    }
    override fun customCheck() {}

    override fun toString(): String {
        return "Task : $taskD"
    }
}
