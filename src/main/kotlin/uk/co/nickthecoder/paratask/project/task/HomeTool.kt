package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.uncamel

class HomeTool() : AbstractTableTool<Tool>() {

    override val taskD = TaskDescription("home", description = "Lists available Tools")

    override fun createColumns() {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name.uncamel() })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }

    override fun run() {
        list.clear()
        list.addAll(TaskRegistry.allTools())
    }

}


fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
