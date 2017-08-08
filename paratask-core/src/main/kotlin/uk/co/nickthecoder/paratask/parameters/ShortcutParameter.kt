package uk.co.nickthecoder.paratask.parameters

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import uk.co.nickthecoder.paratask.gui.ApplicationAction
import uk.co.nickthecoder.paratask.util.uncamel

class ShortcutParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: KeyCodeCombination? = null)
    : GroupParameter(name, label, description) {

    val keyP = ChoiceParameter<KeyCode?>("shortcutKey", label = "Key", required = false, value = null).nullableEnumChoices()

    val controlP = BooleanParameter("control", required = false, value = false)
    val shiftP = BooleanParameter("shift", required = false, value = false)
    val altP = BooleanParameter("alt", required = false, value = false)

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
        addParameters(keyP, controlP, shiftP, altP)
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

}
