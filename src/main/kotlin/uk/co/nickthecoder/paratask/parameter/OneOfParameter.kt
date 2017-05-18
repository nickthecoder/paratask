package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.field.OneOfForm
import uk.co.nickthecoder.paratask.util.uncamel

class OneOfParameter(
        name: String,
        val required: Boolean = true,
        label: String = name.uncamel(),
        description: String = "",
        val message: String = "Choose",
        value: String? = null)

    : GroupParameter(name, label = label, description = description), PropertyValueParameter<String?> {

    override val expressionProperty = SimpleStringProperty()

    override val converter: StringConverter<String?> = object : StringConverter<String?>() {
        override fun toString(v: String?): String = v ?: ""
        override fun fromString(v: String): String? = if (v == "") null else v
    }

    override val valueProperty = SimpleObjectProperty<String?>()

    init {
        this.value = value
    }


    override fun createField(): OneOfForm {
        val result = OneOfForm(this)
        result.buildContent()
        return result
    }

    override fun errorMessage(): String? {
        return errorMessage(value)
    }

    override fun errorMessage(v: String?): String? {
        if (required && value == null) {
            return "You must choose an item from the list"
        }
        return null
    }

    override fun check() {
        errorMessage()?.let { throw ParameterException(this, it) }

        chosenParameter()?.let { checkChild(it) }
    }

    fun chosenParameter(): Parameter? = if (value == null) null else find(value!!)
}
