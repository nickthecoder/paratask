package uk.co.nickthecoder.paratask.project

import javafx.application.Platform

open class UnthreadedToolRunner(val tool: Tool) : UnthreadedTaskRunner(tool) {

    override open fun runTask() {
        super.runTask()

        Platform.runLater {
            tool.updateResults()
        }
    }
}
