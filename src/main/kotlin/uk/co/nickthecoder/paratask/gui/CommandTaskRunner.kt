package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.Command

abstract class CommandTaskRunner : TaskRunner {

    override fun run(task: Task, values: Values) {
        AutoExit.inc()
        Thread({

            try {
                val result = task.run(values)
                if (result is Command) {
                    processCommand(result)
                }
            } finally {
                AutoExit.dec()
            }
        }).start()
    }

    abstract fun processCommand(command: Command)
}
