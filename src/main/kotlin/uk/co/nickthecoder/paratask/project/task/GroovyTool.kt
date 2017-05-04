package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class GroovyTool : AbstractTerminalTool(allowInput = true, showCommand = true) {

    override val taskD = TaskDescription("groovy", description="Interactive Groovy Shell")

    override fun createCommand(values: Values): Command {
        return Command("groovysh")
        // TODO I tried disabling the colors, but groovysh 2.2.2 throws whenever I use --color argument :-(
    }
}

