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
package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParaTask

/**
 */
open class ApplicationAction(
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

    private val defaultKeyCodeCombination: KeyCodeCombination? =
            keyCode?.let { createKeyCodeCombination(keyCode, shift, control, alt, meta, shortcut) }

    var keyCodeCombination = defaultKeyCodeCombination

    open val image: Image? = ParaTask.imageResource("buttons/$name.png")

    fun revert() {
        keyCodeCombination = defaultKeyCodeCombination
    }

    fun isChanged(): Boolean = keyCodeCombination != defaultKeyCodeCombination

    fun shortcutString(): String = if (keyCodeCombination?.code == null) "" else keyCodeCombination.toString()

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

    fun createMenuItem(shortcuts: ShortcutHelper? = null, action: () -> Unit): MenuItem {
        shortcuts?.add(this, action)

        val menuItem = MenuItem(label)
        menuItem.onAction = EventHandler { action() }
        image?.let { menuItem.graphic = ImageView(it) }
        menuItem.accelerator = keyCodeCombination
        return menuItem
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
            button.isSelected = !button.isSelected
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
