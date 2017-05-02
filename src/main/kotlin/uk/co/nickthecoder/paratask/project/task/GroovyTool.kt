package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class GroovyTool : AbstractTerminalTool(GroovyTask(), allowInput = true, showCommand = true) {
}

class GroovyTask : AbstractTerminalTask() {

    override val taskD = TaskDescription("groovy")

    override fun command(values: Values): Command {
        return Command("groovysh")
        // TODO I tried disabling the colors, but groovysh 2.2.2 throws whenever I use --color argument :-(
    }
}

