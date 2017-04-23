package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription

class EmptySimpleTask() : SimpleTask() {

    override val taskD = TaskDescription()

    override fun run() {}

}