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
package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.gui.ApplicationAction
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.ShortcutParameter
import uk.co.nickthecoder.paratask.tools.HomeTool

class ParataskAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : ApplicationAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = ParaTask.imageResource("buttons/$name.png")

    init {
        ParataskActions.add(this)
    }

    fun createToolButton(shortcuts: ShortcutHelper? = null, action: (Tool) -> Unit): ToolSplitMenuButton {
        shortcuts?.let { it.add(this) { action(HomeTool()) } }

        val split = ToolSplitMenuButton(label ?: "", image, action)
        split.tooltip = createTooltip()

        return split
    }

    fun editTask(): Task = EditShortcut(this)

    private class EditShortcut(val action: ParataskAction) : AbstractTask() {

        override val taskD = TaskDescription("editShortcut",
                description = "You will need to restart the application for new shortcuts to take effect. Sorry.")

        val shortcutP = ShortcutParameter("shortcut")

        init {
            shortcutP.keyCodeCombination = action.keyCodeCombination

            taskD.addParameters(shortcutP)
        }

        override fun run() {
            action.keyCodeCombination = shortcutP.keyCodeCombination
            ParataskActions.save()
        }
    }

    class ToolSplitMenuButton(label: String, icon: Image?, val action: (Tool) -> Unit)
        : SplitMenuButton() {

        init {
            text = label
            graphic = ImageView(icon)
            onAction = EventHandler { action(HomeTool()) }
        }

        init {

            TaskRegistry.home.listTools().forEach { tool ->
                val imageView = tool.icon?.let { ImageView(it) }
                val item = MenuItem(tool.shortTitle, imageView)

                item.onAction = EventHandler {
                    action(tool)
                }
                items.add(item)
            }

        }
    }
}
