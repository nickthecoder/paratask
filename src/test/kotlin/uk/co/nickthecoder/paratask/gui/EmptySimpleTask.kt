package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription

class EmptySimpleTask(taskD: TaskDescription) : SimpleTask<TaskDescription>(taskD) {

    override fun run() {}

}