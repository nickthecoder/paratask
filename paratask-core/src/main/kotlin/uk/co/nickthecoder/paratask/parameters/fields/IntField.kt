/*
ParaTask Copyright (C) 2017  Nick Robinson

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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import uk.co.nickthecoder.paratask.parameters.IntParameter

class IntField(val intParameter: IntParameter) : AbstractLabelledField(intParameter) {

    private var dirty = false

    override fun createControl(): Spinner<*> {
        val spinner = createSpinner()

        spinner.valueFactory.converter = intParameter.converter
        spinner.editableProperty().set(true)

        spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, { event ->
            if (ApplicationActions.SPINNER_INCREMENT.match(event)) {
                try {
                    spinner.increment(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (ApplicationActions.SPINNER_DECREMENT.match(event)) {
                try {
                    spinner.decrement(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (ApplicationActions.ENTER.match(event)) {
                processEnter()
                event.consume()
            }
        })

        spinner.editor.textProperty().addListener({ _, _, newValue: String ->
            try {
                val v = intParameter.converter.fromString(newValue)
                if (intParameter.expression == null) {
                    spinner.valueFactory.value = v
                }
                showOrClearError(intParameter.errorMessage(v))
                dirty = false
            } catch (e: Exception) {
                showError("Not an integer")
                dirty = true
            }
        })

        return spinner
    }

    override fun isDirty(): Boolean = dirty

    /**
     * Spinners normally consume the ENTER key, which means the default button won't be run when ENTER is
     * pressed in a Spinner. My Spinner doesn't need to handle the ENTER key, and therefore, this code
     * re-introduces the expected behaviour of the ENTER key (i.e. performing the default button's action).
     */
    private fun processEnter() {
        val defaultRunnable = control?.scene?.accelerators?.get(ApplicationActions.ENTER.keyCodeCombination)
        defaultRunnable?.let { defaultRunnable.run() }
    }

    private fun createSpinner(): Spinner<*> {

        val spinner = Spinner(IntSpinnerValueFactory(intParameter.range, intParameter.value))
        if (intParameter.expression == null) {
            intParameter.value = spinner.valueFactory.value
        }

        // Grr. I've had to add 2 to the columnCount, because it doesn't work otherwise!
        // It's as if this number is used to calculate the width of the control IGNORING the space that the
        // spinner buttons take up. Useless! I assume this is a bug in Spinner.
        // Assuming a later version of JavaFX fixes this, then we'll need an IF based on version numbers to
        // keep the control approximately the right size. Grrr.
        spinner.editor.prefColumnCount = intParameter.columnCount + 2
        spinner.valueFactory.valueProperty().bindBidirectional(intParameter.valueProperty)
        return spinner
    }

    class IntSpinnerValueFactory(
            val min: Int = Integer.MIN_VALUE,
            val max: Int = Integer.MAX_VALUE,
            initialValue: Int? = null,
            val amountToStepBy: Int = 1)
        : SpinnerValueFactory<Int?>() {

        constructor(range: IntRange, initialValue: Int? = null, amountToStepBy: Int = 1)
                : this(range.start, range.endInclusive, initialValue, amountToStepBy)

        init {

            value = initialValue

            valueProperty().addListener { _, _, newValue: Int? ->
                value = newValue
            }
        }

        private fun add(from: Int?, diff: Int): Int {
            if (from == null) {
                // Set to 0, or min/max if 0 is not within the range
                if (min > 0) {
                    return min
                } else if (max < 0) {
                    return max
                } else {
                    return 0
                }
            } else {
                return from + diff
            }
        }

        override fun decrement(steps: Int) {
            val newValue: Int = add(value, -steps * amountToStepBy)
            value = if (newValue >= min) newValue else if (isWrapAround) max else min
        }

        override fun increment(steps: Int) {
            val newValue: Int = add(value, steps * amountToStepBy)
            value = if (newValue <= max) newValue else if (isWrapAround) min else max
        }
    }

}