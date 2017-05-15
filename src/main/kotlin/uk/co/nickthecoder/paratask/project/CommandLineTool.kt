package uk.co.nickthecoder.paratask.project

import javafx.application.Application
import uk.co.nickthecoder.paratask.CommandLineTask
import uk.co.nickthecoder.paratask.ParaToolApp

/**
 * Use to process command line arguments, and then run the Tool.
 */
class CommandLineTool(val tool: Tool) : CommandLineTask(tool) {
    override fun runOrPrompt() {
        ParaToolApp.startTool = tool
        Application.launch(ParaToolApp::class.java)
    }
}
