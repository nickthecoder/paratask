package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription

class NullTask() : SimpleTask() {
    override val taskD = TaskDescription("")

    override fun run() {}
}