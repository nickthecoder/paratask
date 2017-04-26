package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.FileField
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileParameter(
        name: String,
        label: String = name.uncamel(),
        val value: File? = homeDirectory,
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<StringValue>(name = name, label = label, required = required, columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(values: Values): String? = errorMessage(value(values))

    fun errorMessage(v: File?): String? {
        if (required && v == null) {
            return "Required"
        }
        return null
    }

    override fun createField(values: Values): FileField = FileField(this, values)

    override fun createValue() = FileValue(this, value)

    fun parameterValue(values: Values) = values.get(name) as FileValue

    fun value(values: Values) = parameterValue(values).value

    override fun copyValue(source: Values): FileValue {
        val copy = FileValue(this, value(source))
        return copy
    }

    override fun toString(): String {
        return "FileParameter ${name}"
    }

}