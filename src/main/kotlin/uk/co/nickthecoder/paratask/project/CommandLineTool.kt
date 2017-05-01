package uk.co.nickthecoder.paratask.project

import javafx.application.Application
import uk.co.nickthecoder.paratask.ParaToolApp

/**
 * Use to process command line arguments, and then run the Tool.
 */
class CommandLineTool(val tool: Tool) {
    fun go(args: Array<String>) {

        args.size // To prevent compiler warning

        // TODO LATER Parse command line arguments, and decide if we need to prompt
        ParaToolApp.startTool = tool
        Application.launch(ParaToolApp::class.java);
    }
}