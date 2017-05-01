package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.parameter.Values

class ThreadedToolRunner(val tool: Tool) : ToolRunner {

    private var started: Boolean = false

    private var finished: Boolean = false

    override fun run(values: Values) {
        started = true
        finished = false
        object : Thread() {
            override fun run() {
                println("ThreadedToolRunner running tool")
                tool.run(values);
                println("ThreadedToolRunner run complete")
                Platform.runLater {
                    val results = tool.createResults()

                    tool.toolPane?.let { it.updateResults(results) }
                    println("ThreadedToolRunner updated results")
                    finished = true
                }
            }
        }.start()
    }

    override fun hasStarted() = started

    override fun hasFinished() = finished
}
