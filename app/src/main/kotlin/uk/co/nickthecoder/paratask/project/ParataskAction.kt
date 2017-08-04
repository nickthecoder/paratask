package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.ApplicationAction
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
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
