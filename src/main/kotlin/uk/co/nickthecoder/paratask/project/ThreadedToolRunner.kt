package uk.co.nickthecoder.paratask.project

import javafx.application.Platform

class ThreadedToolRunner(tool: Tool)

    : AbstractToolRunner(tool) {

    private var thread: Thread? = null

    override fun run() {

        runState = RunState.RUNNING

        thread = object : Thread("ThreadedToolRunner") {
            override fun run() {
                tool.run();

                Platform.runLater {
                    tool.updateResults()

                    runState = RunState.FINISHED
                }
            }
        }
        thread?.setDaemon(true)
        thread?.start()
    }

}
