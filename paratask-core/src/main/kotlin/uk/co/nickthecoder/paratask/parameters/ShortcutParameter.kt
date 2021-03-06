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
package uk.co.nickthecoder.paratask.parameters

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.ApplicationAction
import uk.co.nickthecoder.paratask.parameters.fields.ButtonField
import uk.co.nickthecoder.paratask.util.uncamel

class ShortcutParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: KeyCodeCombination? = null)
    : SimpleGroupParameter(name, label, description) {

    val keyP = ChoiceParameter<KeyCode?>("shortcutKey", label = "Key", required = false, value = null).nullableEnumChoices()
    val controlP = BooleanParameter("control", required = false, value = false)
    val shiftP = BooleanParameter("shift", required = false, value = false)
    val altP = BooleanParameter("alt", required = false, value = false)
    val chooseKeyP = ButtonParameter("keyPress", label = "", buttonText = "Click to Pick Key Combination") { onChooseKey(it) }

    private var cachedKCC: KeyCodeCombination? = null

    var keyCodeCombination: KeyCodeCombination?
        get() {
            if (dirty) {
                cachedKCC = keyP.value?.let {
                    ApplicationAction.createKeyCodeCombination(
                            it,
                            control = controlP.value,
                            shift = shiftP.value,
                            alt = altP.value
                    )
                }
                dirty = false
            }
            return cachedKCC
        }
        set(v) {
            keyP.value = v?.code
            controlP.value = modifierToBoolean(v?.control)
            shiftP.value = modifierToBoolean(v?.shift)
            altP.value = modifierToBoolean(v?.alt)
        }

    private var dirty: Boolean = true

    init {
        keyCodeCombination = value
        addParameters(keyP, controlP, shiftP, altP, chooseKeyP)
        keyP.listen { dirty = true }
        controlP.listen { dirty = true }
        shiftP.listen { dirty = true }
        altP.listen { dirty = true }
    }

    private fun modifierToBoolean(mod: KeyCombination.ModifierValue?): Boolean? {
        if (mod == null) {
            return false
        }
        return when (mod) {
            KeyCombination.ModifierValue.ANY -> null
            KeyCombination.ModifierValue.DOWN -> true
            KeyCombination.ModifierValue.UP -> false
        }
    }

    private var keyPressHandler: EventHandler<KeyEvent>? = null

    private fun onChooseKey(buttonField: ButtonField) {

        keyPressHandler = EventHandler { event ->
            if (event.code != KeyCode.SHIFT && event.code != KeyCode.CONTROL && event.code != KeyCode.ALT) {
                keyP.value = event.code
                controlP.value = event.isControlDown
                shiftP.value = event.isShiftDown
                altP.value = event.isAltDown
                buttonField.button?.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressHandler)
            }
        }
        buttonField.button?.addEventFilter(KeyEvent.KEY_PRESSED, keyPressHandler)

    }
}
