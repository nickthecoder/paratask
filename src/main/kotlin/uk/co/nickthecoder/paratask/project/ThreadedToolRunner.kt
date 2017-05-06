package uk.co.nickthecoder.paratask.project

import javafx.application.Platform

open class ThreadedToolRunner(val tool: Tool) : ThreadedTaskRunner(tool) {

    override open fun runTask() {
        super.runTask()

        println("**TTR.runTask")
        Platform.runLater {
            tool.updateResults()

            runState = RunState.FINISHED
        }
    }
}
