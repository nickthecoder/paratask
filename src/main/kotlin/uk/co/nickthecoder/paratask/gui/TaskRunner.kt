package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.runAndWait

interface TaskRunner {

    fun run(task: Task, values: Values)

}