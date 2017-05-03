package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.parameter.Values

class ThreadedToolRunner(tool: Tool)

    : AbstractToolRunner(tool) {

    private var thread: Thread? = null

    override fun run(values: Values) {

        runState = RunState.RUNNING

        thread = object : Thread("ThreadedToolRunner") {
            override fun run() {
                tool.run(values);

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
