package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ButtonBase
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent

/**
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
            result.append(" (")
        }
        keyCodeCombination?.let { result.append(it.displayText) }
        if (tooltip != null && keyCodeCombination != null) {
            result.append(")")
        }

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
        shortcuts?.add(this, {
            button.isSelected = !button.isSelected;
            action()
        })

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
