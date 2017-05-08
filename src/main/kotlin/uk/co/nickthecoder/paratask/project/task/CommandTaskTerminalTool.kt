package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.util.Command


abstract class CommandTaskTerminalTool(
        val commandTask: CommandTask,
        showCommand: Boolean = true,
        allowInput: Boolean = false)

    : AbstractTerminalTool(showCommand, allowInput) {

    init {
        commandTask.taskRunner = UnthreadedTaskRunner(commandTask)
    }

    override val taskD = commandTask.taskD

    override fun createCommand(): Command {

        return commandTask.run()
    }
}
