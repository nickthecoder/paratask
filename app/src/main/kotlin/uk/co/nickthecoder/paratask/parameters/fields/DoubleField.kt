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

import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.parameters.DoubleAdaptor
import uk.co.nickthecoder.paratask.parameters.ValueParameter

open class DoubleField(val doubleParameter: ValueParameter<*>, val adaptor : DoubleAdaptor) : LabelledField(doubleParameter) {

    private var dirty = false

    init {
        control = createSpinner()
    }

    protected fun createSpinner(): Node {
        val spinner = createSimpleSpinner()

        spinner.valueFactory.converter = adaptor.converter
        spinner.editableProperty().set(true)

        spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, { event ->
            if (acceleratorUp.match(event)) {
                try {
                    spinner.increment(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (acceleratorDown.match(event)) {
                try {
                    spinner.decrement(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (acceleratorEnter.match(event)) {
                processEnter()
                event.consume()
            }
        })

        spinner.editor.textProperty().addListener({ _, _, newValue: String ->
            try {
                val v = adaptor.converter.fromString(newValue)
                if (doubleParameter.expression == null) {
                    spinner.valueFactory.value = v
                }
                showOrClearError(adaptor.errorMessage(v))
                dirty = false
            } catch (e: Exception) {
                showError("Not a number")
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
        val defaultRunnable = scene?.accelerators?.get(acceleratorEnter)
        defaultRunnable?.let { defaultRunnable.run() }
    }

    private fun createSimpleSpinner(): Spinner<*> {
        val initialValue: Double? = adaptor.initialValue()

        val factory = DoubleSpinnerValueFactory(adaptor.minValue, adaptor.maxValue, initialValue)
        val spinner = Spinner(factory)
        if (doubleParameter.expression == null) {
            adaptor.value = spinner.valueFactory.value
        }

        spinner.valueFactory.valueProperty().bindBidirectional(adaptor.valueProperty)
        return spinner
    }

    class DoubleSpinnerValueFactory(
            val min: Double = Double.MIN_VALUE,
            val max: Double = Double.MAX_VALUE,
            initialValue: Double? = null,
            val amountToStepBy: Double = 1.0)
        : SpinnerValueFactory<Double?>() {

        init {
            value = initialValue

            valueProperty().addListener { _, _, newValue: Double? ->
                value = newValue
            }
        }

        private fun add(from: Double?, diff: Double): Double {
            if (from == null) {
                // Set to 0, or min/max if 0 is not within the range
                if (min > 0) {
                    return min
                } else if (max < 0) {
                    return max
                } else {
                    return 0.0
                }
            } else {
                return from + diff
            }
        }

        override fun decrement(steps: Int) {
            val newValue: Double = add(value, -steps * amountToStepBy)
            value = if (newValue >= min) newValue else if (isWrapAround) max else min
        }

        override fun increment(steps: Int) {
            val newValue: Double = add(value, steps * amountToStepBy)
            value = if (newValue <= max) newValue else if (isWrapAround) min else max
        }
    }

}
