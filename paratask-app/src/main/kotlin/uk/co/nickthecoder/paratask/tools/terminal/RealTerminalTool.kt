package uk.co.nickthecoder.paratask.tools.terminal

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.util.process.OSCommand

class RealTerminalTool : AbstractTool() {

    override val taskD = TaskDescription("realTerminal")

    val programP = StringParameter("program", value = "bash")

    val argumentsP = MultipleParameter("arguments") { StringParameter("", required = false) }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    var results: RealTerminalResults? = null

    init {
        taskD.addParameters(programP, argumentsP, directoryP)
    }

    override fun run() {
        Platform.runLater {
            results = RealTerminalResults(this, createCommand())
            toolPane?.replaceResults(createResults(), resultsList)
            results?.start()
        }
    }

    override fun updateResults() {
        // We updated the results in run
    }

    override fun createResults(): List<Results> = singleResults(results!!)


    fun createCommand(): OSCommand {
        val command = OSCommand(programP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value
        return command
    }

}
