package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.project.ProjectWindow

// TODO Maybe ParaToolApp isn't needed, as we can make the Task the entry point
// Tasks can then have a parameter, or fixed value to determine if the results are to stdout,
// to a TextPane, or to a Tool window.

class ParaToolApp : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return
        }
        val projectWindow = ProjectWindow()
        projectWindow.placeOnStage(stage)

        startTool?.let { tool ->
            projectWindow.addTool(tool)
        }
    }

    companion object {
        var startTool: Tool? = null
    }
}