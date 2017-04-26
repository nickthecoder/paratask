package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.ComboBox
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceValue
import uk.co.nickthecoder.paratask.parameter.Values

class ChoiceField<T> : LabelledField {

    override val parameter: ChoiceParameter<T>

    val value: ChoiceValue<T>

    private var dirty = false

    constructor(parameter: ChoiceParameter<T>, values: Values) : super(parameter) {
        this.parameter = parameter
        this.value = parameter.parameterValue(values)
        this.control = createControl()
    }

    private fun createControl(): Node {

        val initialValue = value.value

        val comboBox = ComboBox<T>()
        comboBox.converter = value
        comboBox.valueProperty().bindBidirectional(value.property)

        value.keyToValueMap.forEach { (_, value) ->
            comboBox.getItems().add(value)
        }
        comboBox.setValue(initialValue)

        return comboBox
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

}