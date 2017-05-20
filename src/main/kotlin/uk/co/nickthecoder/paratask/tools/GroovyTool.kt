package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.util.process.OSCommand

class GroovyTool : AbstractTerminalTool(allowInput = true, showCommand = true) {

    override val taskD = TaskDescription("groovy", description="Interactive Groovy Shell")

    override fun createCommand(): OSCommand {
        return OSCommand("groovysh")
        // TODO I tried disabling the colors, but groovysh 2.2.2 throws whenever I use --color argument :-(
    }
}

