package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.parameter.Values

class ThreadedToolRunner(val tool: Tool) : ToolRunner {

    private var started: Boolean = false

    private var finished: Boolean = false

    private var thread: Thread? = null

    override fun run(values: Values) {
        started = true
        finished = false
        thread = object : Thread() {
            override fun run() {
                tool.run(values);
                Platform.runLater {
                    tool.updateResults()

                    finished = true
                    thread = null
                }
            }
        }
        thread?.start()
    }

    override fun hasStarted() = started

    override fun hasFinished() = finished

    override fun isRunning() = thread != null
}
