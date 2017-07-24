package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.tools.HomeTool

/**
 * Created by nick on 24/07/17.
 */

abstract class ApplicationAction(
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

    val keyCodeCombination: KeyCodeCombination? = if (keyCode == null) {
        null
    } else {
        createKeyCodeCombination(keyCode, shift, control, alt, meta, shortcut)
    }

    abstract val image: Image?

    fun match(event: KeyEvent): Boolean {
        return keyCodeCombination?.match(event) == true
    }

    fun createTooltip(): Tooltip? {
        if (tooltip == null && keyCodeCombination == null) {
            return null
        }

        val result = StringBuilder()
        tooltip?.let { result.append(it) }

        if (tooltip != null && keyCodeCombination != null) {
            result.append(" ")
        }
        keyCodeCombination?.let { result.append(it.displayText) }

        return Tooltip(result.toString())
    }

    fun createButton(shortcuts: ShortcutHelper? = null, action: () -> Unit): Button {

        shortcuts?.add(this, action)

        val button = Button()
        updateButton(button, action)
        return button
    }

    fun createToggleButton(shortcuts: ShortcutHelper? = null, action: () -> Unit): ToggleButton {

        val button = ToggleButton()
        shortcuts?.add(this, { button.isSelected = true; action() })

        updateButton(button, action)
        return button
    }

    private fun updateButton(button: ButtonBase, action: () -> Unit) {
        if (image == null) {
            button.text = label ?: name
        } else {
            button.graphic = ImageView(image)
        }
        if (label != null) {
            button.text = label
        }
        button.onAction = EventHandler {
            action()
        }
        button.tooltip = createTooltip()
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

    companion object {

        fun modifier(down: Boolean?) =
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
    }
}
