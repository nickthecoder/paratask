package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.Tool

open class ThreadedToolRunner(val tool: Tool) : ThreadedTaskRunner(tool) {

    override fun runTask() {
        super.runTask()

        Platform.runLater {
            tool.updateResults()
            if (tool?.toolPane?.halfTab?.projectTab?.isSelected() == true) {
                tool?.toolPane?.focusResults()
            }
        }
    }
}
