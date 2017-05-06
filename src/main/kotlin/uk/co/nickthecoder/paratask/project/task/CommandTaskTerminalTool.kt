package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.util.Command


abstract class CommandTaskTerminalTool(
        val commandTask: CommandTask,
        showCommand: Boolean = true,
        allowInput: Boolean = false)

    : AbstractTerminalTool(showCommand, allowInput) {

    override val taskD = commandTask.taskD

    override fun createCommand(): Command {

        return commandTask.run()
    }
}
