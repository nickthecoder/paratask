package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool

object Actions {

    private val nameToActionMap = mutableMapOf<String, Action>()

    // ProjectWindow
    val QUIT = Action("application.quit", KeyCode.Q, alt = true, label = "Quit", tooltip = "Quit Para Task")
    val NEW_WINDOW = Action("window.new", KeyCode.N, control = true, label = "New Window", tooltip = "New Window")
    val NEW_TAB = Action("tab.new", KeyCode.T, control = true, label = "New Tab", tooltip = "New Tab")
    val CLOSE_TAB = Action("tab.close", KeyCode.W, control = true, tooltip = "Close Tab")
    val DUPLICATE_TAB = Action("tab.duplicate", KeyCode.D, control = true, tooltip = "Duplicate Tab")

    val SPLIT_TAB_TOGGLE = Action("split.tab.toggle", KeyCode.F3, tooltip = "Split/Unsplit")
    val APPLICATION_ABOUT = Action("application.about", KeyCode.F1, tooltip = "About ParaTask")

    // HalfTab
    val SPLIT_TOOL_TOGGLE = Action("split.tool.toggle", KeyCode.F9, control = true, tooltip = "Show/Hide Parameters")
    val SPLIT_TOOL_CYCLE = Action("split.tool.cycle", KeyCode.F9, tooltip = "Cycle through Views")

    val TOOL_STOP = Action("tool.stop", KeyCode.ESCAPE, tooltip = "Stop the Tool")
    val TOOL_RUN = Action("tool.run", KeyCode.F5, tooltip = "(Re) Run the Tool")

    val TOOL_SELECT = Action("tool.select", KeyCode.HOME, control = true, tooltip = "Select a Tool")
    val TOOL_CLOSE = Action("tool.close", KeyCode.W, control = true, tooltip = "Close Tool")

    val HISTORY_BACK = Action("history.back", KeyCode.LEFT, alt = true, tooltip = "Back")
    val HISTORY_FORWARD = Action("history.forward", KeyCode.RIGHT, alt = true, tooltip = "Forward")

    // AbstractTableResults
    val OPTIONS_RUN = Action("actions.run", KeyCode.ENTER)
    val OPTIONS_RUN_NEW_TAB = Action("actions.run.newTab", KeyCode.ENTER, control = true)
    val OPTIONS_RUN_PROMPT = Action("actions.run.newWindow", KeyCode.F4)

    fun add(action: Action) {
        nameToActionMap.put(action.name, action)
    }
}

private fun modifier(down: Boolean) = if (down) KeyCombination.ModifierValue.DOWN else KeyCombination.ModifierValue.UP

class Action(
        val name: String,
        keyCode: KeyCode?,
        shift: Boolean = false,
        control: Boolean = false,
        alt: Boolean = false,
        meta: Boolean = false,
        shortcut: Boolean = false,
        val tooltip: String? = null,
        val label: String? = null
) {

    val keyCodeCombination: KeyCodeCombination?

    val image: Image?

    init {
        keyCodeCombination = if (keyCode == null) null else
            KeyCodeCombination(
                    keyCode,
                    modifier(shift), modifier(control), modifier(alt), modifier(meta), modifier(shortcut))

        image = ParaTaskApp.imageResource("buttons/${name}.png")

        Actions.add(this)
    }

    fun createTooltip(): Tooltip? {
        if (tooltip == null && keyCodeCombination == null) {
            return null
        }

        var result = StringBuilder()
        tooltip?.let { result.append(it) }

        if (tooltip != null && keyCodeCombination != null) {
            result.append(" ")
        }
        keyCodeCombination?.let { result.append(it.getDisplayText()) }

        return Tooltip(result.toString())
    }

    fun createButton(shortcuts: ShortcutHelper? = null, action: () -> Unit): Button {

        shortcuts?.let { it.add(this, action) }

        val button = Button()
        if (image == null) {
            button.setText(label ?: name)
        } else {
            button.setGraphic(ImageView(image))
        }
        if (label != null) {
            button.setText(label)
        }
        button.onAction = EventHandler {
            action()
        }
        button.tooltip = createTooltip()
        return button
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

            HomeTool.toolList.forEach { tool ->
                val imageView = tool.icon?.let { ImageView(it) }
                val item = MenuItem(tool.shortTitle(), imageView)

                item.onAction = EventHandler {
                    action(tool)
                }
                getItems().add(item)
            }

        }
    }

}
