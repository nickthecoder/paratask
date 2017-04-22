package uk.co.nickthecoder.paratask.gui

import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameter.IntParameter

class IntControl(val parameter: IntParameter) {

    private lateinit var field: Field

    public fun createField(): Field {

        val spinner = createSpinner()
        field = Field(parameter.name, spinner)

        spinner.valueFactory.converter = parameter.converter;
        spinner.editableProperty().set(true)

        spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, {
            if (it.code == KeyCode.UP) {
                try {
                    spinner.increment(1);
                } catch (e: Exception) {
                    // Do nothing when editor contains an invalid numbers
                }
                it.consume()
            } else if (it.code == KeyCode.DOWN) {
                try {
                    spinner.decrement(1);
                } catch (e: Exception) {
                    // Do nothing when editor contains an invalid numbers
                }
                it.consume()
            }
        })

        @Suppress("UNUSED_PARAMETER")
        spinner.editor.textProperty().addListener({ o: Any?, oldValue: String, newValue: String ->
            try {
                val v = parameter.converter.fromString(newValue)
                spinner.valueFactory.value = v
                field.clearError()
            } catch (e: ParameterException) {
                field.showError(e)
            }
        })

        return field
    }

    private fun createSpinner(): Spinner<*> {
        val initialValue = if (parameter.value == null && parameter.required) {
            if (parameter.range.start > 0) {
                parameter.range.start
            } else if (parameter.range.endInclusive < 0) {
                parameter.range.endInclusive
            } else {
                0
            }

        } else {
            parameter.value
        }

        val spinner = Spinner(IntSpinnerValueFactory(parameter.range, initialValue))
        parameter.value = spinner.valueFactory.value
        spinner.valueFactory.valueProperty().bindBidirectional(parameter.property);
        return spinner
    }

    class IntSpinnerValueFactory(
            val min: Int = Integer.MIN_VALUE,
            val max: Int = Integer.MAX_VALUE,
            initialValue: Int? = null,
            val amountToStepBy: Int = 1)
        : SpinnerValueFactory<Int?>() {

        constructor(range: IntRange, initialValue: Int? = null, amountToStepBy: Int = 1)
                : this(range.start, range.endInclusive, initialValue, amountToStepBy) {
        }

        init {

            value = initialValue

            @Suppress("UNUSED_PARAMETER")
            valueProperty().addListener { o: Any?, oldValue: Int?, newValue: Int? ->
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
            val newValue: Int = add(value, -steps * amountToStepBy);
            value = if (newValue >= min) newValue else if (isWrapAround()) max else min
        }

        override fun increment(steps: Int) {
            val newValue: Int = add(value, steps * amountToStepBy);
            value = if (newValue <= max) newValue else if (isWrapAround()) min else max;
        }
    }

}