package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values

class EmptySimpleTask() : SimpleTask() {

    override val taskD = TaskDescription()

    override fun run(values: Values) {}

}