package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.editor.EditorTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.uncamel

class HomeTool() : AbstractTableTool<Tool>() {

    override val taskD = TaskDescription("home", description = "Lists available Tools")

    companion object {
        val toolList = mutableListOf<Tool>(
                HomeTool(),
                DirectoryTool(), DirectoryTreeTool(), PlacesTool(),
                TerminalTool(), PythonTool(), GroovyTool(),
                WebTool(), EditorTool(),
                GrepTool(), GitTool(),
                OptionsFilesTool()
        )

        fun add(vararg tools: Tool) {
            tools.forEach { tool ->
                toolList.add(tool)
            }
        }
    }

    override fun createColumns() {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name.uncamel() })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }

    override fun run() {
        list.clear()
        list.addAll(toolList)
    }

}


fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
