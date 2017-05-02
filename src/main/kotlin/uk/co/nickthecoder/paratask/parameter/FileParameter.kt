package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.FileField
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: File? = homeDirectory,
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<File?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required,
        columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun createField(values: Values): FileField = FileField(this, parameterValue(values))

    override fun createValue() = FileValue(this, value)

    override fun parameterValue(values: Values) = super.parameterValue(values) as FileValue

    override fun toString() = "File" + super.toString()

}