package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column

class HomeTool() : AbstractTool() {

    override val taskD = TaskDescription("home", description = "Lists available Tools")

    companion object {
        val toolList = mutableListOf<Tool>(
                HomeTool(), TerminalTool(), PythonTool(), GroovyTool(), WebTool(), GrepTool()
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
        toolPane?.updateResults(HomeResults(this, toolList))
    }
}

class HomeResults(tool: Tool, list: List<Tool>) : AbstractTableResults<Tool>(tool, list) {

    init {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }
}

fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
