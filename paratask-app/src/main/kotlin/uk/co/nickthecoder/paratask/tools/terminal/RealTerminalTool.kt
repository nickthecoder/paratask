package uk.co.nickthecoder.paratask.tools.terminal

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand

class RealTerminalTool : AbstractTool(), Stoppable {

    override val taskD = TaskDescription("realTerminal")

    val programP = StringParameter("program", value = "sh")

    val argumentsP = MultipleParameter("arguments") { StringParameter("", required = false) }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    var results: RealTerminalResults? = null

    var isStopping: Boolean = false

    init {
        taskD.addParameters(programP, argumentsP, directoryP)
    }

    override fun run() {
        println("Enter run")
        isStopping = false
        results = null

        Platform.runLater {
            results = RealTerminalResults(this, createCommand())
            toolPane?.replaceResults(createResults(), resultsList)
        }
        // Wait for the results to be created (which must be done on the JavaFX thread, and we can't block that.
        while (!isStopping) {
            if (results != null) {
                break
            }
            Thread.sleep(100)
        }
        println("Starting results $results")
        results?.start()

        // Now wait for the process to finish.
        if (isStopping) {
            results?.stop()
        } else {
            println("Waiting for process to finish")
            val exitStatus = results?.waitFor()
            println("Process has finished $exitStatus")
        }

        println("Exit run")
    }

    override fun detaching() {
        super.detaching()
        stop()
    }

    override fun stop() {
        println("Stopping ReakTerminalTool")
        isStopping = true
        results?.stop()
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
