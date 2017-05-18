package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.field.OneOfForm
import uk.co.nickthecoder.paratask.util.uncamel

class OneOfParameter(
        name: String,
        val required: Boolean = true,
        label: String = name.uncamel(),
        val message: String = "Choose",
        value: String? = null)

    : GroupParameter(name, label = label), ValueParameter<String?> {

    override val expressionProperty = SimpleStringProperty()

    override var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    override val converter: StringConverter<String?> = object : StringConverter<String?>() {
        override fun toString(v: String?): String = v ?: ""
        override fun fromString(v: String): String? = if (v == "") null else v
    }

    val valueProperty = SimpleStringProperty()

    override var value: String?
        get() = valueProperty.get()
        set(v) {
            valueProperty.set(v)
        }

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
