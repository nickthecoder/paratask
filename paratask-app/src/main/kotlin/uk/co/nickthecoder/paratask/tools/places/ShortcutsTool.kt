package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.ParataskAction
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.BooleanColumn
import uk.co.nickthecoder.paratask.table.Column

class ShortcutsTool : AbstractTableTool<ParataskAction>() {

    override val taskD = TaskDescription("shortcuts", description = "Keyboard Shortcuts")

    override fun createColumns() {
        columns.add(Column<ParataskAction, ImageView>("icon", label = "") { action -> ImageView(action.image) })
        columns.add(Column<ParataskAction, String>("name") { action -> action.name })
        columns.add(BooleanColumn<ParataskAction>("changed") { action -> action.isChanged() })
        columns.add(Column<ParataskAction, String>("shortcut", width = 300) { action -> action.shortcutString() })
    }

    override fun run() {
        list.clear()
        list.addAll(ParataskActions.nameToActionMap.values)
    }

}