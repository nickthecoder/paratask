package uk.co.nickthecoder.paratask.project.task

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool

class HomeTool() : AbstractTool(NullTask()) {

    companion object {
        val toolList = mutableListOf<Tool>()

        init {
            add(HomeTool(), GrepTool(), TerminalTool())
        }

        fun add(vararg tools: Tool) {
            tools.forEach { tool ->
                toolList.add(tool)
            }
        }
    }

    override fun shortTitle() = "Home"

    override fun iconName() = "home"

    override fun run(values: Values) {

    }

    override fun updateResults() {

        val results = HomeResults()

        toolList.forEach { tool ->
            val button = Button(tool.shortTitle(), tool.createIcon())
            button.onAction = EventHandler {
                toolPane?.halfTab?.projectTab?.projectTabs?.addTool(tool.copy())
            }

            val description = tool.task.taskD.description
            if (description != "") {
                button.tooltip = Tooltip(description)
            }
            results.node.children.add(button)
        }

        toolPane?.updateResults(results)
    }

    class HomeResults : Results {

        override val node = FlowPane()
    }
}


// TODO Remove this once we can test Grep as a tool using GrepTask as the entry point.
fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
