package uk.co.nickthecoder.paratask

import javafx.application.Application

/**
 * Use to process osCommand line arguments, and then run the Tool.
 */
class ToolParser(val tool: Tool) : TaskParser(tool) {
    override fun runOrPrompt() {
        ParaToolApp.startTool = tool
        Application.launch(ParaToolApp::class.java)
    }
}
