/*
ParaTask Copyright (C) 2017  Nick Robinson>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.paratask.tools

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.ParataskAction
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.table.BooleanColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.RowFilter

class ShortcutsTool : ListTableTool<ParataskAction>() {

    override val taskD = TaskDescription("shortcuts", description = "Keyboard Shortcuts")

    override val rowFilter = RowFilter<ParataskAction>(this, columns, ParataskActions.EDIT_COPY)

    init {
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