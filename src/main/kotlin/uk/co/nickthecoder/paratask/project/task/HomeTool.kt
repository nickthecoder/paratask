package uk.co.nickthecoder.paratask.project.task

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.EmptyResults
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.TableHomeTool

class HomeTool() : AbstractTool() {

    override val taskD = TaskDescription("home", description="Lists available Tools")

    companion object {
        val toolList = mutableListOf<Tool>(
                HomeTool(), TerminalTool(), PythonTool(), GroovyTool(), WebTool(), GrepTool(), TableHomeTool()
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

            val description = tool.taskD.description
            if (description != "") {
                button.tooltip = Tooltip(description)
            }
            results.node.children.add(button)
        }

        toolPane?.updateResults(results)
    }
}

class HomeResults : EmptyResults() {

    override val node = FlowPane()
}

// TODO Remove this once we can test Grep as a tool using GrepTask as the entry point.
fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
