package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values

class NullTask() : SimpleTask() {
    override val taskD = TaskDescription("")

    override fun run(values: Values) {}
}