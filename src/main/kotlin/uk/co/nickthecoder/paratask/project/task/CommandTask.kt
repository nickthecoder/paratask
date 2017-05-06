package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.util.Command

interface CommandTask : Task {

    override fun run(): Command

}
