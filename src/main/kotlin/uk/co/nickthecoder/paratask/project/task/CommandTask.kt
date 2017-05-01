package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

interface CommandTask : Task {

    override fun run(values: Values): Command

}
