package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription

class NullTask() : AbstractTask() {
    override val taskD = TaskDescription("")

    override fun run() {}
}