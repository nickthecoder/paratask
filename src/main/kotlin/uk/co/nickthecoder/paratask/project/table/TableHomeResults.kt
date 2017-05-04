package uk.co.nickthecoder.paratask.project.table

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.project.Tool

class TableHomeResults(list: List<Tool>) : AbstractTableResults<Tool>(list) {

    init {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }
}
