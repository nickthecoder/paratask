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
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool

object Actions {

    private val nameToActionMap = mutableMapOf<String, Action>()

    // ProjectWindow
    val OPEN_PROJECT = Action("project.open", KeyCode.O, alt = true, tooltip = "Open Project")
    val SAVE_PROJECT = Action("project.save", KeyCode.S, alt = true, tooltip = "Save Project")
    val QUIT = Action("application.quit", KeyCode.Q, alt = true, label = "Quit", tooltip = "Quit Para Task")
    val NEW_WINDOW = Action("window.new", KeyCode.N, control = true, tooltip = "New Window")
    val NEW_TAB = Action("tab.new", KeyCode.T, control = true, label = "New Tab", tooltip = "New Tab")
    val CLOSE_TAB = Action("tab.close", KeyCode.W, control = true, tooltip = "Close Tab")
    val DUPLICATE_TAB = Action("tab.duplicate", KeyCode.D, control = true, tooltip = "Duplicate Tab")

    val SPLIT_TAB_TOGGLE = Action("split.tab.toggle", KeyCode.F3, tooltip = "Split/Unsplit")
    val APPLICATION_ABOUT = Action("application.about", KeyCode.F1, tooltip = "About ParaTask")

    // HalfTab
    val SPLIT_TOOL_TOGGLE = Action("tool.toggleParameters", KeyCode.F9, tooltip = "Show/Hide Parameters")

    val TOOL_STOP = Action("tool.stop", KeyCode.ESCAPE, tooltip = "Stop the Tool")
    val TOOL_RUN = Action("tool.run", KeyCode.F5, tooltip = "(Re) Run the Tool")

    val TOOL_SELECT = Action("tool.select", KeyCode.HOME, control = true, tooltip = "Select a Tool")
    val TOOL_CLOSE = Action("tool.close", KeyCode.W, control = true, tooltip = "Close Tool")

    val HISTORY_BACK = Action("history.back", KeyCode.LEFT, alt = true, tooltip = "Back")
    val HISTORY_FORWARD = Action("history.forward", KeyCode.RIGHT, alt = true, tooltip = "Forward")

    // AbstractTableResults
    val OPTION_RUN = Action("actions.run", KeyCode.ENTER, shift = null)
    val OPTION_PROMPT = Action("actions.run.newWindow", KeyCode.F4, shift = null)

    // FileField
    val UP_DIRECTORY = Action("directory.up", KeyCode.UP, alt = true)
    val COMPLETE_FILE = Action("file.complete", KeyCode.DOWN, alt = true)

    // EditorTool

    val EDIT_FIND = Action("edit.find", KeyCode.F, control = true, tooltip = "Find")
    val EDIT_FIND_NEXT = Action("edit.find.next", KeyCode.G, control = true, tooltip = "Find Next")
    val EDIT_FIND_PREV = Action("edit.find.prev", KeyCode.G, control = true, shift = true, tooltip = "Find Next")

    // General
    val CONTEXT_MENU = Action("context.menu", KeyCode.CONTEXT_MENU)
    val FILE_SAVE = Action("file.save", KeyCode.S, control = true, tooltip = "Save")
    val EDIT_CUT = Action("edit.cut", KeyCode.X, control = true, tooltip = "Cut")
    val EDIT_COPY = Action("edit.copy", KeyCode.C, control = true, tooltip = "Copy")
    val EDIT_PASTE = Action("edit.paste", KeyCode.V, control = true, tooltip = "Paste")
    val EDIT_UNDO = Action("edit.undo", KeyCode.Z, control = true, tooltip = "Undo")
    val EDIT_REDO = Action("edit.redo", KeyCode.Z, shift = true, control = true, tooltip = "Redo")
    val ESCAPE = Action("escape", KeyCode.ESCAPE)

    val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
    val acceleratorUp = KeyCodeCombination(KeyCode.UP)
    val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)


    fun add(action: Action) {
        nameToActionMap.put(action.name, action)
    }
}

private fun modifier(down: Boolean?) =
        if (down == null) {
            KeyCombination.ModifierValue.ANY
        } else if (down) {
            KeyCombination.ModifierValue.DOWN
        } else {
            KeyCombination.ModifierValue.UP
        }

fun createKeyCodeCombination(
        keyCode: KeyCode,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false): KeyCodeCombination {

    return KeyCodeCombination(
            keyCode,
            modifier(shift), modifier(control), modifier(alt), modifier(meta), modifier(shortcut))
}

class Action(
        val name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        val tooltip: String? = null,
        val label: String? = null
) {

    val keyCodeCombination: KeyCodeCombination?

    val image: Image?

    init {
        keyCodeCombination = if (keyCode == null) null else
            createKeyCodeCombination(keyCode, shift, control, alt, meta, shortcut)

        image = ParaTaskApp.imageResource("buttons/${name}.png")

        Actions.add(this)
    }

    fun match(event: KeyEvent): Boolean {
        return keyCodeCombination?.match(event) == true
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

            TaskRegistry.allTools().forEach { tool ->
                val imageView = tool.icon?.let { ImageView(it) }
                val item = MenuItem(tool.shortTitle, imageView)

                item.onAction = EventHandler {
                    action(tool)
                }
                getItems().add(item)
            }

        }
    }

}
