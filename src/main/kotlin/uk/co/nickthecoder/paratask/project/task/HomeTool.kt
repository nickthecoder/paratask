package uk.co.nickthecoder.paratask.project.task

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.EmptyResults
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool

class HomeTool() : AbstractTool(HomeTask()) {

    companion object {
        val toolList = mutableListOf<Tool>(
                HomeTool(), GrepTool(), TerminalTool(), PythonTool()
        )

        fun add(vararg tools: Tool) {
            tools.forEach { tool ->
                toolList.add(tool)
            }
        }
    }

    override fun run(values: Values) {
    }

    override fun updateResults() {

        val results = HomeResults()

        toolList.forEach { tool ->
            val imageView = tool.icon?.let { ImageView(it) }
            val button = Button(tool.shortTitle(), imageView)
            button.onAction = EventHandler {
                toolPane?.halfTab?.changeTool(tool.copy())
            }

            val description = tool.task.taskD.description
            if (description != "") {
                button.tooltip = Tooltip(description)
            }
            results.node.children.add(button)
        }

        toolPane?.updateResults(results)
    }

    class HomeResults : EmptyResults() {

        override val node = FlowPane()

        override fun chooseFocus(toolPane: ToolPane): Node {
            if (node.children.count() > 0) {
                return node.children.get(0)
            } else {
                return super.chooseFocus(toolPane)
            }
        }
    }
}

class HomeTask : SimpleTask() {

    override val taskD = TaskDescription("home")

    override fun run(value: Values) {}
}

// TODO Remove this once we can test Grep as a tool using GrepTask as the entry point.
fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
