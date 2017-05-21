package uk.co.nickthecoder.paratask.tools

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.uncamel

class HomeTool : AbstractTableTool<Tool>() {

    override val taskD = TaskDescription("home", description = "Lists available Tools")

    override fun createColumns() {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name.uncamel() })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }

    override fun run() {
        list.clear()
        list.addAll(TaskRegistry.homeTools())
    }

}

fun main(args: Array<String>) {
    ToolParser(HomeTool()).go(args)
}
