package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.CommandLineTool

class GrepTool() : CommandTaskTerminalTool(GrepTask(), allowInput = false, showCommand = true) {
}

fun main(args: Array<String>) {
    CommandLineTool(GrepTool()).go(args)
}
