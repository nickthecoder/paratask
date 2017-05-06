package uk.co.nickthecoder.paratask.gui.field

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter

// Note. JavaFX cannot handle null values in Combobox correctly
// See : http://stackoverflow.com/questions/25877323/no-select-item-on-javafx-combobox
// So I've added a "special" value, and made the generic type "ANY?"
// and added a bodgeProperty, which forwards get/sets to the parameter's property

private val FAKE_NULL = "FAKE_NULL"

class ChoiceField<T>(override val parameter: ChoiceParameter<T>) : LabelledField(parameter) {

    private var dirty = false

    val converter = object : StringConverter<Any?>() {

        override fun fromString(label: String): Any? {
            return parameter.getValueForLabel(label) ?: FAKE_NULL
        }

        override fun toString(obj: Any?): String {
            val lab = parameter.getLabelForValue(if (obj === FAKE_NULL) null else obj as T)
            return if (lab == null) "<unknown>" else lab
        }
    }

    val bodgeProperty = object : SimpleObjectProperty <Any?>(FAKE_NULL) {
        override fun get(): Any? = parameter.value ?: FAKE_NULL
        override fun set(value: Any?) {
            if (value === FAKE_NULL) {
                parameter.value = null

            } else {
                parameter.value
            }
        }
    }

    init {
        this.control = createControl()
    }

    private fun createControl(): Node {

        val initialValue = parameter.value

        val comboBox = ComboBox<Any?>()
        comboBox.converter = converter
        comboBox.valueProperty().bindBidirectional(bodgeProperty)

        for (value in parameter.choiceValues()) {
            comboBox.getItems().add(value ?: FAKE_NULL)
        }
        comboBox.setValue(initialValue ?: FAKE_NULL)

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