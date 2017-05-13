package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.CommandLineTool

class GrepTerminalTool() : CommandTaskTerminalTool(GrepTask(), allowInput = false, showCommand = true) {
}

fun main(args: Array<String>) {
    CommandLineTool(GrepTerminalTool()).go(args)
}
